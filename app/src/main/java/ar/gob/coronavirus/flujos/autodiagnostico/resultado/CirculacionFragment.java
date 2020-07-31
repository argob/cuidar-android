package ar.gob.coronavirus.flujos.autodiagnostico.resultado;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.autodiagnostico.AutoevaluacionViewModelFactory;
import ar.gob.coronavirus.flujos.autodiagnostico.ProvincesEnum;
import ar.gob.coronavirus.utils.strings.PintarSeccionBold;
import ar.gob.coronavirus.utils.strings.SpanFormatter;
import ar.gob.coronavirus.utils.strings.StringUtils;

public class CirculacionFragment extends Fragment {
    private static final String opcionDeNavegacion = "opcionDeNavegacion";
    private ResultadoActivity.OpcionesNavegacion opcionNavegacion;

    public CirculacionFragment() {
        // Required empty public constructor
    }

    private AutodiagnosticoResultadoViewModel viewModel;
    private View view;

    public static CirculacionFragment newInstance(ResultadoActivity.OpcionesNavegacion opcionNavegacion) {
        CirculacionFragment fragment = new CirculacionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(opcionDeNavegacion, opcionNavegacion);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_puede_circular, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            opcionNavegacion = (ResultadoActivity.OpcionesNavegacion) arguments.getSerializable(opcionDeNavegacion);
            if (opcionNavegacion != null) {
                switch (opcionNavegacion) {
                    case RESULTADO_VERDE:
                        view.findViewById(R.id.puede_circular).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.no_puede_circular).setVisibility(View.GONE);
                        break;
                    case RESULTADO_ROSA:
                        view.findViewById(R.id.puede_circular).setVisibility(View.GONE);
                        view.findViewById(R.id.no_puede_circular).setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        view = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider.Factory factory = new AutoevaluacionViewModelFactory();
        viewModel = new ViewModelProvider(requireActivity(), factory).get(AutodiagnosticoResultadoViewModel.class);
        observarUsuario();
        viewModel.cargarUsuario();
    }

    private void observarUsuario() {
        viewModel.usuarioLiveData.observe(getViewLifecycleOwner(), new Observer<LocalUser>() {
            @Override
            public void onChanged(LocalUser usuario) {
                if (opcionNavegacion != null) {
                    switch (opcionNavegacion) {
                        case RESULTADO_VERDE:
                            configurarVistaVerde(usuario);
                            break;
                        case RESULTADO_ROSA:
                            configurarVistaRosada(usuario);
                            break;
                    }
                }
            }
        });
    }

    private void configurarVistaVerde(LocalUser usuario) {
        final TextView txtPuedeCircularDiagnostico = view.findViewById(R.id.txtPuedeCircularDiagnostico);
        String text = getString(R.string.msg_no_tiene_sintomas, usuario.getNames());
        int pintarDesde = usuario.getNames().length() + 2;
        int pintarHasta = text.length();
        Typeface font = ResourcesCompat.getFont(requireActivity(), R.font.roboto_bold);
        SpannableStringBuilder textoPintado = PintarSeccionBold.pintarBold(text, pintarDesde, pintarHasta, font);
        txtPuedeCircularDiagnostico.setText(textoPintado);
    }

    private void configurarVistaRosada(LocalUser usuario) {
        pintarDatosCoep(usuario);
        if (usuario.getCurrentState().getUserStatus() == UserStatus.DERIVED_TO_LOCAL_HEALTH) {
            pintarDerivadoASaludLocal(usuario);
        } else if (usuario.getCurrentState().getUserStatus() == UserStatus.INFECTED) {
            pintarInfectado(usuario);
        }
    }

    private void pintarDerivadoASaludLocal(LocalUser usuario) {
        final TextView txtNoPuedeCircularDiagnostico = view.findViewById(R.id.txtNoPuedeCircularDiagnostico);
        final TextView txtIntruccionesLlamar = view.findViewById(R.id.txtIntruccionesLlamar);
        final TextView txtNoSalir = view.findViewById(R.id.txtNoSalir);

        String text = getString(R.string.msg_sintomas_compatibles_y_reportados, usuario.getNames());
        int pintarDesde = usuario.getNames().length() + 2;
        int pintarHasta = text.length();
        final Typeface font = ResourcesCompat.getFont(requireActivity(), R.font.roboto_bold);
        final SpannableStringBuilder textoPintado = PintarSeccionBold.pintarBold(text, pintarDesde, pintarHasta, font);
        txtNoPuedeCircularDiagnostico.setText(textoPintado);

        // cambiar texto si corresponde a CABA
        ProvincesEnum stateValue = ProvincesEnum.fromString(usuario.getCurrentState().getCoep().getCoep());
        if (stateValue == ProvincesEnum.CABA) {
            txtIntruccionesLlamar.setText(R.string.diagnostic_caba_message);
            txtNoSalir.setText(R.string.error_diagnostic_message);
        }
    }

    private void pintarInfectado(LocalUser usuario) {
        final TextView title = view.findViewById(R.id.txtNoPuedeCircularTitle);
        title.setText(getString(R.string.covid_positivo));

        Spanned mSpannedBold = StringUtils.applyFont(requireContext(), getString(R.string.texto_resultado_positivo_dos), R.font.roboto_bold);
        final TextView tvNoPuedeCircular = view.findViewById(R.id.txtNoPuedeCircularDiagnostico);
        SpannedString spannedString = SpanFormatter.format(getResources().getString(R.string.texto_resultado_positivo_uno), usuario.getNames(), mSpannedBold);
        tvNoPuedeCircular.setText(spannedString);

        final TextView txtIntruccionesLlamar = view.findViewById(R.id.txtIntruccionesLlamar);
        txtIntruccionesLlamar.setVisibility(View.GONE);

        final TextView txtNoSalir = view.findViewById(R.id.txtNoSalir);
        txtNoSalir.setVisibility(View.GONE);
    }

    private void pintarDatosCoep(LocalUser usuario) {
        final TextView caba = view.findViewById(R.id.txtNumeroLlamar);
        caba.setText(getString(R.string.provinvia_y_telefono,
                usuario.getCurrentState().getCoep().getCoep(),
                usuario.getCurrentState().getCoep().getContactInformation()));
    }
}
