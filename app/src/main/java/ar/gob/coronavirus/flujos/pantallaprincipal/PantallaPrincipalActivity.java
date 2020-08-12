package ar.gob.coronavirus.flujos.pantallaprincipal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Group;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

import org.koin.androidx.viewmodel.compat.ViewModelCompat;

import ar.gob.coronavirus.R;
import ar.gob.coronavirus.data.UserStatus;
import ar.gob.coronavirus.data.local.modelo.LocalUser;
import ar.gob.coronavirus.flujos.BaseActivity;
import ar.gob.coronavirus.flujos.autodiagnostico.AutodiagnosticoActivity;
import ar.gob.coronavirus.flujos.autodiagnostico.ProvincesEnum;
import ar.gob.coronavirus.flujos.autodiagnostico.resultado.ResultadoActivity;
import ar.gob.coronavirus.flujos.identificacion.IdentificacionActivity;
import ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.CircularFragment;
import ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.CovidPositivoFragment;
import ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.DerivadoASaludFragment;
import ar.gob.coronavirus.flujos.pantallaprincipal.ui.pantallaprincipal.NoInfectadoFragment;
import ar.gob.coronavirus.flujos.pba.PbaActivity;
import ar.gob.coronavirus.utils.Constantes;
import ar.gob.coronavirus.utils.InternetUtileria;
import ar.gob.coronavirus.utils.dialogs.LoadingDialog;
import ar.gob.coronavirus.utils.dialogs.PantallaCompletaDialog;
import ar.gob.coronavirus.utils.extensions.ActivityExtensionsKt;
import ar.gob.coronavirus.utils.observables.EventoUnico;

public class PantallaPrincipalActivity extends BaseActivity implements View.OnClickListener {
    private static final String LLAVE_MOSTRAR_RESULTADO = "LLAVE_MOSTRAR_RESULTADO";
    private PantallaPrincipalViewModel mViewModel;
    private TextView nombreDeUsuario;
    private TextView numeroDni;
    private TextView direccion;
    private TextView telefono;
    private SwipeRefreshLayout refreshLayout;
    private WebView adviceIv;

    private Dialog loadingDialog;

    boolean mostrarResultado;
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;

    public static void iniciar(Context context, boolean mostrarResultado) {
        Intent intent = new Intent(context, PantallaPrincipalActivity.class);
        intent.putExtra(LLAVE_MOSTRAR_RESULTADO, mostrarResultado);
        context.startActivity(intent);
    }

    Observer<LocalUser> mostrar_resultado = new Observer<LocalUser>() {
        @Override
        public void onChanged(LocalUser usuario) {
            refreshLayout.setRefreshing(false);
            setHeaderData(usuario);
            if (mostrarResultado) {
                mostrarResultado = false;
                if (puedeUsuarioCircular(usuario.getCurrentState().getUserStatus())) {
                    ResultadoActivity.iniciar(PantallaPrincipalActivity.this, ResultadoActivity.OpcionesNavegacion.RESULTADO_VERDE);
                } else {
                    ResultadoActivity.iniciar(PantallaPrincipalActivity.this, ResultadoActivity.OpcionesNavegacion.RESULTADO_ROSA);
                }
            } else {
                if (adviceIv != null) {
                    adviceIv.loadUrl(null);
                    adviceIv.setVisibility(View.GONE);
                }
            }
        }
    };

    private void observarCambiosUsuario() {
        mViewModel.obtenerUltimoEstadoLiveData().observeForever(mostrar_resultado);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.obtenerUltimoEstadoLiveData().removeObserver(mostrar_resultado);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_principal_drawer_layout);

        toolbar = findViewById(R.id.toolb_bar_principal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        mViewModel = ViewModelCompat.getViewModel(this, PantallaPrincipalViewModel.class);
        setBaseViewModel(mViewModel);
        mViewModel.permitirNavegar = false;

        setupViews();

        loadingDialog = LoadingDialog.createLoadingDialog(this, getLayoutInflater());
        adviceIv = findViewById(R.id.advice_image);
        adviceIv.getSettings().setAppCacheEnabled(true);
        adviceIv.getSettings().setAppCachePath(getCacheDir().getPath());
        adviceIv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        adviceIv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                adviceIv.setVisibility(View.VISIBLE);
            }
        });

        mostrarResultado = getIntent().getBooleanExtra(LLAVE_MOSTRAR_RESULTADO, false);
        observarCambiosUsuario();
        mViewModel.obtenerUsuarioDeLaBD();


        mViewModel.obtenerUltimoEstadoDeBackend();
        mViewModel.obtenerEventosDeNavegacionLiveData()
                .observe(this, destino -> {
                    PantallaPrincipalViewModel.NavegacionDestinosPantallaPrincipal destinoDelEvento = destino.obtenerContenidoSiNoFueLanzado();
                    if (destinoDelEvento != null) {
                        switch (destinoDelEvento) {
                            case CIRCULAR:
                                circular();
                                break;
                            case NO_INFECTADO:
                                noInfectado();
                                break;
                            case INFECTADO:
                                covidPositivo();
                                break;
                            case DERIVADO_A_SALUD_LOCAL:
                                derivadoSalud();
                                break;
                            case DEBE_AUTODIAGNOSTICARSE:
                                AutodiagnosticoActivity.iniciarActividadParaResultado(PantallaPrincipalActivity.this, true);
                                break;
                            case DESLOGUEAR:
                                mViewModel.logout();
                                IdentificacionActivity.startRemovingStack(PantallaPrincipalActivity.this);
                                break;
                        }
                    }
                });

        mViewModel.obtenerErrorBackend().observe(this, new Observer<EventoUnico<Integer>>() {
            @Override
            public void onChanged(EventoUnico<Integer> booleanEventoUnico) {
                if (booleanEventoUnico.obtenerContenidoSiNoFueLanzado() != null) {
                    PantallaCompletaDialog.newInstance(
                            getString(R.string.hubo_error),
                            getString(R.string.hubo_error_desc),
                            "Cerrar",
                            R.drawable.ic_error
                    ).show(getSupportFragmentManager(), "ERROR_DIALOG_TAG");
                }
            }
        });

        mViewModel.obtenerEventoDeDialogo().observe(this, new Observer<EventoUnico<Boolean>>() {
            @Override
            public void onChanged(EventoUnico<Boolean> booleanEventoUnico) {
//                if (booleanEventoUnico.obtenerContenidoSiNoFueLanzado() != null) {
//                    if (booleanEventoUnico.obtenerConenido()) {
//                        loadingDialog.show();
//                    } else {
//                        loadingDialog.dismiss();
//                    }
//                }
            }
        });

        refreshLayout.setOnRefreshListener(() -> {
            mViewModel.obtenerUltimoEstadoDeBackend();
        });

        mViewModel.getAdviceLiveData().observe(this, adviceUrl -> {
            if (!mostrarResultado) {
                adviceIv.loadUrl(adviceUrl);
            }
        });
    }

    private void setHeaderData(LocalUser usuario) {
        nombreDeUsuario.setText(usuario.getNames() + " " + usuario.getLastNames());
        numeroDni.setText(String.valueOf(usuario.getDni()));
        direccion.setText(usuario.getAddress().toString());
        telefono.setText(usuario.getPhone());

        ProvincesEnum province = ProvincesEnum.fromString(usuario.getAddress().getProvince());
        Group pbaOption = findViewById(R.id.pba_group);
        if (pbaOption != null) {
            // Si es PBA mostramos un boton especial en el drawer
            pbaOption.setVisibility(province == ProvincesEnum.BSAS ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Constantes.CODIGO_DE_PEDIDO_AUTODIAGNOSTICO == requestCode && resultCode == RESULT_OK) {
            mostrarResultado = true;
            mViewModel.limpiarUsuarioActual();
            mViewModel.obtenerUsuarioDeLaBD();
        }
    }

    private boolean puedeUsuarioCircular(UserStatus status) {
        return status == UserStatus.NOT_CONTAGIOUS || status == UserStatus.NOT_INFECTED;
    }

    private void setupViews() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        nombreDeUsuario = headerView.findViewById(R.id.nombre_de_usuario);
        numeroDni = headerView.findViewById(R.id.numero_dni);
        telefono = headerView.findViewById(R.id.h_telefono_numero);
        direccion = headerView.findViewById(R.id.h_direccion_valor);
        TextView cerrarSesion = headerView.findViewById(R.id.h_cerrar_sesion_label);
        ImageView cruz = headerView.findViewById(R.id.h_icono_equis);
        TextView editarInfo = headerView.findViewById(R.id.h_editar_info);
        TextView videoLlamadaLabel = headerView.findViewById(R.id.h_video_llamada_label);
        TextView masInfoLabel = headerView.findViewById(R.id.h_info_label);
        TextView redesLabel = headerView.findViewById(R.id.h_redes_label);
        View pbaLabel = headerView.findViewById(R.id.h_pba);

        cerrarSesion.setOnClickListener(this);
        cruz.setOnClickListener(this);
        editarInfo.setOnClickListener(this);
        videoLlamadaLabel.setOnClickListener(this);
        masInfoLabel.setOnClickListener(this);
        redesLabel.setOnClickListener(this);
        pbaLabel.setOnClickListener(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        refreshLayout = findViewById(R.id.swipe_refresh);
        ActionBarDrawerToggle toogle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.abrir, R.string.cerrar);
        toogle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
    }

    public void covidPositivo() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof CovidPositivoFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CovidPositivoFragment())
                    .commitNow();
        }
    }

    public void derivadoSalud() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof DerivadoASaludFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new DerivadoASaludFragment())
                    .commitNow();
        }
    }

    public void circular() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof CircularFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new CircularFragment())
                    .commitNow();
        }
    }


    public void noInfectado() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof NoInfectadoFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new NoInfectadoFragment())
                    .commitNow();
        }
    }

    public void openPbaActivity() {
        PbaActivity.start(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.h_icono_equis:
                drawerLayout.close();
                break;
            case R.id.h_cerrar_sesion_label:
                mViewModel.logout();
                drawerLayout.close();
                IdentificacionActivity.startRemovingStack(PantallaPrincipalActivity.this);
                break;
            case R.id.h_editar_info:
                IdentificacionActivity.iniciar(this, true);
                drawerLayout.close();
                break;
            case R.id.h_video_llamada_label:
            case R.id.h_icon_video_call:
                if (InternetUtileria.hayConexionDeInternet(this)) {
                    drawerLayout.close();
                    ActivityExtensionsKt.startWebView(this, Constantes.URL_VIDEO_LLAMADA);
                }
                break;
            case R.id.h_info_label:
            case R.id.h_icono_informacion:
                if (InternetUtileria.hayConexionDeInternet(this)) {
                    drawerLayout.close();
                    ActivityExtensionsKt.startWebView(this, Constantes.URL_MAS_INFORMACION);
                }
                break;
            case R.id.h_redes_label:
            case R.id.h_icono_redes:
                if (InternetUtileria.hayConexionDeInternet(this)) {
                    drawerLayout.close();
                    ActivityExtensionsKt.startWebView(this, Constantes.URL_REDES_SOCIALES);
                }
                break;
            case R.id.h_pba:
                openPbaActivity();
                drawerLayout.close();
                break;
        }
    }
}
