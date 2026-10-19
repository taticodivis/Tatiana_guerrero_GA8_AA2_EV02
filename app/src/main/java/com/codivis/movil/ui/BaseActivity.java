package com.codivis.movil.ui;

import android.app.Activity;
import android.content.Intent;
import com.codivis.movil.CodivisApp;
import com.codivis.movil.dominio.ReglaException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

/** Coordina trabajo de base de datos y hash fuera del hilo que dibuja la interfaz. */
public abstract class BaseActivity extends Activity {
    protected CodivisApp app() { return (CodivisApp)getApplication(); }
    protected <T> void ejecutar(Callable<T> trabajo,Consumer<T> exito,Consumer<String> fallo) {
        app().tareas.execute(()->{
            try { T resultado=trabajo.call();runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())exito.accept(resultado);}); }
            catch(Exception ex) {
                if(!(ex instanceof ReglaException))android.util.Log.e("Codivis","No se pudo completar la operación",ex);
                String mensaje=ex instanceof ReglaException?ex.getMessage():"No se pudo completar la operación. Inténtalo de nuevo.";
                runOnUiThread(()->{if(!isFinishing()&&!isDestroyed())fallo.accept(mensaje);});
            }
        });
    }
    protected void abrirClientes() {
        startActivity(new Intent(this,ClientesActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();
    }
    protected boolean exigirSesion() {
        if(app().sesion.activa())return true;
        startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();return false;
    }
}
