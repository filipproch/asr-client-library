package cz.zcu.kky.asrclientlibrary.model

import cz.zcu.kky.asr.lib.AsrEngineState

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngine(val id: String, var lastEnergy: Float = -1F, var state: Int = AsrEngineState.DISABLED)