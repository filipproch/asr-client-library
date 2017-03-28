package cz.zcu.kky.asr.lib

import android.os.Bundle

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

/**
 * Command Request Bundle keys
 */
object RequestData {

    val CLIENT_ID = "client_id"

    val GRAMMAR_TYPE = "grammar_type"

    val GRAMMAR = "grammar"

    val GRAMMAR_FILE = "grammar_file"

    var ASR_CONFIGURATIONS = "configurations"

}

/**
 * Command Response codes
 */
object ResponseCode {

    /**
     * Command was executed successfully
     */
    val SUCCESS = 0

    /**
     * Command execution failed, check ResponseData.ERROR_MSG or ResponseData.ERROR_CODE
     */
    val FAILURE = 1

}

/**
 * Command Response Bundle keys
 */
object ResponseData {

    val CLIENT_ID = "client_id"

    val ASR_CONFIGURATIONS = "asr_configurations"
    val ASR_ERROR_MESSAGE = "asr_error_message"

    val ENGINE_ID = "engine"
    val ENGINE_STATE = "engine_state"
    val ENGINE_ENERGY = "engine_energy"

    val RESULT_TEXT = "engine_result"
    val RESULT_CONFIDENCE = "engine_result_confidence"
    val RESULT_REJECTED = "engine_result_rejected"

    val ERROR_CODE = "error_code"
    val ERROR_MSG = "error_msg"

    val RESPONSE_CODE = "response_code"
    val STATE = "state"
}

/**
 * Command Response error codes
 */
object ResponseError {

    val GENERIC_ERROR = 0

    val ASR_CONTROLLED_BY_ANOTHER_CLIENT = 100

    val MISSING_OR_INVALID_ARGUMENTS = 101

    fun errorBundle(errorCode: Int = GENERIC_ERROR, errorMsg: String = ""): Bundle {
        val bundle = Bundle()
        bundle.putInt(ResponseData.ERROR_CODE, errorCode)
        bundle.putString(ResponseData.ERROR_MSG, errorMsg)
        return bundle
    }
}

/**
 * Possible states of ASR Service (broadcasted to all clients)
 */
object AsrServiceState {

    /**
     * Asr Service state is unknown
     */
    val UNKNOWN = -1

    /**
     * ASR Service is available and ready to be claimed by client
     */
    val READY = 0

    /**
     * ASR Service is already claimed by another client
     */
    val LOCKED = 1
}

/**
 * Possible states of ASR (delivered only to controlling client)
 */
object AsrState {

    /**
     * ASR state is UNKNOWN
     */
    val UNKNOWN = -1

    /**
     * ASR is idle, waiting for configuration
     */
    val IDLE = 0

    /**
     * ASR started initialization of all Engines
     */
    val INITIALIZING = 1

    /**
     * All selected ASR Engines are initialized and ready to start recognition
     */
    val READY = 2

    /**
     * All selected ASR Engines are currently recognizing
     */
    val RECOGNIZING = 3

    /**
     * ASR cannot be used
     */
    val UNAVAILABLE = 4
}

/**
 * States of individual ASR Engines
 */
object AsrEngineState {

    /**
     * The engine is not to be used, all resources are released
     */
    val DISABLED = 0

    /**
     * The engine is preparing for recognizing
     */
    val INITIALIZING = 1

    /**
     * The engine is configured and ready to start recognizing
     */
    val READY = 2

    /**
     * When the Engine is currently recognizing
     */
    val RECOGNIZING = 3

    /**
     * When the Engine cannot be used, superior to DISABLED
     */
    val UNAVAILABLE = 4

}