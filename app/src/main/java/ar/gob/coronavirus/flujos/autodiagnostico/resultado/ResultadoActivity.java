package ar.gob.coronavirus.flujos.autodiagnostico.resultado;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.flujos.BaseActivity;

public class ResultadoActivity extends BaseActivity {
    private static final String LLAVE_OPCION_NAVEGACION = "LLAVE_OPCION_NAVEGACION";

    public static void iniciar(Context context, OpcionesNavegacion opcion) {
        Intent intent = new Intent(context, ResultadoActivity.class);
        intent.putExtra(LLAVE_OPCION_NAVEGACION, opcion);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autodiagnostico_resultado);

        TextView btnAceptarResultado = findViewById(R.id.btnAceptarResultado);
        ImageView imgCerrar = findViewById(R.id.imgCerrar);

        imgCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAceptarResultado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        OpcionesNavegacion opcion = (OpcionesNavegacion) getIntent().getSerializableExtra(LLAVE_OPCION_NAVEGACION);
        Fragment fragment = CirculacionFragment.newInstance(opcion);
        agregarFragment(fragment);
    }

    private void agregarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_fragment_resultado, fragment)
                .commit();
    }

    public enum OpcionesNavegacion {
        RESULTADO_VERDE, RESULTADO_ROSA
    }
}
