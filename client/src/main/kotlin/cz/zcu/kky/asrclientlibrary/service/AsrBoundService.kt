package cz.zcu.kky.asrclientlibrary.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import cz.zcu.kky.asrclientlibrary.connection.ServiceConnection

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
        startForeground(FOREGROUND_NOTIFICATION, createOngoingNotification())

        ServiceConnection.connect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            ServiceConnection.close(this)
        } catch (e: Exception) {
        }

        stopForeground(true)
    }

    abstract fun createOngoingNotification(): Notification

    companion object {
        val FOREGROUND_NOTIFICATION = 1002
    }

}