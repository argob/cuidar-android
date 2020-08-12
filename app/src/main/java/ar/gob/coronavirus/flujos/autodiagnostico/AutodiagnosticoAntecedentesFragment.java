package ar.gob.coronavirus.flujos.autodiagnostico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;
import org.koin.androidx.viewmodel.compat.SharedViewModelCompat;

import java.util.ArrayList;
import java.util.List;

import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents;
import ar.gob.coronavirus.databinding.FragmentAutodiagnosticoAntecedentesBinding;
import kotlin.Unit;

public class AutodiagnosticoAntecedentesFragment extends Fragment {
    private FragmentAutodiagnosticoAntecedentesBinding binding = null;
    private AutodiagnosticoViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAutodiagnosticoAntecedentesBinding.inflate(inflater, container, false);

        viewModel = SharedViewModelCompat.getSharedViewModel(this, AutodiagnosticoViewModel.class);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        iniciarInterfaz();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            viewModel.pasoActual.postValue(AutodiagnosticoAntecedentesFragmentArgs.fromBundle(getArguments()).getPasoActual());
        }

        iniciarAntecedentes();
        iniciarValoresDeVistas();
        viewModel.obtenerInformacionDeUsuario();
    }

    private void iniciarAntecedentes() {
        if (viewModel.noTieneAntecedentes()) {
            for (Antecedents value : Antecedents.values()) {
                viewModel.agregarAntecedente(createAntecedent(value));
            }
        }
    }

    private void iniciarValoresDeVistas() {
        List<AntecedentElement> elements = new ArrayList<>();
        for (Antecedents value : Antecedents.values()) {
            elements.add(new AntecedentElement(value, obtainPreviousValue(value)));
        }
        AntecedentsAdapter adapter = new AntecedentsAdapter(elements, (antecedents, newValue) -> {
            viewModel.modificarAntecedente(antecedents.getId(), newValue);
            return Unit.INSTANCE;
        });
        binding.antecedentsRecyclerView.setAdapter(adapter);
    }

    private boolean obtainPreviousValue(Antecedents antecedent) {
        RemoteAntecedents previousValue = viewModel.obtenerAntecedente(antecedent.getId());
        return previousValue != null && previousValue.getValue();
    }

    private void iniciarInterfaz() {
        binding.btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(
                        AutodiagnosticoAntecedentesFragmentDirections.
                                actionAutodiagnosticoAntecedentesFragmentToAutodiagnosticoConfirmacionFragment()
                );
            }
        });
    }

    @NotNull
    private RemoteAntecedents createAntecedent(Antecedents antecedent) {
        return new RemoteAntecedents(
                antecedent.getId(),
                getString(antecedent.getShortText()),
                false
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
