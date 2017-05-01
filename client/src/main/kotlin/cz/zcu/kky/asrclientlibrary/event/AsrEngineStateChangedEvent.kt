package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.model.AsrEngineState

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrEngineStateChangedEvent(val state: AsrEngineState) : ServiceEvent