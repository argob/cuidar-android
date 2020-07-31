package ar.gob.coronavirus.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import ar.gob.coronavirus.R;

public class ComoObtenerTramiteDialog {

    private Activity activity;
    private Dialog dialog;

    public ComoObtenerTramiteDialog(Activity activity) {
        this.activity = activity;
    }

    public void showDialog() {
        if (activity != null) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.tramite_dialog_layout);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView botonCerrar = dialog.findViewById(R.id.boton_cerrar);
            botonCerrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissDialog();
                }
            });
            dialog.show();
        }
    }

    public void dismissDialog() {
        if (activity != null && dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
