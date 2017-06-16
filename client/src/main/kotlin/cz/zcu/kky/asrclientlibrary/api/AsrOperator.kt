package cz.zcu.kky.asrclientlibrary.api

import android.os.Bundle
import android.os.Messenger
import cz.zcu.kky.asr.lib.AsrCommand
import cz.zcu.kky.asr.lib.AsrEvent
import cz.zcu.kky.asr.lib.model.*
import cz.zcu.kky.asrclientlibrary.event.*
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class AsrOperator internal constructor() {

    private val messenger: ServiceMessenger = ServiceMessenger()

    private val serviceEventSubject = PublishSubject.create<ServiceEvent>()

    private val asrConfigurationsSubject = BehaviorSubject.create<AsrConfigurations>()
    private var asrGrammarsSubject = BehaviorSubject.create<AsrGrammarsChangedEvent>()

    private val serviceRegistrationSubject = BehaviorSubject.createDefault(false)

    private val latestEngineStates = hashMapOf<String, AsrEngineState>()

    init {
        messenger.observeEvent(AsrEvent.EVENT_ASR_CONFIGURATIONS_CHANGED)
                .map {
                    it.data.classLoader = AsrConfiguration::class.java.classLoader
                    it.data.getParcelableArrayList<AsrConfiguration>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrConfigurationsChangedEvent(it) }
                .subscribe {
                    asrConfigurationsSubject.onNext(AsrConfigurations(false, it.configurations))
                    serviceEventSubject.onNext(it)
                }

        messenger.observeEvent(AsrEvent.EVENT_ASR_GRAMMARS_CHANGED)
                .map {
                    it.data.classLoader = AsrGrammar::class.java.classLoader
                    it.data.getParcelableArrayList<AsrGrammar>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrGrammarsChangedEvent(it) }
                .subscribe {
                    asrGrammarsSubject.onNext(it)
                    serviceEventSubject.onNext(it)
                }

        messenger.observeEvent(AsrEvent.EVENT_ASR_ENGINE_RESULT)
                .map {
                    it.data.classLoader = AsrEngineResult::class.java.classLoader
                    it.data.getParcelable<AsrEngineResult>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrEngineResultEvent(it) }
                .subscribe {
                    serviceEventSubject.onNext(it)
                }

        messenger.observeEvent(AsrEvent.EVENT_ASR_ENGINE_ENERGY_LEVEL_CHANGED)
                .map {
                    it.data.classLoader = AsrEngineResult::class.java.classLoader
                    it.data.getParcelable<AsrEngineEnergyLevel>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrEngineEnergyLevelEvent(it) }
                .subscribe {
                    serviceEventSubject.onNext(it)
                }

        messenger.observeEvent(AsrEvent.EVENT_ASR_ENGINE_STATE_CHANGED)
                .map {
                    it.data.classLoader = AsrEngineState::class.java.classLoader
                    it.data.getParcelable<AsrEngineState>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrEngineStateChangedEvent(it) }
                .subscribe {
                    processEngineState(it.state)
                    serviceEventSubject.onNext(it)
                }
        messenger.observeEvent(AsrEvent.EVENT_ASR_ACTIVE_CONFIGURATION_CHANGED)
                .map {
                    AsrActiveConfigurationChangedEvent(
                            configKey = it.data.getString(AsrEvent.FIELD_RESPONSE_OBJECT)
                    )
                }
                .subscribe {
                    serviceEventSubject.onNext(it)
                }

        messenger.observeCommandResponses()
                .map { AsrCommandResponseEvent(it) }
                .subscribe(serviceEventSubject)
    }

    private fun processEngineState(state: AsrEngineState) {
        latestEngineStates[state.engineKey] = state
    }

    internal fun setRemoteMessenger(messenger: Messenger) {
        this.messenger.setRemoteMessenger(messenger)
    }

    // todo: is it really necessary?
    internal fun reset() {
        stop().subscribe({}, {})
    }

    internal fun start(): Observable<Response> {
        return messenger.registerWithService()
                .doOnNext {
                    if (it.success == true) {
                        serviceRegistrationSubject.onNext(true)
                    }
                }
                .map { Response(it.waitingForResponse, it.success, it.error) }
    }

    internal fun stop(): Observable<Response> {
        return messenger.unregisterWithService()
                .doOnSubscribe {
                    serviceRegistrationSubject.onNext(false)
                }
                .map { Response(it.waitingForResponse, it.success, it.error) }
    }

    fun takeControl(): Observable<CommandResponse> {
        return messenger.sendCommand(AsrCommand.CMD_TAKE_CONTROL)
    }

    fun releaseControl(): Observable<CommandResponse> {
        return messenger.sendCommand(AsrCommand.CMD_RELEASE_CONTROL)
    }

    fun observeEvents(): Observable<ServiceEvent> {
        return serviceEventSubject
    }

    fun observeEngineState(): Observable<AsrEngineState> {
        var observable = observeEvents()
                .ofType(AsrEngineStateChangedEvent::class.java)
                .map { it.state }

        if (latestEngineStates.size > 0) {
            observable = observable.startWith(
                    Observable.fromIterable(latestEngineStates.map { it.value })
            )
        }

        return observable
    }

    fun observeClientRegistered(): Observable<Boolean> {
        return serviceRegistrationSubject
    }

    fun requestLoadConfiguration(configId: String): Observable<CommandResponse> {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, AsrControlCommand.loadConfiguration(configId))
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
    }

    fun requestReleaseConfiguration(): Observable<CommandResponse> {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, AsrControlCommand.releaseConfiguration())
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
    }

    fun sendAsrControlCommand(controlCommand: AsrControlCommand): Observable<CommandResponse> {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, controlCommand)
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
    }

    fun observeAsrConfigurations(): Observable<AsrConfigurations> {
        return asrConfigurationsSubject
                .startWith(AsrConfigurations(true, null))
    }

    fun observeAsrGrammars(): Observable<AsrGrammarsChangedEvent> {
        return asrGrammarsSubject
    }

}
