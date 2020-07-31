package ar.gob.coronavirus.flujos;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ar.gob.coronavirus.R;

import androidx.appcompat.app.AppCompatActivity;

public class ErrorGenericoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_generico);
        TextView viewBoton= findViewById(R.id.btn_action_pantalla_completa_dialog);
        viewBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorGenericoActivity.this.finishAffinity();
            }
        });
    }
}
