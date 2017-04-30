package cz.zcu.kky.asrclientlibrary.connection

import android.os.Bundle
import cz.zcu.kky.asr.lib.deprecated.ResponseData

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */

fun Bundle.responseCode(): Int {
    return getInt(ResponseData.RESPONSE_CODE)
}

fun Bundle.clientId(): String {
    return getString(ResponseData.CLIENT_ID)
}

fun Bundle.errorMsg(): String {
    return getString(ResponseData.ERROR_MSG)
}

fun Bundle.state(): Int {
    return getInt(ResponseData.STATE)
}

fun Bundle.asrError(): String {
    return getString(ResponseData.ASR_ERROR_MESSAGE)
}