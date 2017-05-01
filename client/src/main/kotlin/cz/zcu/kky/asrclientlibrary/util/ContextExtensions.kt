package cz.zcu.kky.asrclientlibrary.util

import android.content.Context
import android.content.Intent
import cz.zcu.kky.asrclientlibrary.connection.AsrServiceConnection
import cz.zcu.kky.asrclientlibrary.exceptions.AsrClientException
import cz.zcu.kky.asrclientlibrary.exceptions.AsrServiceUnavailableException
import io.reactivex.Completable

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */

fun Context.bindServiceRx(serviceConn: android.content.ServiceConnection): Completable {
    return Completable.create {
        if (AsrServiceConnection.isServiceInstalled(this)) {
            val i = Intent(AsrServiceConnection.ASR_SERVICE_INTENT_ACTION)
            i.`package` = AsrServiceConnection.ASR_SERVICE_PACKAGE_NAME
            val bound = this.bindService(i, serviceConn, Context.BIND_AUTO_CREATE)
            if (!bound) {
                it.onError(AsrClientException("Unable to bind to ASRService, check that the service is installed"))
            } else {
                it.onComplete()
            }
        } else {
            it.onError(AsrServiceUnavailableException())
        }
    }
}