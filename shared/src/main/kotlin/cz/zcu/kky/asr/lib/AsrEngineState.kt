package cz.zcu.kky.asr.lib

import android.os.Bundle

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

    fun toBundle(engineId: String, state: Int): Bundle {
        val bundle = Bundle()
        bundle.putString(ResponseData.ENGINE_ID, engineId)
        bundle.putInt(ResponseData.ENGINE_STATE, state)
        return bundle
    }

}