package cz.zcu.kky.asr.lib

import android.os.Bundle

/**
 * Command Response error codes
 */
object ResponseError {

    val GENERIC_ERROR = 0

    val ASR_CONTROLLED_BY_ANOTHER_CLIENT = 100

    val MISSING_OR_INVALID_ARGUMENTS = 101

    fun toBundle(errorCode: Int = GENERIC_ERROR, errorMsg: String = ""): Bundle {
        val bundle = Bundle()
        bundle.putInt(ResponseData.ERROR_CODE, errorCode)
        bundle.putString(ResponseData.ERROR_MSG, errorMsg)
        return bundle
    }
}