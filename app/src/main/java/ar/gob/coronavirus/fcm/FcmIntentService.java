package ar.gob.coronavirus.fcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;

import timber.log.Timber;

import static ar.gob.coronavirus.fcm.MyFirebaseMessagingService.registerToken;

public class FcmIntentService extends IntentService {

    private static final String ACTION_FETCH_TOKEN = "com.jfr.homecenter.app.action.FETCH_TOKEN";
    public static final String GSM_TOKEN_INTENT_SERVICE = "FcmIntentService";

    public static void startActionFetchToken(Context context) {
        Intent intent = new Intent(context, FcmIntentService.class);
        intent.setAction(ACTION_FETCH_TOKEN);

        context.startService(intent);
    }

    public FcmIntentService() {
        super(GSM_TOKEN_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FETCH_TOKEN.equals(action)) {

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(task -> {

                            if (!task.isSuccessful()) {
                                Timber.w(task.getException(), "Failed trying to getInstanceId");
                                return;
                            }

                            // Get new Instance ID token
                            String token = task.getResult() != null ? task.getResult().getToken() : null;

                            if (token != null) {
                                // Log and toast
                                Timber.d("FCM - On new instance ID token: %s", token);
                                registerToken(token);
                            }
                        });
            }
        }
    }

}