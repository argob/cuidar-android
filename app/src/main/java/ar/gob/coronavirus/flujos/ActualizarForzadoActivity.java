package ar.gob.coronavirus.flujos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.utils.Constantes;

public class ActualizarForzadoActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent pantallaActualizar = new Intent(context, ActualizarForzadoActivity.class);
        pantallaActualizar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(pantallaActualizar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogo_pantalla_completa);

        TextView viewTitulo = findViewById(R.id.txt_titulo_pantalla_completa_dialog);
        viewTitulo.setText(R.string.dialog_version_title);
        TextView viewMensaje = findViewById(R.id.txt_mensaje_pantalla_completa_dialog);
        viewMensaje.setText(R.string.dialog_version_message);
        ImageView viewIcono = findViewById(R.id.pantalla_completa_logo);
        viewIcono.setImageDrawable(getResources().getDrawable(R.drawable.ic_error));
        TextView viewBoton = findViewById(R.id.btn_action_pantalla_completa_dialog);
        viewBoton.setText(R.string.actualizar);
        viewBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.PREFIJO_PLAYSTORE + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_PREFIJO_PLAYSTORE + appPackageName)));
                }
                finishAffinity();
            }
        });
    }
}