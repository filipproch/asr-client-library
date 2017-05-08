package cz.zcu.kky.asrclientlibrary.exceptions

import android.os.Bundle

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
class CommandRejectedException(
        val commandId: String,
        val extras: Bundle?
) : RuntimeException("Command ($commandId) was rejected by service")