package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.model.AsrCommandResponse

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrCommandResponseEvent(val response: AsrCommandResponse) : ServiceEvent