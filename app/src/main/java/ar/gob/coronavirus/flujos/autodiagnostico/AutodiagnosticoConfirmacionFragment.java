package ar.gob.coronavirus.flujos.autodiagnostico;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSelfEvaluation;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteSymptom;
import ar.gob.coronavirus.databinding.FragmentAutodiagnosticoConfirmacionBinding;
import ar.gob.coronavirus.utils.InternetUtileria;
import ar.gob.coronavirus.utils.TipoDePermisoDeUbicacion;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;
import ar.gob.coronavirus.utils.permisos.PermisosUtileria;

public class AutodiagnosticoConfirmacionFragment extends Fragment {
    private FragmentAutodiagnosticoConfirmacionBinding binding = null;
    private AutodiagnosticoViewModel viewModel;
    private AlertDialog confirmacionDialogo = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAutodiagnosticoConfirmacionBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(AutodiagnosticoViewModel.class);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        iniciarInterfaz();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            viewModel.pasoActual.postValue(AutodiagnosticoAntecedentesFragmentArgs.fromBundle(getArguments()).getPasoActual());
            crearDialogo();
        }

        viewModel.obtenerInformacionDeUsuario();
    }

    private void iniciarInterfaz() {
        binding.btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmacionDialogo == null)
                    crearDialogo();
                confirmacionDialogo.show();
            }
        });
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a pantalla de temperatura.
                Navigation.findNavController(v).navigate(
                        AutodiagnosticoConfirmacionFragmentDirections.
                                actionAutodiagnosticoConfirmacionFragmentToAutodiagnosticoTemperaturaFragment()
                );
            }
        });

        RemoteSelfEvaluation autoevaluacion = viewModel.obtenerAutoevaluacion();
        String stringTemperatura = String.format("Mi temperatura es: %s", autoevaluacion.getTemperature());
        binding.tvTemperatura.setText(stringTemperatura);

        List<RemoteSymptom> sintomas = autoevaluacion.getSymptoms();
        ordenarSintomas(sintomas);
        StringBuilder stringBuilderSintomas = new StringBuilder();
        for (RemoteSymptom sintoma : sintomas) {
            if (sintoma.getValue()) {
                if (stringBuilderSintomas.length() == 0) {
                    stringBuilderSintomas.append("Mis síntomas son:");
                }
                stringBuilderSintomas.append("\n");
                stringBuilderSintomas.append(" • ");
                stringBuilderSintomas.append(obtenerResumenSintoma(sintoma));
            }
        }

        if (stringBuilderSintomas.length() == 0) {
            stringBuilderSintomas.append("Sin síntomas");
        }

        binding.tvSintomas.setText(stringBuilderSintomas);

        List<RemoteAntecedents> antecedentes = autoevaluacion.getAntecedents();
        ordenarAntecedentes(antecedentes);
        StringBuilder stringBuilderAntecedentes = new StringBuilder();
        for (RemoteAntecedents antecedente : antecedentes) {
            if (antecedente.getValue()) {
                if (stringBuilderAntecedentes.length() == 0) {
                    stringBuilderAntecedentes.append("Mis antecedentes son:");
                }
                stringBuilderAntecedentes.append("\n");
                stringBuilderAntecedentes.append(" • ");
                stringBuilderAntecedentes.append(antecedente.getDescription());
            }
        }

        if (stringBuilderAntecedentes.length() == 0) {
            stringBuilderAntecedentes.append("Sin antecedentes");
        }

        binding.tvAntecedentes.setText(stringBuilderAntecedentes);
    }

    private void ordenarSintomas(List<RemoteSymptom> sintomas) {
        Collections.sort(sintomas, new Comparator<RemoteSymptom>() {
            @Override
            public int compare(RemoteSymptom sr1, RemoteSymptom sr2) {
                return obtenerPosicionSintoma(sr1).compareTo(obtenerPosicionSintoma(sr2));
            }
        });
    }

    private Integer obtenerPosicionSintoma(RemoteSymptom sintoma) {
        Symptoms tipo = Symptoms.valueOf(sintoma.getId());
        return tipo.ordinal();
    }

    private void ordenarAntecedentes(List<RemoteAntecedents> antecedentes) {
        Collections.sort(antecedentes, new Comparator<RemoteAntecedents>() {
            @Override
            public int compare(RemoteAntecedents ar1, RemoteAntecedents ar2) {
                return obtenerPosicionAntecedente(ar1).compareTo(obtenerPosicionAntecedente(ar2));
            }
        });
    }

    private Integer obtenerPosicionAntecedente(RemoteAntecedents antecedente) {
        Antecedents tipo = Antecedents.valueOf(antecedente.getId());
        return tipo.ordinal();
    }

    private String obtenerResumenSintoma(RemoteSymptom sintoma) {
        Symptoms tipo = Symptoms.valueOf(sintoma.getId());
        return getString(tipo.getShortDescription());
    }

    private void crearDialogo() {
        confirmacionDialogo = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(R.string.autodiagnostico_confirmacion)
                .setMessage(R.string.autodiagnostico_confirmacion_message)
                .setPositiveButton(R.string.enviar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (viewModel.debePedirPermisoDeLocalizacion()) {
                            // Se solicita la ubicación en caso de tener síntomas compatibles para poder
                            // derivar al ciudadano al Centro de Salud más cercano.
                            // Este es el único lugar y momento donde se le requiere la ubicación al usuario.
                            verificarSiPidePermisoLocalizacion();
                        } else {
                            enviarResultadosAutodiagnostico();
                        }
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

    private void crearDialogoInternet() {
        final PantallaCompletaDialog dialog = PantallaCompletaDialog.newInstance(
                getString(R.string.hubo_error),
                getString(R.string.no_hay_internet),
                getString(R.string.cerrar).toUpperCase(),
                R.drawable.ic_error
        );

        dialog.setAccionBoton(new PantallaCompletaDialog.AccionBotonDialogoPantallaCompleta() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show(getParentFragmentManager(), "TAG");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        confirmacionDialogo = null;
        binding = null;
    }

    private void verificarSiPidePermisoLocalizacion() {
        escucharObtieneGeolocalizacion();
        if (PermisosUtileria.revisarPermisoSinSolicitar(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            viewModel.obtenerUbicacionLatLong();
            return;
        }
        escucharDialogoDePermisosDeUbicacion();
        lanzarDialogoPermisosLocalizacion();
    }

    private void escucharDialogoDePermisosDeUbicacion() {
        viewModel.obtenerResultadoDialogoDePermisoDeUbicacionLivaData()
                .observe(getViewLifecycleOwner(), obtenerObservadorDelDialogoCustomDePermisoDeUbicacion());
    }

    private void escucharObtieneGeolocalizacion() {
        viewModel.obtenerGeolocalizacionLivaData()
                .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        enviarResultadosAutodiagnostico();
                    }
                });
    }

    private void lanzarDialogoPermisosLocalizacion() {
        viewModel.lanzarDialogoPermisosLocalizacion(TipoDePermisoDeUbicacion.SOLO_UBICACION);
    }

    @NotNull
    private Observer<Boolean> obtenerObservadorDelDialogoCustomDePermisoDeUbicacion() {
        return new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean tienePermisoDeUbicacion) {
                if (tienePermisoDeUbicacion) {
                    viewModel.obtenerUbicacionLatLong();
                } else {
                    enviarResultadosAutodiagnostico();
                }
            }
        };
    }

    private void enviarResultadosAutodiagnostico() {
        if (InternetUtileria.hayConexionDeInternet(getContext())) {
            viewModel.enviarResultadosAutoevaluacion();
        } else {
            crearDialogoInternet();
        }
    }

}
