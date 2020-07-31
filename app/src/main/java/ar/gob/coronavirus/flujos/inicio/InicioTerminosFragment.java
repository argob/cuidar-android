package ar.gob.coronavirus.flujos.inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ar.gob.coronavirus.R;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class InicioTerminosFragment extends DialogFragment {

	private TextView textoTerminos;
	private MaterialTextView buttonEntendido;
	private ImageView backButton;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.inicio_terminos_fragment, container, false);
		textoTerminos = (TextView) view.findViewById(R.id.textoTerminos);
		textoTerminos.setText(getText(R.string.texto_terminos));
		buttonEntendido = view.findViewById(R.id.btn_aceptar);
		backButton = view.findViewById(R.id.imgRowBack);

		buttonEntendido.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

		return view;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configurarPantallaCompleta();
	}

	private void configurarPantallaCompleta() {
		setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
	}
}
