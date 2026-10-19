package com.codivis.movil.dominio;

import com.codivis.movil.datos.Repositorio;
import com.codivis.movil.modelo.Usuario;
import java.util.Locale;

/** Casos de uso de registro e inicio de sesión, reutilizables y comprobables sin interfaz. */
public final class AutenticacionService {
    private final Repositorio repositorio;
    private final PasswordHasher hasher;
    public AutenticacionService(Repositorio repositorio, PasswordHasher hasher) {
        this.repositorio = repositorio; this.hasher = hasher;
    }
    public long registrar(String nombre, String clave, String confirmacion) {
        nombre = normalizar(nombre);
        if (!nombre.matches("[a-z0-9._-]{3,40}"))
            throw new ReglaException("Usa de 3 a 40 letras, números, puntos o guiones para el usuario.");
        if (clave == null || clave.length() < 8 || clave.length() > 128)
            throw new ReglaException("La contraseña debe tener entre 8 y 128 caracteres.");
        if (!clave.equals(confirmacion)) throw new ReglaException("Las contraseñas no coinciden.");
        if (repositorio.buscarUsuario(nombre) != null) throw new ReglaException("Ese usuario ya está registrado.");
        return repositorio.crearUsuario(nombre, hasher.crear(clave));
    }
    public Usuario iniciarSesion(String nombre, String clave) {
        nombre = normalizar(nombre);
        if (nombre.isEmpty() || clave == null || clave.trim().isEmpty()) throw new ReglaException("Escribe tu usuario y contraseña.");
        Usuario usuario = repositorio.buscarUsuario(nombre);
        if (usuario == null || !hasher.verificar(clave, usuario.claveHash()))
            throw new ReglaException("Usuario o contraseña incorrectos.");
        return usuario;
    }
    private static String normalizar(String nombre) { return nombre == null ? "" : nombre.trim().toLowerCase(Locale.ROOT); }
}
