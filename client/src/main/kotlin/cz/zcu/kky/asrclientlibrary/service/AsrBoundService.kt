package cz.zcu.kky.asrclientlibrary.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import cz.zcu.kky.asrclientlibrary.connection.AsrServiceConnection

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

        AsrServiceConnection.connect(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            AsrServiceConnection.close(this)
        } catch (e: Exception) {
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