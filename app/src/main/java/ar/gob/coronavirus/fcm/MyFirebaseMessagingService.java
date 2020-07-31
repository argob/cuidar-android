package ar.gob.coronavirus.fcm;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import ar.gob.coronavirus.CovidApplication;
import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.EncryptedDataBase;
import ar.gob.coronavirus.data.remoto.Api;
import ar.gob.coronavirus.data.remoto.CovidRetrofit;
import ar.gob.coronavirus.data.remoto.interceptores.HeadersInterceptor;
import ar.gob.coronavirus.flujos.inicio.InicioActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.PreferencesManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static void registerToken(final String token) {
        CovidApplication.getInstance().getSharedUtils().putString(Constantes.SHARED_KEY_PUSH, token);

        String login = PreferencesManager.INSTANCE.getRefreshToken();
        if (!TextUtils.isEmpty(login)) {
            compositeDisposable.add(EncryptedDataBase.getInstance()
                    .getUserDao()
                    .select()
                    .delaySubscription(15, TimeUnit.SECONDS)
                    .flatMapCompletable(user -> {
                        HeadersInterceptor headersInterceptor = new HeadersInterceptor();
                        CovidRetrofit covidRetrofit = new CovidRetrofit(headersInterceptor);
                        final Api api = new Api(covidRetrofit);
                        return api.registrarPush(user.getDni(), user.getGender(), token);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(compositeDisposable::clear)
                    .subscribe(() -> Timber.d("Push Notification registered"),
                            t -> Timber.d("Push Notification - Usuario error: %s", t.getMessage())));
        }
    }

    public void setNotification(String notificationTitle, String text, String notificationChannel, PendingIntent pendingIntent) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(CovidApplication.getInstance(), notificationChannel)
                        .setSmallIcon(R.drawable.icon)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                        .setContentTitle(notificationTitle)
                        .setContentText(text)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(text))
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        // Set the intent that will fire when the user taps the notification
        if (pendingIntent != null) {
            mBuilder.setContentIntent(pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        String login = PreferencesManager.INSTANCE.getRefreshToken();
        if (!TextUtils.isEmpty(login)) {
            String title = remoteMessage.getData().get("title");
            String text = remoteMessage.getData().get("body");

            Intent notificationIntent = new Intent(this, InicioActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            setNotification(title, text, Constantes.GENERALES_ID, pendingIntent);
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(@NotNull String token) {
        // Log and toast
        Timber.d("FCM - On new token: %s", token);
        registerToken(token);
    }
}