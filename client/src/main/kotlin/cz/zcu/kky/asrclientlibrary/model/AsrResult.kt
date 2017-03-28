package cz.zcu.kky.asrclientlibrary.model

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrResult(val engineId: String, val text: String, val confidence: Float, val rejected: Boolean)