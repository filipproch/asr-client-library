package cz.zcu.kky.asrclientlibrary.connection

import android.os.Bundle
import cz.zcu.kky.asr.lib.AsrConfiguration
import cz.zcu.kky.asr.lib.RequestData

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */

fun Array<AsrConfiguration>.toBundle(): Bundle {
    val bundle = Bundle()
    bundle.putStringArray(RequestData.ASR_CONFIGURATIONS, this.map { it.id }.toTypedArray())
    return bundle
}

fun grammarBundle(grammarType: String, grammar: String): Bundle {
    val bundle = Bundle()
    bundle.putString(RequestData.GRAMMAR_TYPE, grammarType)
    bundle.putString(RequestData.GRAMMAR, grammar)
    return bundle
}

fun grammarFileBundle(grammarFilePath: String): Bundle {
    val bundle = Bundle()
    bundle.putString(RequestData.GRAMMAR_FILE, grammarFilePath)
    return bundle
}