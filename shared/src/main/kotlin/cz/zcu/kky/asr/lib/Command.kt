package cz.zcu.kky.asr.lib

object Command {

    /**
     * Message requesting / confirming registration of your client
     */
    val REGISTER_CLIENT = 1000

    /**
     * Message your client should send before disconnecting from service
     */
    val UNREGISTER_CLIENT = 1001

    /**
     * Message sent by the service when you don't identify yourself while communicating with the service
     */
    val UNKNOWN_CLIENT = 2000

    /**
     * Message sent by the service when you sent an invalid client identifier
     */
    val INVALID_REGISTRATION = 2001

    /**
     * Message that configures the ASR options for your client
     * Currently capable of configuring:
     *
     * GRAMMAR - adding / removing grammars used by ASR Engines
     */
    val ASR_SET_GRAMMAR = 3000

    /**
     * Message that requests your client to become the one in charge (controlling ASR Engines)
     */
    val ASR_TAKE = 3001

    /**
     * Message that requests your client no longer to be in charge (causing de-initialization of ASR Engines)
     */
    val ASR_RELEASE = 3002

    /**
     * Message containing current state of ASR service
     * @see [ASR.State]
     */
    val ASR_STATE = 3003

    /**
     * Message containing information about an error that occurred
     */
    val ASR_ERROR = 3004

    /**
     * Message containing list of currently available engines to client
     * (does not take into account whether the Engine can be used, for example because of lack of network)
     */
    val ASR_AVAILABLE_CONFIGURATIONS = 3005

    /**
     * Command that configures AsrService to use selected engines
     */
    val ASR_SELECT_CONFIGURATIONS = 3006

    /**
     * Command that starts the initialization process of AsrService
     */
    val ASR_INITIALIZE = 3007

    /**
     * Command that deinitializes all Engines and the whole AsrService
     */
    val ASR_DEINITIALIZE = 3008

    /**
     * Message containing current state of an ASR Engine
     * Sent only to client in charge
     * @see [Engine.State]
     */
    val ASR_ENGINE_STATE = 3100

    /**
     * Message containing energy
     */
    val ASR_ENGINE_ENERGY = 3101

    /**
     * Message containing recognition result of an ASR Engine
     */
    val ASR_RESULT = 3102

    /**
     * TODO
     */
    val ASR_CONTROL_STATE = 3103

    /**
     * Message requesting/confirming start of recognizing (only available while your client is in charge)
     */
    val ASR_START_RECOGNIZING = 3200

    /**
     * Message requesting/confirming stop of recognizing (only available while your client is in charge)
     */
    val ASR_STOP_RECOGNIZING = 3201
}