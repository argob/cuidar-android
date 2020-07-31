package ar.gob.coronavirus.flujos.identificacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.BaseActivity;
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity;
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;

public class IdentificacionActivity extends BaseActivity implements IdentificacionNavegador {
    private static final String LLAVE_ESTA_EDITANDO = "LLAVE_ESTA_EDITANDO";

    private Boolean isEditing;
    public LocalUser localUser = null;

    public static void iniciar(Context context, boolean isEditing) {
        Intent intent = new Intent(context, IdentificacionActivity.class);
        intent.putExtra(LLAVE_ESTA_EDITANDO, isEditing);
        context.startActivity(intent);
    }

    public static void startRemovingStack(Context context) {
        Intent intent = new Intent(context, IdentificacionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private IdentificacionViewModel identificacionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identificacion_activity);

        IdentificacionViewModelFactory factory = new IdentificacionViewModelFactory(this, this);
        identificacionViewModel = new ViewModelProvider(this, factory).get(IdentificacionViewModel.class);
        setBaseViewModel(identificacionViewModel);

        isEditing = getIntent().getBooleanExtra(LLAVE_ESTA_EDITANDO, false);

        iniciarObservers();

        if (savedInstanceState == null) {
            AppBarLayout appBarLayout = findViewById(R.id.app_bar_inicio_identificacion);
            appBarLayout.setVisibility(View.VISIBLE);
            if (!isEditing) {
                IdentificacionDniManualFragment fragment = new IdentificacionDniManualFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, fragment).commit();
            } else {
                identificacionViewModel.obtenerUsuario();
                identificacionViewModel.getUsuarioLiveData().observe(this, usuario -> {
                    IdentificacionActivity.this.localUser = usuario;
                    navegarAIdentificacionTelefonoFragment();
                });

            }
        }
    }

    private void iniciarObservers() {
        identificacionViewModel.getActualizarUsuarioLiveData().observe(this, booleanEventoUnico -> {
            if (booleanEventoUnico.obtenerContenidoSiNoFueLanzado() != null) {
                if (booleanEventoUnico.obtenerConenido()) {
                    final PantallaCompletaDialog dialog = PantallaCompletaDialog.newInstance(
                            getString(R.string.gracias_por_tu_ayuda),
                            getString(R.string.ahora_podemos_continuar),
                            "Continuar",
                            R.drawable.confirmacion_icon_96px
                    );
                    dialog.setAccionBoton(v -> {
                        identificacionViewModel.navegarSiguientePantallaDependiendoDelEstado();
                        dialog.dismiss();
                    });
                    dialog.show(getSupportFragmentManager(), "TAG");
                } else {
                    final PantallaCompletaDialog dialog = PantallaCompletaDialog.newInstance(
                            getString(R.string.hubo_error),
                            getString(R.string.hubo_error_desc),
                            "CERRAR",
                            R.drawable.ic_error
                    );

                    dialog.setAccionBoton(v -> dialog.dismiss());
                    dialog.show(getSupportFragmentManager(), "TAG");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (isEditing) {
            if (currentFragment instanceof IdentificacionTelefonoFragment) {
                finish();
            } else {
                super.onBackPressed();
            }
        } else {
            if (currentFragment instanceof IdentificacionDniConfirmacionDatosFragment) {
                identificacionViewModel.logout();
                super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void navegarAIdentificacionTelefonoFragment() {
        Fragment newFragment = new IdentificacionTelefonoFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void navegarAIdentificacionDireccionCompletaFragment() {
        Fragment newFragment = new IdentificacionDireccionCompletaFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void navegarAIdentificacionConfirmacionDatosFragment() {
        Fragment newFragment = new IdentificacionDniConfirmacionDatosFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void navegarAAutoDiagnosticoActivity() {
        AutodiagnosticoActivity.iniciar(this, false);
        finish();
    }

    @Override
    public void navegarAPantallaPrincipal(boolean mostrarResultado) {
        if (!isEditing) {
            PantallaPrincipalActivity.iniciar(this, mostrarResultado);
            finish();
        } else {
            finish();
        }
    }
}
