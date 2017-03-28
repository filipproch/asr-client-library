package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.AsrConfiguration

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AvailableConfigurationsListEvent(val configurations: MutableList<AsrConfiguration>) : ServiceEvent