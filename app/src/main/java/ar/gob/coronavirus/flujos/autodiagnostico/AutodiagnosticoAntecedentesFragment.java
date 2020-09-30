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

import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.data.remoto.modelo_autodiagnostico.RemoteAntecedents;
import ar.gob.coronavirus.databinding.FragmentAutodiagnosticoAntecedentesBinding;
import ar.gob.coronavirus.utils.Constantes;
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

        binding.btnSiguiente.setOnClickListener(v -> Navigation.findNavController(v).navigate(
                AutodiagnosticoAntecedentesFragmentDirections.actionAutodiagnosticoAntecedentesFragmentToAutodiagnosticoConfirmacionFragment()
        ));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            viewModel.pasoActual.postValue(AutodiagnosticoAntecedentesFragmentArgs.fromBundle(getArguments()).getPasoActual());
        }

        viewModel.obtenerInformacionDeUsuario();

        viewModel.getUserInformation().observe(getViewLifecycleOwner(), user -> {
            iniciarAntecedentes(user);
            iniciarValoresDeVistas(user);
        });
    }

    private void iniciarAntecedentes(LocalUser user) {
        if (viewModel.noTieneAntecedentes()) {
            for (Antecedents value : Antecedents.values()) {
                //When the user is male an the antecendent = A_EMB, the antecedent is not added to the list.
                if (!(value == Antecedents.A_EMB && user.getGender().equals(Constantes.MASCULINO))) {
                    viewModel.agregarAntecedente(createAntecedent(value));
                }
            }
        }
    }

    private void iniciarValoresDeVistas(LocalUser user) {
        List<AntecedentElement> antecedents = new ArrayList<>();

        for (Antecedents value : Antecedents.values()) {
            //When the user is male an the antecendent = A_EMB, the antecedent is not added to the list.
            if (!(value == Antecedents.A_EMB && user.getGender().equals(Constantes.MASCULINO))) {
                antecedents.add(new AntecedentElement(value, obtainPreviousValue(value)));
            }
        }

        AntecedentsAdapter adapter = new AntecedentsAdapter(antecedents, (antecedent, newValue) -> {
            viewModel.modificarAntecedente(antecedent.getId(), newValue);
            return Unit.INSTANCE;
        });

        binding.antecedentsRecyclerView.setAdapter(adapter);
    }

    private boolean obtainPreviousValue(Antecedents antecedent) {
        RemoteAntecedents previousValue = viewModel.getAntecedent(antecedent);
        return previousValue != null && previousValue.getValue();
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
