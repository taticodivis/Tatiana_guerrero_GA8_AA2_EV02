package com.codivis.movil.datos;

import com.codivis.movil.modelo.Cliente;
import com.codivis.movil.modelo.Usuario;
import java.util.List;

/** Contrato de almacenamiento utilizado por los servicios, independiente de las pantallas. */
public interface Repositorio {
    Usuario buscarUsuario(String nombre);
    long crearUsuario(String nombre, String claveHash);
    List<Cliente> listarClientes(long usuarioId);
    Cliente buscarCliente(long usuarioId, long clienteId);
    boolean correoOcupado(long usuarioId, String correo, long exceptoId);
    long crearCliente(Cliente cliente);
    boolean actualizarCliente(Cliente cliente);
    boolean eliminarCliente(long usuarioId, long clienteId);
}
