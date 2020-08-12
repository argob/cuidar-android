package ar.gob.coronavirus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ar.gob.coronavirus.di.KoinComponentKt;
import ar.gob.coronavirus.fcm.FcmIntentService;
import ar.gob.coronavirus.flujos.ActualizarForzadoActivity;
import ar.gob.coronavirus.flujos.ErrorGenericoActivity;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.SharedUtils;
import kotlin.Unit;
import timber.log.Timber;

public class CovidApplication extends Application {

    private static CovidApplication instance;
    private SharedUtils sharedUtils;

    public CovidApplication() {
        instance = this;
    }

    public static CovidApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> manejadorDeExcepciones(e));

        sharedUtils = new SharedUtils(this);

        createAllNotificationsChannels();
        FcmIntentService.startActionFetchToken(this);

        GlobalActionsManager.INSTANCE.subscribe(action -> {
            switch (action) {
                case FORCE_UPDATE:
                    ActualizarForzadoActivity.start(this);
                    break;
                case LOGOUT:
                    IdentificacionActivity.startRemovingStack(this);
                    break;
                case INVALID_REFRESH_TOKEN:
                    IdentificacionActivity.startAndPrintDialogOtroDispositivo(this);
                    break;
            }
            return Unit.INSTANCE;
        });
        KoinComponentKt.startKoin(this);
    }

    public void manejadorDeExcepciones(Throwable e) {
        Timber.e(e);
        Intent intent = new Intent(getApplicationContext(), ErrorGenericoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(1);
    }

    public SharedUtils getSharedUtils() {
        return sharedUtils;
    }

    private void createAllNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(createChannel(Constantes.GENERALES_ID, Constantes.GENERALES_NAME));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createChannel(String id, String name) {
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        return new NotificationChannel(id, name, importance);
    }
}
