package com.codivis.movil.sesion;

import android.content.Context;
import android.content.SharedPreferences;
import com.codivis.movil.modelo.Usuario;

/** Conserva la identidad local de la sesión, sin guardar la contraseña. */
public final class Sesion {
    private final SharedPreferences preferencias;
    public Sesion(Context context) { preferencias = context.getSharedPreferences("codivis_sesion",Context.MODE_PRIVATE); }
    public long usuarioId() { return preferencias.getLong("usuario_id",0); }
    public String nombre() { return preferencias.getString("nombre",""); }
    public boolean activa() { return usuarioId() > 0; }
    public void abrir(Usuario usuario) { preferencias.edit().putLong("usuario_id",usuario.id()).putString("nombre",usuario.nombre()).apply(); }
    public void cerrar() { preferencias.edit().clear().apply(); }
}
