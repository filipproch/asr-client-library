package cz.zcu.kky.asrclientlibrary.connection

import android.os.*
import cz.zcu.kky.asr.lib.AsrCommand
import cz.zcu.kky.asr.lib.AsrEvent
import cz.zcu.kky.asr.lib.model.AsrCommandResponse
import cz.zcu.kky.asrclientlibrary.exceptions.AsrClientDisconnectedException
import cz.zcu.kky.asrclientlibrary.util.RandomKey
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class ServiceMessenger {

    private var clientMessenger = Messenger(IncomingHandler(this))
    private var clientId: String? = null

    private val incomingEventSubject = PublishSubject.create<IncomingEvent>()
    private var sharedCommandResponseObservable: Observable<AsrCommandResponse>? = null

    private var remoteMessenger: Messenger? = null

    fun setRemoteMessenger(messenger: Messenger) {
        remoteMessenger = messenger
    }

    fun registerWithService(): Completable {
        return Completable.create {
            if (remoteMessenger == null) {
                throw RuntimeException("remoteMessenger = null")
            }

            it.onComplete()
        }
                .andThen(sendCommand(AsrCommand.CMD_INIT_SESSION))
                .flatMapCompletable { waitForClientId(it) }
    }

    fun unregisterWithService(): Completable {
        if (remoteMessenger == null || clientId == null) {
            return Completable.complete()
        }

        return sendCommand(AsrCommand.CMD_TERMINATE_SESSION)
                .toCompletable()
    }

    fun observeEvent(vararg eventCode: Int): Observable<IncomingEvent> {
        return incomingEventSubject
                .filter { eventCode.contains(it.code) }
    }

    fun observeCommandResponses(): Observable<AsrCommandResponse> {
        if (sharedCommandResponseObservable == null) {
            sharedCommandResponseObservable = observeEvent(AsrEvent.EVENT_COMMAND_RESPONSE)
                    .map { it.data.getParcelable<AsrCommandResponse>(AsrEvent.FIELD_RESPONSE_OBJECT) }
                    .share()
        }
        return sharedCommandResponseObservable!!
    }

    fun waitForCommandResponse(commandId: String): Maybe<AsrCommandResponse> {
        return observeCommandResponses()
                .filter { it.commandId == commandId }
                .take(1)
                .firstElement()
    }

    fun sendCommand(code: Int, data: Bundle = Bundle.EMPTY): Single<String> {
        return Single.create<String> {
            if (remoteMessenger == null) {
                it.onError(RuntimeException("remoteMessenger = null, is service connected?"))
                return@create
            }

            val commandId = RandomKey.generate()

            val msg = Message.obtain(null, code)
            val bundle = Bundle()
            bundle.putString(AsrCommand.FIELD_COMMAND_ID, commandId)
            if (clientId !== null) {
                bundle.putString(AsrCommand.FIELD_CLIENT_ID, clientId)
            }
            bundle.putAll(data)

            try {
                msg.data = bundle
                msg.replyTo = clientMessenger
                remoteMessenger?.send(msg)
                it.onSuccess(commandId)
            } catch (e: DeadObjectException) {
                // we are disconnected from the service (unexpectedly)
                it.onError(AsrClientDisconnectedException())
                remoteMessenger = null
            }
        }
    }

    private fun waitForClientId(commandId: String): Completable {
        return observeCommandResponses()
                .filter { it.commandId == commandId }
                .flatMapCompletable {
                    if (it.success) {
                        clientId = it.extras?.getString(AsrCommandResponse.EXTRA_CLIENT_ID)
                        Completable.complete()
                    } else {
                        Completable.error(SessionInitializationFailedException())
                    }
                }
    }

    class IncomingHandler(private val communicator: ServiceMessenger) : Handler() {
        override fun handleMessage(msg: Message) {
            communicator.incomingEventSubject.onNext(IncomingEvent(msg.what, msg.data))
        }
    }

    data class IncomingEvent(val code: Int, val data: Bundle)

}

class SessionInitializationFailedException : RuntimeException()