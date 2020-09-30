package ar.gob.coronavirus

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import ar.gob.coronavirus.di.startKoin
import ar.gob.coronavirus.fcm.FcmIntentService
import ar.gob.coronavirus.flujos.ForceUpdateActivity
import ar.gob.coronavirus.flujos.GenericErrorActivity
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity
import ar.gob.coronavirus.utils.Constantes
import ar.gob.coronavirus.utils.SharedUtils
import org.koin.android.ext.android.inject
import timber.log.Timber
import timber.log.Timber.DebugTree
import kotlin.system.exitProcess

class CovidApplication : Application() {

    val sharedUtils by inject<SharedUtils>()

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        Thread.setDefaultUncaughtExceptionHandler { _, e -> handleException(e) }
        createAllNotificationsChannels()
        FcmIntentService.startActionFetchToken(this)

        GlobalActionsManager.subscribe { action: GlobalAction ->
            when (action) {
                GlobalAction.FORCE_UPDATE -> ForceUpdateActivity.start(this)
                GlobalAction.LOGOUT -> IdentificacionActivity.startRemovingStack(this)
                GlobalAction.INVALID_REFRESH_TOKEN -> IdentificacionActivity.startAndShowInvalidLogin(this)
            }
        }
        startKoin(this)
    }

    private fun handleException(e: Throwable) {
        Timber.e(e)
        val intent = Intent(applicationContext, GenericErrorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        exitProcess(1)
    }

    private fun createAllNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService<NotificationManager>()
            notificationManager?.createNotificationChannel(createChannel(Constantes.GENERALES_ID, Constantes.GENERALES_NAME))
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createChannel(id: String, name: String): NotificationChannel {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        return NotificationChannel(id, name, importance)
    }

    companion object {
        @JvmStatic
        lateinit var instance: CovidApplication
            private set
    }
}