package cz.zcu.kky.asrclientlibrary.connection

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Messenger
import cz.zcu.kky.asrclientlibrary.event.ServiceEvent
import cz.zcu.kky.asrclientlibrary.model.ConnectionState
import cz.zcu.kky.asrclientlibrary.event.ServiceConnectionStateEvent
import cz.zcu.kky.asrclientlibrary.exceptions.AsrServiceUnavailableException
import cz.zcu.kky.asrclientlibrary.util.bindServiceRx
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * TODO
 *
 * @author Filip Prochazka (@filipproch)
 */
object AsrServiceConnection {

    private val MAX_CONNECTION_RETRIES = 5

    private var serviceBound: Boolean = false

    private val serviceConnectionSubject = BehaviorSubject.createDefault(ConnectionState.DISCONNECTED)

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
    fun connect(context: Context): Completable {
        return context.bindServiceRx(serviceConn)
                .retryWhen { errors ->
                    errors.zipWith(Flowable.range(1, MAX_CONNECTION_RETRIES), BiFunction { error: Throwable, _: Int ->
                        error
                    }).flatMap { error ->
                        if (error is AsrServiceUnavailableException) {
                            Flowable.error<Exception>(error)
                        } else {
                            Flowable.timer(3000, TimeUnit.MILLISECONDS)
                        }
                    }
                }
                .andThen(observeConnectionState()
                        .take(1)
                        .filter { it == ConnectionState.CONNECTED }
                        .firstOrError()
                        .toCompletable())
                .andThen(operator.start())
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
                .mergeWith(operator.observe())
    }

    fun observeConnectionState(): Observable<ConnectionState> {
        return serviceConnectionSubject
    }

    /**
     *
     */
    fun close(context: Context): Completable {
        return operator.stop()
                .andThen(unbindService(context))
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
        serviceConnectionSubject.onNext(ConnectionState.CONNECTED)
        serviceBound = true
    }

    private fun onDisconnected() {
        serviceBound = false
        serviceConnectionSubject.onNext(ConnectionState.DISCONNECTED)
        operator.reset()
    }

    val ASR_SERVICE_INTENT_ACTION = "cz.zcu.kky.ASR_SERVICE"
    val ASR_SERVICE_PACKAGE_NAME = "cz.zcu.kky.asrservice"

}