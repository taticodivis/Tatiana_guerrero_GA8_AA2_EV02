package com.codivis.movil;

import android.app.Application;
import com.codivis.movil.datos.BaseDatos;
import com.codivis.movil.datos.SQLiteRepositorio;
import com.codivis.movil.dominio.AutenticacionService;
import com.codivis.movil.dominio.ClienteService;
import com.codivis.movil.dominio.PasswordHasher;
import com.codivis.movil.sesion.Sesion;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Composición de dependencias: una base y un ejecutor compartidos por la aplicación. */
public final class CodivisApp extends Application {
    public AutenticacionService autenticacion;
    public ClienteService clientes;
    public Sesion sesion;
    public final ExecutorService tareas = Executors.newSingleThreadExecutor();
    @Override public void onCreate() {
        super.onCreate();
        SQLiteRepositorio repositorio = new SQLiteRepositorio(new BaseDatos(this));
        autenticacion = new AutenticacionService(repositorio,new PasswordHasher());
        clientes = new ClienteService(repositorio);
        sesion = new Sesion(this);
    }
}
