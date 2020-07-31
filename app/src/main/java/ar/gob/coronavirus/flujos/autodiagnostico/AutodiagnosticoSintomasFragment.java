package ar.gob.coronavirus.flujos.autodiagnostico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.SintomasRemoto;
import ar.gob.coronavirus.databinding.FragmentAutodiagnosticoSintomasBinding;
import kotlin.Unit;

public class AutodiagnosticoSintomasFragment extends Fragment {
    private FragmentAutodiagnosticoSintomasBinding binding = null;
    private AutodiagnosticoViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAutodiagnosticoSintomasBinding.inflate(inflater, container, false);

        AutoevaluacionViewModelFactory factory = new AutoevaluacionViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(AutodiagnosticoViewModel.class);

        binding.setLifecycleOwner(getViewLifecycleOwner());
        iniciarValoresDeVistas();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            viewModel.pasoActual.postValue(AutodiagnosticoSintomasFragmentArgs.fromBundle(getArguments()).getPasoActual());
        }
    }

    private void iniciarValoresDeVistas() {
        List<SymptomElement> elements = new ArrayList<>();
        for (Symptoms value : Symptoms.values()) {
            SintomasRemoto currentValue = viewModel.obtenerSintoma(value);
            elements.add(new SymptomElement(value, currentValue != null && currentValue.isValor()));
        }
        SymptomsAdapter adapter = new SymptomsAdapter(elements, (symptoms, value) -> {
            viewModel.agregarSintoma(createSymptom(symptoms, value));
            return Unit.INSTANCE;
        });
        binding.symptomsRecyclerView.setAdapter(adapter);

        binding.btnSiguiente.setOnClickListener(v -> {
            Navigation.findNavController(v)
                    .navigate(AutodiagnosticoSintomasFragmentDirections.actionAutodiagnosticoSintomasFragmentToAutodiagnosticoAntecedentesFragment());
        });
    }

    @NotNull
    private SintomasRemoto createSymptom(Symptoms symptom, boolean value) {
        return new SintomasRemoto(
                symptom.getValue(),
                getString(symptom.getTitle()),
                value
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
