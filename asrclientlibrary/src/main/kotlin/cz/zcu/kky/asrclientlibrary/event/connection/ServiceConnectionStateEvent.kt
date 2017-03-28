package cz.zcu.kky.asrclientlibrary.event.connection

import cz.zcu.kky.asrclientlibrary.event.ServiceEvent

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
data class ServiceConnectionStateEvent(val state: ConnectionState) : ServiceEvent