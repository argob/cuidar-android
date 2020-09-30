package ar.gob.coronavirus.flujos.identificacion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;

import org.koin.androidx.viewmodel.compat.ViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.BaseActivity;
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity;
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity;
import ar.gob.coronavirus.utils.dialogs.Dialogs;
import ar.gob.coronavirus.utils.dialogs.FullScreenDialog;

public class IdentificacionActivity extends BaseActivity {
    private static final String IS_EDIT_KEY = "is_edit";
    private static final String OTHER_DEVICE_KEY = "other_device";

    private Boolean isEditing;
    public LocalUser localUser = null;

    public static void start(Context context, boolean isEditing) {
        Intent intent = new Intent(context, IdentificacionActivity.class);
        intent.putExtra(IS_EDIT_KEY, isEditing);
        context.startActivity(intent);
    }

    public static void startRemovingStack(Context context) {
        Intent intent = new Intent(context, IdentificacionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startAndShowInvalidLogin(Context context) {
        Intent intent = new Intent(context, IdentificacionActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(OTHER_DEVICE_KEY, true);
        context.startActivity(intent);
    }

    private IdentificacionViewModel identificacionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identificacion_activity);

        identificacionViewModel = ViewModelCompat.getViewModel(this, IdentificacionViewModel.class);
        setBaseViewModel(identificacionViewModel);

        isEditing = getIntent().getBooleanExtra(IS_EDIT_KEY, false);

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


    @Override
    protected void onResume() {
        super.onResume();
        boolean showOtherDeviceDialog = getIntent().getBooleanExtra(OTHER_DEVICE_KEY, false);

        if (showOtherDeviceDialog) {
            Dialogs.createMessageDialog(this, R.string.error_logged_other_device, R.string.aceptar, (dialog, which) -> dialog.dismiss());
        }
    }

    private void iniciarObservers() {
        identificacionViewModel.getActualizarUsuarioLiveData().observe(this, booleanEventoUnico -> {
            if (booleanEventoUnico.getOrNull() != null) {
                if (booleanEventoUnico.get()) {
                    FullScreenDialog.newInstance(
                            getString(R.string.gracias_por_tu_ayuda),
                            getString(R.string.ahora_podemos_continuar),
                            getString(R.string.lbl_continue),
                            R.drawable.confirmacion_icon_96px
                    )
                            .setActionListener(identificacionViewModel::navegarSiguientePantallaDependiendoDelEstado)
                            .show(getSupportFragmentManager(), "TAG");
                } else {
                    FullScreenDialog.newInstance(
                            getString(R.string.hubo_error),
                            getString(R.string.hubo_error_desc),
                            getString(R.string.lbl_close),
                            R.drawable.ic_error)
                            .show(getSupportFragmentManager(), "TAG");
                }
            }
        });
        identificacionViewModel.getRegistrarUsuarioLiveData().observe(this, event -> {
            NavegacionFragments navigation = event.get();
            switch (navigation) {
                case AUTODIAGNOSTICO:
                    AutodiagnosticoActivity.iniciar(this, false);
                    finish();
                    break;
                case PRINCIPAL:
                    if (!isEditing) {
                        PantallaPrincipalActivity.iniciar(this, false);
                    }
                    finish();
                    break;
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
            }
            super.onBackPressed();
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

}
