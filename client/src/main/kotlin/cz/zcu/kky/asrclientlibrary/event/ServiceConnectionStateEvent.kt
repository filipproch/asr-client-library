package cz.zcu.kky.asrclientlibrary.event

import cz.zcu.kky.asrclientlibrary.event.ServiceEvent
import cz.zcu.kky.asrclientlibrary.model.ConnectionState

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class ServiceConnectionStateEvent(val state: ConnectionState) : ServiceEvent