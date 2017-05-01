package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asr.lib.model.AsrConfiguration

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrConfigurationsChangedEvent(val configurations: MutableList<AsrConfiguration>) : ServiceEvent