package com.codivis.movil.dominio;

/** Error de validación que puede explicarse al usuario sin mostrar detalles internos. */
public final class ReglaException extends IllegalArgumentException {
    public ReglaException(String mensaje) { super(mensaje); }
}
