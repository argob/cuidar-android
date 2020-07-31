package ar.gob.coronavirus.utils.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.Window;

import ar.gob.coronavirus.R;

public class LoadingDialog {

    public static Dialog createLoadingDialog(Context context, LayoutInflater inflater) {

        return createLoadingDialogBase(context, R.layout.dialog_loading);
    }

    public static Dialog createLoadingDialogBase(Context context, int layout) {

        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}
