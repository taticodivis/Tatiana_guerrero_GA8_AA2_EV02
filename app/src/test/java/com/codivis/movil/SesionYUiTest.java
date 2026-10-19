package com.codivis.movil;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.widget.*;
import com.codivis.movil.modelo.Usuario;
import com.codivis.movil.sesion.Sesion;
import com.codivis.movil.ui.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,application=CodivisApp.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class SesionYUiTest {
    private CodivisApp app;
    @Before public void preparar(){app=(CodivisApp)RuntimeEnvironment.getApplication();app.sesion.cerrar();app.deleteDatabase("codivis.db");}
    @After public void terminar(){app.tareas.shutdownNow();app.sesion.cerrar();}
    private void esperar() throws Exception {app.tareas.submit(()->{}).get(15,java.util.concurrent.TimeUnit.SECONDS);shadowOf(Looper.getMainLooper()).idle();}
    @Test public void sesionRecuerdaIdentidadSinGuardarClave(){app.sesion.abrir(new Usuario(7,"pedro","hash-que-no-debe-guardarse"));Sesion otra=new Sesion(app);assertTrue(otra.activa());assertEquals(7,otra.usuarioId());assertEquals("pedro",otra.nombre());assertFalse(app.getSharedPreferences("codivis_sesion",0).getAll().containsValue("hash-que-no-debe-guardarse"));}
    @Test public void cerrarSesionEliminaIdentidad(){app.sesion.abrir(new Usuario(7,"pedro","hash"));app.sesion.cerrar();assertFalse(new Sesion(app).activa());assertEquals("",app.sesion.nombre());}
    @Test public void pantallaPrivadaSinSesionRedirigeAlLogin(){try(ActivityController<ClientesActivity> c=Robolectric.buildActivity(ClientesActivity.class).create()){Intent siguiente=shadowOf(c.get()).getNextStartedActivity();assertNotNull(siguiente);assertEquals(LoginActivity.class.getName(),siguiente.getComponent().getClassName());assertTrue(c.get().isFinishing());}}
    @Test public void registroInvalidoMuestraErrorYSigueEditable() throws Exception {
        try(ActivityController<RegistroActivity> c=Robolectric.buildActivity(RegistroActivity.class).setup()){
            c.get().findViewById(R.id.registrar).performClick();esperar();
            assertFalse(((TextView)c.get().findViewById(R.id.error)).getText().toString().isEmpty());assertTrue(c.get().findViewById(R.id.registrar).isEnabled());
        }
    }
    @Test public void loginCorrectoAbreClientes() throws Exception {
        app.autenticacion.registrar("pedro","Codivis2026!","Codivis2026!");
        try(ActivityController<LoginActivity> c=Robolectric.buildActivity(LoginActivity.class).setup()){
            ((EditText)c.get().findViewById(R.id.usuario)).setText("pedro");((EditText)c.get().findViewById(R.id.clave)).setText("Codivis2026!");
            c.get().findViewById(R.id.entrar).performClick();esperar();assertTrue(app.sesion.activa());
            assertEquals(ClientesActivity.class.getName(),shadowOf(c.get()).getNextStartedActivity().getComponent().getClassName());
        }
    }
    @Test public void listadoMuestraClientesYBotonSalirCierraSesion() throws Exception {
        app.autenticacion.registrar("pedro","Codivis2026!","Codivis2026!");app.sesion.abrir(app.autenticacion.iniciarSesion("pedro","Codivis2026!"));
        app.clientes.crear(app.sesion.usuarioId(),"Ana","ana@example.com","");
        try(ActivityController<ClientesActivity> c=Robolectric.buildActivity(ClientesActivity.class).setup()){
            esperar();assertEquals("1 cliente en tu cuenta",((TextView)c.get().findViewById(R.id.total_clientes)).getText().toString());
            c.get().findViewById(R.id.salir).performClick();assertFalse(app.sesion.activa());
            Intent siguiente=shadowOf(c.get()).getNextStartedActivity();assertEquals(LoginActivity.class.getName(),siguiente.getComponent().getClassName());assertTrue((siguiente.getFlags()&Intent.FLAG_ACTIVITY_CLEAR_TASK)!=0);
        }
    }
}
