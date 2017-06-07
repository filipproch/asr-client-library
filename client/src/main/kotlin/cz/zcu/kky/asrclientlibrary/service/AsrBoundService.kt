package cz.zcu.kky.asrclientlibrary.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import cz.zcu.kky.asrclientlibrary.api.AsrServiceConnection
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
abstract class AsrBoundService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createOngoingNotification()
        if (notification != null) {
            startForeground(FOREGROUND_NOTIFICATION, createOngoingNotification())
        }

        Timber.v("AsrService - connecting")
        AsrServiceConnection.connect(this)
                .subscribe({
                    Timber.v("AsrService - $it")
                    if (it.success == false) {
                        Timber.e(it.error, "AsrService - connection failed, stopping self")
                        this.stopSelf()
                    }
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            AsrServiceConnection.close(this)
                    .observeOn(AndroidSchedulers.mainThread())
                    .blockingAwait()
        } catch (t: Throwable) {
            // ignore all exceptions
            Timber.e(t)
        }

        stopForeground(true)
    }

    protected open fun createOngoingNotification(): Notification? {
        return null
    }

    companion object {
        val FOREGROUND_NOTIFICATION = 1002
    }

}