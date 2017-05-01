package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.model.AsrEngineResult

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngineResultEvent(val result: AsrEngineResult) : ServiceEvent