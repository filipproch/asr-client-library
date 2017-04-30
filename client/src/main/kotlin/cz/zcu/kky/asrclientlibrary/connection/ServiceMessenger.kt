package cz.zcu.kky.asrclientlibrary.connection

import android.os.*
import cz.zcu.kky.asr.lib.deprecated.Command
import cz.zcu.kky.asr.lib.deprecated.RequestData
import cz.zcu.kky.asr.lib.deprecated.ResponseCode
import cz.zcu.kky.asr.lib.deprecated.ResponseData
import cz.zcu.kky.asrclientlibrary.exceptions.AsrClientDisconnectedException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class ServiceMessenger {

    private var clientMessenger = Messenger(IncomingHandler(this))
    private var clientId: String? = null

    private val incomingCommandListeners: MutableList<IncomingCommandListener> = CopyOnWriteArrayList()

    private var remoteMessenger: Messenger? = null

    fun setRemoteMessenger(messenger: Messenger) {
        remoteMessenger = messenger
    }

    fun registerWithService(): Completable {
        if (remoteMessenger == null) {
            throw RuntimeException("remoteMessenger = null")
        }

        if (clientId != null) {
            return Completable.complete()
        }

        return sendMessage(Command.REGISTER_CLIENT)
                .toObservable<IncomingMessage>()
                .flatMap { observeIncoming(Command.REGISTER_CLIENT) }
                .firstOrError()
                .doOnSuccess { clientId == it.data.clientId() }
                .toCompletable()
    }

    fun unregisterWithService(): Completable {
        if (remoteMessenger == null || clientId == null) {
            return Completable.complete()
        }

        return sendMessage(Command.UNREGISTER_CLIENT)
                .toObservable<IncomingMessage>()
                .flatMap { observeIncoming(Command.UNREGISTER_CLIENT) }
                .firstOrError()
                .timeout(5000, TimeUnit.MILLISECONDS)
                .doFinally { clientId = null }
                .toCompletable()
    }

    fun reset() {
        synchronized(incomingCommandListeners) {
            incomingCommandListeners.forEach {
                it.onDispose()
            }
            incomingCommandListeners.clear()
        }
        remoteMessenger = null
    }

    fun observeIncoming(vararg messageIds: Int): Observable<IncomingMessage> {
        return observeIncomingMessages()
                .filter { messageIds.contains(it.messageId) }
    }

    fun observeIncomingMessages(): Observable<IncomingMessage> {
        return Observable.create {
            val listener = object : IncomingCommandListener {
                override fun onIncomingCommand(command: Int, data: Bundle) {
                    it.onNext(IncomingMessage(command, data))
                }

                override fun onDispose() {
                    it.onComplete()
                }
            }
            synchronized(incomingCommandListeners) {
                incomingCommandListeners.add(listener)
            }
            it.setCancellable {
                synchronized(incomingCommandListeners) {
                    incomingCommandListeners.remove(listener)
                }
            }
        }
    }

    fun waitForMessage(messageId: Int): Single<IncomingMessage> {
        return observeIncoming(messageId)
                .firstOrError()
                .timeout(3000, TimeUnit.MILLISECONDS)
    }

    fun request(messageId: Int, data: Bundle = Bundle.EMPTY): Completable {
        return sendMessage(messageId, data)
                .andThen(waitForMessage(messageId))
                .flatMapCompletable {
                    if (it.data.containsKey(ResponseData.RESPONSE_CODE)) {
                        if (it.data.responseCode() == ResponseCode.FAILURE) {
                            Completable.error { RequestFailed() }
                        }
                    }
                    Completable.complete()
                }
    }

    fun sendMessage(messageId: Int, data: Bundle = Bundle.EMPTY): Completable {
        return Completable.create {
            if (remoteMessenger == null) {
                it.onError(RuntimeException("remoteMessenger = null, is service connected?"))
                return@create
            }

            val msg = Message.obtain(null, messageId)
            val bundle = Bundle()
            if (clientId !== null) {
                bundle.putString(RequestData.CLIENT_ID, clientId)
            }
            bundle.putAll(data)

            try {
                msg.data = bundle
                msg.replyTo = clientMessenger
                remoteMessenger?.send(msg)
                it.onComplete()
            } catch (e: DeadObjectException) {
                // we are disconnected from the service (unexpectedly)
                it.onError(AsrClientDisconnectedException())
            }
        }
    }

    class IncomingHandler(private val communicator: ServiceMessenger) : Handler() {
        override fun handleMessage(msg: Message) {
            synchronized(communicator.incomingCommandListeners) {
                communicator.incomingCommandListeners.forEach {
                    it.onIncomingCommand(msg.what, msg.data)
                }
            }
        }
    }

    data class IncomingMessage(val messageId: Int, val data: Bundle)

    class IncomingError(val data: Bundle) : RuntimeException()

    class RequestFailed : Error()

    interface IncomingCommandListener {
        fun onIncomingCommand(command: Int, data: Bundle)
        fun onDispose()
    }

}
