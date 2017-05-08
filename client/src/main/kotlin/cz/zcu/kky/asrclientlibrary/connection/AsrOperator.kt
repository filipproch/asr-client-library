package cz.zcu.kky.asrclientlibrary.connection

import android.os.Bundle
import android.os.Messenger
import cz.zcu.kky.asr.lib.AsrCommand
import cz.zcu.kky.asr.lib.AsrEvent
import cz.zcu.kky.asr.lib.model.*
import cz.zcu.kky.asrclientlibrary.event.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class AsrOperator {

    private val messenger: ServiceMessenger = ServiceMessenger()

    private val serviceEventSubject = PublishSubject.create<ServiceEvent>()

    private val asrConfigurationsSubject = BehaviorSubject.create<AsrConfigurationsChangedEvent>()
    private var asrGrammarsSubject = BehaviorSubject.create<AsrGrammarsChangedEvent>()

    init {
        messenger.observeEvent(AsrEvent.EVENT_ASR_CONFIGURATIONS_CHANGED)
                .map {
                    it.data.classLoader = AsrConfiguration::class.java.classLoader
                    it.data.getParcelableArrayList<AsrConfiguration>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrConfigurationsChangedEvent(it) }
                .subscribe {
                    asrConfigurationsSubject.onNext(it)
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
                .subscribe(serviceEventSubject)

        messenger.observeEvent(AsrEvent.EVENT_ASR_ENGINE_STATE_CHANGED)
                .map {
                    it.data.classLoader = AsrEngineState::class.java.classLoader
                    it.data.getParcelable<AsrEngineState>(AsrEvent.FIELD_RESPONSE_OBJECT)
                }
                .map { AsrEngineStateChangedEvent(it) }
                .subscribe {
                    serviceEventSubject.onNext(it)
                }

        messenger.observeCommandResponses()
                .map { AsrCommandResponseEvent(it) }
                .subscribe(serviceEventSubject)
    }

    fun start(): Completable {
        return messenger.registerWithService()
    }

    fun stop(): Completable {
        return messenger.unregisterWithService()
    }

    fun setRemoteMessenger(messenger: Messenger) {
        this.messenger.setRemoteMessenger(messenger)
    }

    // todo: is it really necessary?
    fun reset() {
        stop().subscribe({}, {})
    }

    fun observe(): Observable<ServiceEvent> {
        return serviceEventSubject
    }

    fun takeControl(): Completable {
        return messenger.sendCommand(AsrCommand.CMD_TAKE_CONTROL)
                .flatMapCompletable { commandId -> completeOnCommandResponse(commandId) }
    }

    fun releaseControl(): Completable {
        return messenger.sendCommand(AsrCommand.CMD_RELEASE_CONTROL)
                .flatMapCompletable { commandId -> completeOnCommandResponse(commandId) }
    }

    fun requestLoadConfiguration(configId: String): Completable {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, AsrControlCommand.loadConfiguration(configId))
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
                .toCompletable()
    }

    fun requestReleaseConfiguration(): Completable {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, AsrControlCommand.releaseConfiguration())
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
                .toCompletable()
    }

    fun sendAsrControlCommand(controlCommand: AsrControlCommand): Completable {
        val data = Bundle()
        data.putParcelable(AsrCommand.FIELD_ASR_COMMAND, controlCommand)
        return messenger.sendCommand(AsrCommand.CMD_ASR_CONTROL, data)
                .toCompletable()
    }

    fun observeAsrConfigurations(): Observable<AsrConfigurationsChangedEvent> {
        return asrConfigurationsSubject
    }

    fun observeAsrGrammars(): Observable<AsrGrammarsChangedEvent> {
        return asrGrammarsSubject
    }

    private fun completeOnCommandResponse(commandId: String): Completable {
        return messenger.waitForCommandResponse(commandId)
                .flatMapCompletable {
                    if (it.success) {
                        Completable.complete()
                    } else {
                        Completable.error(CommandRejectedException(it.commandId, it.extras))
                    }
                }
    }

/*
    *//*
     * ASR Actions Below
     *//*

    */

    /**
     * Selects desired configurations to be used for recognizing voice
     */
    /*
        fun selectConfigurations(configurations: Array<AsrConfiguration>): Completable {
            return messenger.request(Command.ASR_SELECT_CONFIGURATIONS, configurations.toBundle())
        }

        */
    /**
     * Initializes Asr Service with defined AsrConfigurations
     */
    /*
        fun initializeAsr(): Completable {
            return messenger.request(Command.ASR_INITIALIZE)
        }

        */
    /**
     * Deinitializes Asr Service
     */
    /*
        fun deinitializeAsr(): Completable {
            return messenger.request(Command.ASR_DEINITIALIZE)
        }

        */
    /**
     * Set's grammar to be used by Asr Service
     */
    /*
        fun setGrammar(grammarType: String, grammar: String): Completable {
            return messenger.request(Command.ASR_SET_GRAMMAR, grammarBundle(grammarType, grammar))
        }

        */
    /**
     * Set's grammar file to be used by Asr Service
     * NOTICE: binary files are supported only for configurations which has [AsrConfiguration.isBinaryGrammarFileSupported]
     */
    /*
        fun setGrammarFile(grammarFilePath: String): Completable {
            return messenger.request(Command.ASR_SET_GRAMMAR, grammarFileBundle(grammarFilePath))
        }

        */
    /**
     * Starts recognizing
     */
    /*
        fun startRecognizing(): Completable {
            return messenger.request(Command.ASR_START_RECOGNIZING)
        }

        */
    /**
     * Stops recognizing
     */
    /*
        fun stopRecognizing(): Completable {
            return messenger.request(Command.ASR_STOP_RECOGNIZING)
        }

        */
    /**
     *
     */
    /*
        fun getAvailableConfigurations(): Single<MutableList<AsrConfiguration>> {
            return observeAvailableConfigurations()
                    .lastOrError()
        }

        */
    /**
     *
     */
    /*
        fun observeAvailableConfigurations(): Observable<MutableList<AsrConfiguration>> {
            return observe()
                    .filter { it is AsrConfigurationsChangedEvent }
                    .map { (it as AsrConfigurationsChangedEvent).configurations }
        }

        */
    /**
     *
     */
    /*
        fun observeAsrControlState(): Observable<Int> {
            return observe()
                    .filter { it is ControlStateChangedEvent }
                    .map { (it as ControlStateChangedEvent).controlState }
        }

        */
    /**
     *
     */
    /*
        fun observeAsrState(): Observable<Int> {
            return observe()
                    .filter { it is AsrStateChangedEvent }
                    .map { (it as AsrStateChangedEvent).state }
        }

        */
    /**
     *
     */
    /*
        fun observeAsrErrors(): Observable<String> {
            return observe()
                    .filter { it is AsrErrorEvent }
                    .map { (it as AsrErrorEvent).errorMsg }
        }

        */
    /**
     *
     */
    /*
        fun observeAsrEngines(): Observable<AsrEngineEvent> {
            return observe()
                    .filter { it is AsrEngineEvent }
                    .map { it as AsrEngineEvent }
        }

        */
    /**
     * Observes results returned by Asr Service
     */
    /*
        fun observeAsrResults(): Observable<AsrResult> {
            return observe()
                    .filter { it is AsrEngineResultEvent }
                    .map { (it as AsrEngineResultEvent).result }
        }*/

}

class CommandRejectedException(val commandId: String, val extras: Bundle?) : RuntimeException("Command ($commandId) was rejected by service")
