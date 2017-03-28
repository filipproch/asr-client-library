package cz.zcu.kky.asrclientlibrary.connection

import android.os.Bundle
import android.os.Messenger
import cz.zcu.kky.asr.lib.AsrConfiguration
import cz.zcu.kky.asr.lib.Command
import cz.zcu.kky.asrclientlibrary.event.*
import cz.zcu.kky.asrclientlibrary.event.engine.AsrEngineEnergyChanged
import cz.zcu.kky.asrclientlibrary.event.engine.AsrEngineEvent
import cz.zcu.kky.asrclientlibrary.event.engine.AsrEngineStateChanged
import cz.zcu.kky.asrclientlibrary.model.AsrResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class AsrOperator {

    private val messenger: ServiceMessenger = ServiceMessenger()

    private var sharedObservable: Observable<ServiceEvent>? = null

    fun start(): Observable<ServiceEvent> {
        return messenger.registerWithService()
                .toObservable<ServiceEvent>()
                .flatMap { observe() }
    }

    fun stop(): Completable {
        return messenger.unregisterWithService()
    }

    fun setRemoteMessenger(messenger: Messenger) {
        this.messenger.setRemoteMessenger(messenger)
    }

    // todo: is it really necessary?
    fun reset() {
        sharedObservable = null
        stop().subscribe()
    }

    fun observe(): Observable<ServiceEvent> {
        if (sharedObservable == null) {
            sharedObservable = messenger.observeIncomingMessages()
                    .map {
                        when (it.messageId) {
                            Command.ASR_AVAILABLE_CONFIGURATIONS -> {
                                AvailableConfigurationsListEvent(it.data.availableConfigurations())
                            }
                            Command.ASR_CONTROL_STATE -> {
                                ControlStateChangedEvent(it.data.state())
                            }
                            Command.ASR_STATE -> {
                                AsrStateChangedEvent(it.data.state())
                            }
                            Command.ASR_ENGINE_STATE -> {
                                AsrEngineStateChanged(it.data.engineState())
                            }
                            Command.ASR_ENGINE_ENERGY -> {
                                AsrEngineEnergyChanged(it.data.engineEnergy())
                            }
                            Command.ASR_RESULT -> {
                                AsrResultReceivedEvent(it.data.asrResult())
                            }
                            Command.ASR_ERROR -> {
                                AsrErrorEvent(it.data.asrError())
                            }
                            else -> UnknownEvent(it.messageId)
                        }
                    }
                    .share()
        }
        return sharedObservable!!.replay()
    }

    /*
     * ASR Actions Below
     */

    /**
     *
     */
    fun takeControl(): Completable {
        return messenger.request(Command.ASR_TAKE)
    }

    /**
     *
     */
    fun releaseControl(): Completable {
        return messenger.request(Command.ASR_RELEASE)
    }

    /**
     * Selects desired configurations to be used for recognizing voice
     */
    fun selectConfigurations(configurations: Array<AsrConfiguration>): Completable {
        return messenger.request(Command.ASR_SELECT_CONFIGURATIONS, configurations.toBundle())
    }

    /**
     * Initializes Asr Service with defined AsrConfigurations
     */
    fun initializeAsr(): Completable {
        return messenger.request(Command.ASR_INITIALIZE)
    }

    /**
     * Deinitializes Asr Service
     */
    fun deinitializeAsr(): Completable {
        return messenger.request(Command.ASR_DEINITIALIZE)
    }

    /**
     * Set's grammar to be used by Asr Service
     */
    fun setGrammar(grammarType: String, grammar: String): Completable {
        return messenger.request(Command.ASR_SET_GRAMMAR, grammarBundle(grammarType, grammar))
    }

    /**
     * Set's grammar file to be used by Asr Service
     * NOTICE: binary files are supported only for configurations which has [AsrConfiguration.isBinaryGrammarFileSupported]
     */
    fun setGrammarFile(grammarFilePath: String): Completable {
        return messenger.request(Command.ASR_SET_GRAMMAR, grammarFileBundle(grammarFilePath))
    }

    /**
     * Starts recognizing
     */
    fun startRecognizing(): Completable {
        return messenger.request(Command.ASR_START_RECOGNIZING)
    }

    /**
     * Stops recognizing
     */
    fun stopRecognizing(): Completable {
        return messenger.request(Command.ASR_STOP_RECOGNIZING)
    }

    /**
     *
     */
    fun getAvailableConfigurations(): Single<MutableList<AsrConfiguration>> {
        return observeAvailableConfigurations()
                .lastOrError()
    }

    /**
     *
     */
    fun observeAvailableConfigurations(): Observable<MutableList<AsrConfiguration>> {
        return observe()
                .filter { it is AvailableConfigurationsListEvent }
                .map { (it as AvailableConfigurationsListEvent).configurations }
    }

    /**
     *
     */
    fun observeAsrControlState(): Observable<Int> {
        return observe()
                .filter { it is ControlStateChangedEvent }
                .map { (it as ControlStateChangedEvent).controlState }
    }

    /**
     *
     */
    fun observeAsrState(): Observable<Int> {
        return observe()
                .filter { it is AsrStateChangedEvent }
                .map { (it as AsrStateChangedEvent).state }
    }

    /**
     *
     */
    fun observeAsrErrors(): Observable<String> {
        return observe()
                .filter { it is AsrErrorEvent }
                .map { (it as AsrErrorEvent).errorMsg }
    }

    /**
     *
     */
    fun observeAsrEngines(): Observable<AsrEngineEvent> {
        return observe()
                .filter { it is AsrEngineEvent }
                .map { it as AsrEngineEvent }
    }

    /**
     * Observes results returned by Asr Service
     */
    fun observeAsrResults(): Observable<AsrResult> {
        return observe()
                .filter { it is AsrResultReceivedEvent }
                .map { (it as AsrResultReceivedEvent).result }
    }

}