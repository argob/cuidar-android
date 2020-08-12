package ar.gob.coronavirus.flujos.autodiagnostico;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.koin.androidx.viewmodel.compat.SharedViewModelCompat;

import java.util.Locale;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.databinding.DialogMedirTemperaturaInformacionBinding;
import ar.gob.coronavirus.databinding.FragmentAutodiagnosticoTemperaturaBinding;


public class AutodiagnosticoTemperaturaFragment extends Fragment {
    private final double LIMITE_INFERIOR = 34.0;
    private final double LIMITE_SUPERIOR = 42.0;
    private FragmentAutodiagnosticoTemperaturaBinding binding = null;
    private AlertDialog infoDialogo = null;
    private AutodiagnosticoViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAutodiagnosticoTemperaturaBinding.inflate(inflater, container, false);
        viewModel = SharedViewModelCompat.getSharedViewModel(this, AutodiagnosticoViewModel.class);
        binding.setViewModel(viewModel);
        crearDialogo();
        binding.setLifecycleOwner(getViewLifecycleOwner());
        iniciarInterfaz();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            viewModel.pasoActual.postValue(AutodiagnosticoTemperaturaFragmentArgs.fromBundle(getArguments()).getPasoActual());
            actualizarTemperatura();
        }
    }

    private void iniciarInterfaz() {
        binding.btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.temperatura < LIMITE_INFERIOR || viewModel.temperatura > LIMITE_SUPERIOR) {
                    String textoError = getString(R.string.temperatura_error_limite, LIMITE_INFERIOR, LIMITE_SUPERIOR);
                    binding.tvTemperaturaActual.setError(textoError);
                } else {
                    viewModel.setTemperatura(viewModel.temperatura);
                    Navigation.findNavController(v).navigate(AutodiagnosticoTemperaturaFragmentDirections.actionAutodiagnosticoTemperaturaFragmentToAutodiagnosticoSintomasFragment());
                }
            }
        });
        binding.tvMasInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialogo.show();
            }
        });
        binding.btnDecrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deshabilitarBotones(viewModel.temperatura);
                if (viewModel.temperatura > LIMITE_INFERIOR) {
                    viewModel.temperatura -= .1;
                    actualizarTemperatura();
                }
            }
        });
        binding.btnDecrementar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (viewModel.temperatura > LIMITE_INFERIOR + 1) {
                    viewModel.temperatura -= 1;
                    actualizarTemperatura();
                    return true;
                }
                return false;
            }
        });
        binding.btnIncrementar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deshabilitarBotones(viewModel.temperatura);
                if (viewModel.temperatura < LIMITE_SUPERIOR) {
                    viewModel.temperatura += .1;
                    actualizarTemperatura();
                }
            }
        });
        binding.btnIncrementar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (viewModel.temperatura < LIMITE_SUPERIOR - 1) {
                    viewModel.temperatura += 1;
                    actualizarTemperatura();
                    return true;
                }
                return false;
            }
        });
        binding.etTemperatura.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String temp = binding.etTemperatura.getText().toString();
                String temp2 = temp.replaceAll(",", "");
                if ((temp.length() - temp2.length()) > 1) {
                    actualizarTemperatura();
                    return;
                }
                temp = temp.replace(',', '.');
                if (temp.isEmpty() || temp2.isEmpty()) {

                    binding.tvTemperaturaActual.setError(getString(R.string.temperatura_vacia_error));
                } else if (Double.parseDouble(temp) < LIMITE_INFERIOR || Double.parseDouble(temp) > LIMITE_SUPERIOR) {
                    deshabilitarBotones(Double.parseDouble(temp));
                    String textoError = getString(R.string.temperatura_error_limite, LIMITE_INFERIOR, LIMITE_SUPERIOR);
                    binding.tvTemperaturaActual.setError(textoError);
                    viewModel.temperatura = Double.parseDouble(temp);
                } else {
                    deshabilitarBotones(Double.parseDouble(temp));
                    binding.tvTemperaturaActual.setError(null);
                    viewModel.temperatura = Double.parseDouble(temp);
                }
            }
        });
    }

    private void deshabilitarBotones(Double temperatura) {
        if (temperatura <= LIMITE_INFERIOR) {

            binding.btnDecrementar.setEnabled(false);
            binding.btnDecrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_quitar_gris));
            binding.btnIncrementar.setEnabled(true);
            binding.btnIncrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
        }
        if (temperatura >= LIMITE_SUPERIOR) {
            binding.btnDecrementar.setEnabled(true);
            binding.btnDecrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_quitar));
            binding.btnIncrementar.setEnabled(false);
            binding.btnIncrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_gris));
        }
        if (temperatura > LIMITE_INFERIOR && temperatura < LIMITE_SUPERIOR) {
            binding.btnIncrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
            binding.btnDecrementar.setEnabled(true);
            binding.btnDecrementar.setImageDrawable(getResources().getDrawable(R.drawable.ic_quitar));
            binding.btnIncrementar.setEnabled(true);
        }
    }

    private void actualizarTemperatura() {
        binding.etTemperatura.setText(String.format(Locale.FRANCE, "%.1f", viewModel.temperatura));
    }

    private void crearDialogo() {
        infoDialogo = new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setView(DialogMedirTemperaturaInformacionBinding.inflate(getLayoutInflater()).getRoot())
                .setPositiveButton(R.string.cerrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
