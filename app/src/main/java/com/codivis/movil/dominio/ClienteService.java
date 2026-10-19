package com.codivis.movil.dominio;

import com.codivis.movil.datos.Repositorio;
import com.codivis.movil.modelo.Cliente;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/** Reglas del módulo de clientes y separación de registros entre cuentas. */
public final class ClienteService {
    private final Repositorio repositorio;
    public ClienteService(Repositorio repositorio) { this.repositorio = repositorio; }
    private void exigirSesion(long usuarioId) { if (usuarioId <= 0) throw new ReglaException("Inicia sesión para acceder a tus clientes."); }
    public List<Cliente> listar(long usuarioId, String busqueda) {
        exigirSesion(usuarioId);
        String filtro = busqueda == null ? "" : busqueda.trim().toLowerCase(Locale.ROOT);
        List<Cliente> resultado = new ArrayList<>();
        for (Cliente c : repositorio.listarClientes(usuarioId)) {
            if (c.nombre().toLowerCase(Locale.ROOT).contains(filtro) || c.correo().contains(filtro)) resultado.add(c);
        }
        return resultado;
    }
    public Cliente obtener(long usuarioId, long id) {
        exigirSesion(usuarioId);
        Cliente cliente = repositorio.buscarCliente(usuarioId, id);
        if (cliente == null) throw new ReglaException("El cliente no existe o no pertenece a tu cuenta.");
        return cliente;
    }
    public long crear(long usuarioId, String nombre, String correo, String telefono) {
        exigirSesion(usuarioId);
        return repositorio.crearCliente(validar(0, usuarioId, nombre, correo, telefono));
    }
    public void actualizar(long usuarioId, long id, String nombre, String correo, String telefono) {
        obtener(usuarioId, id);
        if (!repositorio.actualizarCliente(validar(id, usuarioId, nombre, correo, telefono)))
            throw new ReglaException("El cliente ya no existe.");
    }
    public void eliminar(long usuarioId, long id) {
        obtener(usuarioId, id);
        if (!repositorio.eliminarCliente(usuarioId, id)) throw new ReglaException("El cliente ya no existe.");
    }
    private Cliente validar(long id, long usuarioId, String nombre, String correo, String telefono) {
        nombre = nombre == null ? "" : nombre.trim();
        correo = correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
        telefono = telefono == null ? "" : telefono.trim();
        if (nombre.length() < 2 || nombre.length() > 80) throw new ReglaException("El nombre debe tener entre 2 y 80 caracteres.");
        if (correo.length() > 120 || !correo.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) throw new ReglaException("Escribe un correo electrónico válido.");
        if (!telefono.isEmpty() && !telefono.matches("[+()0-9 .-]{7,25}")) throw new ReglaException("Escribe un teléfono válido de 7 a 25 caracteres o déjalo vacío.");
        if (repositorio.correoOcupado(usuarioId, correo, id)) throw new ReglaException("Ese correo ya pertenece a otro cliente de tu cuenta.");
        return new Cliente(id, usuarioId, nombre, correo, telefono);
    }
}
