package com.codivis.movil;

import com.codivis.movil.dominio.PasswordHasher;
import org.junit.Test;
import static org.junit.Assert.*;

public class PasswordHasherTest {
    private final PasswordHasher hasher = new PasswordHasher();
    @Test public void salAleatoriaProduceHashesDiferentes() {
        String uno=hasher.crear("Codivis2026!"),dos=hasher.crear("Codivis2026!");
        assertNotEquals(uno,dos);assertFalse(uno.contains("Codivis2026!"));
    }
    @Test public void aceptaClaveCorrectaYRechazaIncorrecta() {
        String hash=hasher.crear("Codivis2026!");assertTrue(hasher.verificar("Codivis2026!",hash));assertFalse(hasher.verificar("incorrecta",hash));
    }
    @Test public void rechazaHashDeterioradoSinExcepcion() {
        assertFalse(hasher.verificar("Codivis2026!","dato inválido"));assertFalse(hasher.verificar("x","999999999:abc:def"));
    }
    @Test public void noAceptaClaveNulaONombresDeAlgoritmoManipulados() {
        assertFalse(hasher.verificar(null,"x"));assertFalse(hasher.verificar("x","600000:!:!"));
    }
}
