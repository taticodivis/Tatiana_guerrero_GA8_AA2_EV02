package com.codivis.movil.modelo;

/** Cuenta local. La clave se representa mediante un hash con sal, nunca en texto plano. */
public record Usuario(long id, String nombre, String claveHash) { }
