package cz.zcu.kky.asrclientlibrary.api

import android.os.*
import cz.zcu.kky.asr.lib.AsrCommand
import cz.zcu.kky.asr.lib.AsrEvent
import cz.zcu.kky.asr.lib.model.AsrCommandResponse
import cz.zcu.kky.asrclientlibrary.exceptions.AsrClientDisconnectedException
import cz.zcu.kky.asrclientlibrary.exceptions.MessengerNotRegisteredException
import cz.zcu.kky.asrclientlibrary.exceptions.SessionInitializationFailedException
import cz.zcu.kky.asrclientlibrary.util.RandomKey
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
internal class ServiceMessenger {

    private var clientMessenger = Messenger(IncomingHandler(this))
    private var clientId: String? = null

    private val incomingEventSubject = PublishSubject.create<IncomingEvent>()
    private var sharedCommandResponseObservable: Observable<AsrCommandResponse>? = null

    private var remoteMessenger: Messenger? = null

    fun setRemoteMessenger(messenger: Messenger) {
        remoteMessenger = messenger
    }

    fun registerWithService(): Observable<CommandResponse> {
        return Completable.create {
            if (remoteMessenger == null) {
                throw RuntimeException("remoteMessenger = null")
            }

            Timber.v("registerWithService() - conditions met, sending command...")
            it.onComplete()
        }
                .andThen(sendCommand(AsrCommand.CMD_INIT_SESSION))
                .doOnNext {
                    if (it.success == true) {
                        clientId = it.data?.getString(AsrCommandResponse.EXTRA_CLIENT_ID)
                        requireNotNull(clientId)
                    } else if (it.success == false) {
                        throw SessionInitializationFailedException()
                    }
                }
                .timeout(5000, TimeUnit.MILLISECONDS)
    }

    fun unregisterWithService(): Observable<CommandResponse> {
        if (remoteMessenger == null || clientId == null) {
            return Observable.error(MessengerNotRegisteredException())
        }

        return sendCommand(AsrCommand.CMD_TERMINATE_SESSION)
    }

    fun observeEvent(vararg eventCode: Int): Observable<IncomingEvent> {
        return incomingEventSubject
                .filter { eventCode.contains(it.code) }
    }

    fun observeCommandResponses(): Observable<AsrCommandResponse> {
        if (sharedCommandResponseObservable == null) {
            sharedCommandResponseObservable = observeEvent(AsrEvent.EVENT_COMMAND_RESPONSE)
                    .map {
                        it.data.classLoader = AsrCommandResponse::class.java.classLoader
                        it.data.getParcelable<AsrCommandResponse>(AsrEvent.FIELD_RESPONSE_OBJECT)
                    }
                    .doOnNext { Timber.v("command response arrived - $it") }
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

    internal fun sendCommand(commandId: String, code: Int, data: Bundle) {
        if (remoteMessenger == null) {
            throw RuntimeException("remoteMessenger = null, is service connected?")
        }

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
        } catch (e: DeadObjectException) {
            // we are disconnected from the service (unexpectedly)
            throw AsrClientDisconnectedException()
            remoteMessenger = null
        }
    }

    fun sendCommand(code: Int, data: Bundle = Bundle.EMPTY): Observable<CommandResponse> {
        return Single.just(RandomKey.generate())
                .flatMapObservable { commandId ->
                    observeCommandResponses().filter { it.commandId == commandId }
                            .take(1)
                            .map { CommandResponse.fromResponse(it) }
                            .doOnSubscribe { sendCommand(commandId, code, data) }
                }
                .onErrorReturn { CommandResponse.fromError(it) }
                .startWith(CommandResponse.WAITING)
    }

    class IncomingHandler(private val communicator: ServiceMessenger) : Handler() {
        override fun handleMessage(msg: Message) {
            communicator.incomingEventSubject.onNext(IncomingEvent(msg.what, msg.data))
        }
    }

}
