package cz.zcu.kky.asrclientlibrary.connection

import android.os.Bundle
import cz.zcu.kky.asr.lib.AsrConfiguration
import cz.zcu.kky.asr.lib.ResponseData
import cz.zcu.kky.asrclientlibrary.model.AsrResult

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

fun Bundle.availableConfigurations(): MutableList<AsrConfiguration> {
    this.classLoader = AsrConfiguration::class.java.classLoader
    return getParcelableArrayList<AsrConfiguration>(ResponseData.ASR_CONFIGURATIONS)
}

fun Bundle.state(): Int {
    return getInt(ResponseData.STATE)
}

fun Bundle.asrError(): String {
    return getString(ResponseData.ASR_ERROR_MESSAGE)
}

fun Bundle.asrResult(): AsrResult {
    return AsrResult(engineId(),
            getString(ResponseData.RESULT_TEXT),
            getFloat(ResponseData.RESULT_CONFIDENCE),
            getBoolean(ResponseData.RESULT_REJECTED))
}

fun Bundle.engineId(): String {
    return getString(ResponseData.ENGINE_ID)
}

fun Bundle.engineEnergy(): Float {
    return getFloat(ResponseData.ENGINE_ENERGY)
}

fun Bundle.engineState(): Int {
    return getInt(ResponseData.ENGINE_STATE)
}