package cz.zcu.kky.asrclientlibrary.event

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class AsrActiveConfigurationChangedEvent(
        val configKey: String?
) : ServiceEvent