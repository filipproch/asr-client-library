package cz.zcu.kky.asr.lib

import android.os.Bundle

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

    fun toBundle(state: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt(ResponseData.STATE, state)
        return bundle
    }

}