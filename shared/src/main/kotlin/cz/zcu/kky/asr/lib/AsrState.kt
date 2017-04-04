package cz.zcu.kky.asr.lib

import android.os.Bundle

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

    fun toBundle(state: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt(ResponseData.STATE, state)
        return bundle
    }

}