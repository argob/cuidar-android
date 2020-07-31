package ar.gob.coronavirus.utils.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import ar.gob.coronavirus.R;

public class ConfirmacionDialog {

    public static void showDialog(Context context, int message, int possitiveButton, final DialogInterface.OnClickListener listenerAceptar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(possitiveButton,  listenerAceptar);
        builder.setCancelable(false);
        builder.show();
    }

}