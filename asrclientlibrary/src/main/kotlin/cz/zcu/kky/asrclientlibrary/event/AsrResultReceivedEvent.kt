package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asrclientlibrary.model.AsrResult

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrResultReceivedEvent(val result: AsrResult): ServiceEvent