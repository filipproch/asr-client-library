package cz.zcu.kky.asrclientlibrary.connection

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Messenger
import cz.zcu.kky.asrclientlibrary.event.ServiceEvent
import cz.zcu.kky.asrclientlibrary.event.connection.ConnectionState
import cz.zcu.kky.asrclientlibrary.event.connection.ServiceConnectionStateEvent
import cz.zcu.kky.asrclientlibrary.exceptions.AsrServiceUnavailableException
import cz.zcu.kky.asrclientlibrary.util.bindServiceRx
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object ServiceConnection {

    private var serviceBound: Boolean = false

    private val serviceConnectionListeners: MutableList<(ConnectionState) -> Unit> = ArrayList()

    /**
     *
     */
    private var serviceConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            onConnected(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            onDisconnected()
        }
    }

    val operator: AsrOperator = AsrOperator()

    /**
     *
     */
    @Throws(AsrServiceUnavailableException::class)
    fun connect(context: Context): Observable<ServiceEvent> {
        return context.bindServiceRx(serviceConn)
                .toObservable<ServiceEvent>()
                .retryWhen { errors ->
                    errors.flatMap { error ->
                        if (error is AsrServiceUnavailableException) {
                            Observable.error<Exception>(error)
                        } else {
                            Observable.timer(3000, TimeUnit.MILLISECONDS)
                        }
                    }
                }
                .flatMap { observeConnectionState() }
                .map { ServiceConnectionStateEvent(it) as ServiceEvent }
                .mergeWith { operator.start() }
    }

    private fun unbindService(context: Context): Completable {
        return Completable.create {
            if (!serviceBound) {
                it.onComplete()
                return@create
            }

            try {
                context.unbindService(serviceConn)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

    fun observeEvents(): Observable<ServiceEvent> {
        return observeConnectionState()
                .map { ServiceConnectionStateEvent(it) as ServiceEvent }
                .mergeWith { operator.observe() }
    }

    fun observeConnectionState(): Observable<ConnectionState> {
        return Observable.create {
            it.onNext(if (serviceBound) ConnectionState.CONNECTED else ConnectionState.DISCONNECTED)

            val listener = { state: ConnectionState ->
                it.onNext(state)
                if (state == ConnectionState.DISCONNECTED) {
                    it.onComplete()
                }
            }
            synchronized(serviceConnectionListeners) {
                serviceConnectionListeners.add(listener)
            }
            it.setCancellable {
                synchronized(serviceConnectionListeners) {
                    serviceConnectionListeners.remove(listener)
                }
            }
        }
    }

    /**
     *
     */
    fun close(context: Context): Completable {
        return operator.stop()
                .andThen { unbindService(context) }
    }

    /**
     * Checks whether AsrService is available on device
     */
    fun isServiceInstalled(context: Context): Boolean {
        try {
            context.packageManager
                    .getPackageInfo(ASR_SERVICE_PACKAGE_NAME, PackageManager.GET_SERVICES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
    }

    private fun onConnected(service: IBinder?) {
        operator.setRemoteMessenger(Messenger(service))
        invokeConnectionStateListeners(ConnectionState.CONNECTED)
        serviceBound = true
    }

    private fun onDisconnected() {
        serviceBound = false
        invokeConnectionStateListeners(ConnectionState.DISCONNECTED)
        operator.reset()
    }

    private fun invokeConnectionStateListeners(state: ConnectionState) {
        synchronized(serviceConnectionListeners) {
            serviceConnectionListeners.forEach { it.invoke(state) }
        }
    }

    interface ServiceConnectionListener {
        fun onStateChanged(state: Int)
    }

    val ASR_SERVICE_INTENT_ACTION = "cz.zcu.kky.ASR_SERVICE"
    val ASR_SERVICE_PACKAGE_NAME = "cz.zcu.kky.asrservice"

}