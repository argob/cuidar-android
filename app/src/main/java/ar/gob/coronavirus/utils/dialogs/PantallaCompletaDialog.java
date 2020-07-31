package ar.gob.coronavirus.utils.dialogs;

import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.utils.strings.StringUtils;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PantallaCompletaDialog extends DialogFragment {

    private static final String TITULO_LLAVE = "TITULO_LLAVE";
    private static final String MENSAJE_LLAVE = "MENSAJE_LLAVE";
    private static final String MENSAJE_BOTON_LLAVE = "MENSAJE_BOTON_LLAVE";
    private static final String ICONO_RECURSO_LLAVE = "ICONO_RECURSO_LLAVE";

    private AccionBotonDialogoPantallaCompleta accion;
    private Boolean dismissAlFinal = true;

    public static PantallaCompletaDialog newInstance (
            String titulo,
            String mensaje,
            String mensajeBoton,
            int iconoResource
    ) {
        Bundle data = new Bundle();
        data.putString(TITULO_LLAVE, titulo);
        data.putString(MENSAJE_LLAVE, mensaje);
        data.putString(MENSAJE_BOTON_LLAVE, mensajeBoton);
        data.putInt(ICONO_RECURSO_LLAVE, iconoResource);

        PantallaCompletaDialog dialog = new PantallaCompletaDialog();
        dialog.setArguments(data);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogo_pantalla_completa, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViewValues();
        setNoCacelable();
    }

    private void setViewValues() {
        String titulo = getArguments().getString(TITULO_LLAVE);
        String mensaje = getArguments().getString(MENSAJE_LLAVE);
        String mensajeBoton = getArguments().getString(MENSAJE_BOTON_LLAVE);
        int recursoIcono = getArguments().getInt(ICONO_RECURSO_LLAVE);

        TextView viewTitulo = getView().findViewById(R.id.txt_titulo_pantalla_completa_dialog);
        TextView viewMensaje = getView().findViewById(R.id.txt_mensaje_pantalla_completa_dialog);
        ImageView viewIcono = getView().findViewById(R.id.pantalla_completa_logo);
        MaterialTextView viewBoton= getView().findViewById(R.id.btn_action_pantalla_completa_dialog);

        Spanned titloFormated = StringUtils.applyFont(getContext(), titulo, R.font.encode_bold);
        Spanned mensajeFormated = StringUtils.applyFont(getContext(), mensaje, R.font.roboto_medium);

        viewTitulo.setText(titloFormated);
        viewMensaje.setText(mensajeFormated);
        viewIcono.setImageResource(recursoIcono);
        viewBoton.setText(mensajeBoton);

        viewBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dismissAlFinal) dismiss();
                if (accion != null) accion.onClick(v);
            }
        });
    }

    private void setNoCacelable() {
        setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void configurarPantallaCompleta() {
        setStyle(DialogFragment.STYLE_NORMAL,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setDismissAlFinal(Boolean dismissAlFinal) {
        this.dismissAlFinal = dismissAlFinal;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configurarPantallaCompleta();
    }

    public void setAccionBoton(AccionBotonDialogoPantallaCompleta accion) {
        this.accion = accion;
    }

    public interface AccionBotonDialogoPantallaCompleta extends View.OnClickListener {
        @Override
        void onClick(View v);
    }
}
