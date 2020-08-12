package ar.gob.coronavirus.flujos.autodiagnostico;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.koin.androidx.viewmodel.compat.ViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.databinding.ActivityAutodiagnosticoBinding;
import ar.gob.coronavirus.flujos.BaseActivity;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity;
import ar.gob.coronavirus.flujos.pantallaprincipal.PantallaPrincipalActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.dialogs.LoadingDialog;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;
import ar.gob.coronavirus.utils.observables.EventoUnico;

public class AutodiagnosticoActivity extends BaseActivity {
    private static final String LLAVE_ORIGEN_PRINCIPAL = "LLAVE_ORIGEN_PRINCIPAL";
    private static final String ERROR_DIALOG_TAG = "ERROR_DIALOG_TAG";
    private AutodiagnosticoViewModel viewModel = null;
    private Dialog loaderDialog;
    private boolean vieneDesdePrincipal = false;
    private NavController navController;

    public static void iniciar(Context context, boolean vieneDesdePrincipal) {
        Intent intent = new Intent(context, AutodiagnosticoActivity.class);
        intent.putExtra(LLAVE_ORIGEN_PRINCIPAL, vieneDesdePrincipal);
        context.startActivity(intent);
    }

    public static void iniciarActividadParaResultado(Activity context, boolean vieneDesdePrincipal) {
        Intent intent = new Intent(context, AutodiagnosticoActivity.class);
        intent.putExtra(LLAVE_ORIGEN_PRINCIPAL, vieneDesdePrincipal);
        context.startActivityForResult(intent, Constantes.CODIGO_DE_PEDIDO_AUTODIAGNOSTICO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAutodiagnosticoBinding binding = ActivityAutodiagnosticoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vieneDesdePrincipal = getIntent().getBooleanExtra(LLAVE_ORIGEN_PRINCIPAL, false);

        viewModel = ViewModelCompat.getViewModel(this, AutodiagnosticoViewModel.class);
        setBaseViewModel(viewModel);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.toolBarAutodiagnostico);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        navController = Navigation.findNavController(this, R.id.nav_autodiagnostico);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(binding.toolBarAutodiagnostico, navController, appBarConfiguration);
        escucharCambioDePantalla();
        reaccionarAlBotonBack();

        loaderDialog = LoadingDialog.createLoadingDialog(this, getLayoutInflater());
    }

    private void escucharCambioDePantalla() {
        viewModel.screenStateLiveData.observe(this, new Observer<AutodiagnosticoViewModel.ScreenState>() {
            @Override
            public void onChanged(AutodiagnosticoViewModel.ScreenState estado) {
                switch (estado) {
                    case SendingToServer:
                        loaderDialog.show();
                        break;
                    case MainScreen:
                        navegarPantallaPuedeCircular();
                        loaderDialog.dismiss();
                        break;
                    case PhoneConfirmation:
                        loaderDialog.dismiss();
                        findViewById(R.id.progress_circles_group).setVisibility(View.GONE);
                        navController.navigate(R.id.action_autodiagnosticoConfirmacionFragment_to_phoneConfirmationDialog);
                        break;
                    case ServerError:
                        loaderDialog.dismiss();
                        showErrorDialog();
                        break;
                }
            }
        });
    }

    private void showErrorDialog() {
        PantallaCompletaDialog.newInstance(
                getString(R.string.hubo_error),
                getString(R.string.hubo_error_desc),
                getString(R.string.cerrar),
                R.drawable.ic_error
        ).show(getSupportFragmentManager(), ERROR_DIALOG_TAG);
    }

    private void navegarPantallaPuedeCircular() {
        if (!vieneDesdePrincipal) {
            PantallaPrincipalActivity.iniciar(this, true);
        }
        setResult(RESULT_OK);
        finish();
    }

    private void reaccionarAlBotonBack() {
        viewModel.estadoAlPresionarBack.observe(this, new Observer<EventoUnico<AutodiagnosticoViewModel.EstadoAlPresionarBack>>() {

            @Override
            public void onChanged(EventoUnico<AutodiagnosticoViewModel.EstadoAlPresionarBack> estado) {
                if (estado.obtenerContenidoSiNoFueLanzado() != null) {
                    if (estado.obtenerConenido() == AutodiagnosticoViewModel.EstadoAlPresionarBack.DebeDiagnosticarse) {
                        viewModel.logout();
                        IdentificacionActivity.startRemovingStack(getApplicationContext());
                    } else if (estado.obtenerConenido() == AutodiagnosticoViewModel.EstadoAlPresionarBack.Diagnosticado) {
                        AutodiagnosticoActivity.super.onBackPressed();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        final NavController controller = Navigation.findNavController(this, R.id.nav_autodiagnostico);
        if (controller.getCurrentBackStackEntry().getArguments().getInt("pasoActual") == 1) {
            viewModel.manejarBotonBack();
        } else {
            super.onBackPressed();
        }
    }
}
