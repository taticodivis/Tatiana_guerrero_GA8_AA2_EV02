package com.codivis.movil.modelo;

/** Cliente perteneciente a una cuenta de Codivis. */
public record Cliente(long id, long usuarioId, String nombre, String correo, String telefono) { }
