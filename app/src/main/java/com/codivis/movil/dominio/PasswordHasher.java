package com.codivis.movil.dominio;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** PBKDF2-HMAC-SHA256 con sal aleatoria por cuenta y comparación de tiempo constante. */
public final class PasswordHasher {
    private static final int ITERACIONES = 600_000;
    public String crear(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return ITERACIONES + ":" + Base64.getEncoder().encodeToString(sal) + ":"
                + Base64.getEncoder().encodeToString(derivar(clave, sal));
    }
    public boolean verificar(String clave, String guardado) {
        if (clave == null || clave.length() > 128 || guardado == null) return false;
        try {
            String[] partes = guardado.split(":");
            if (partes.length != 3 || Integer.parseInt(partes[0]) != ITERACIONES) return false;
            byte[] sal = Base64.getDecoder().decode(partes[1]);
            byte[] hash = Base64.getDecoder().decode(partes[2]);
            return sal.length == 16 && hash.length == 32 && MessageDigest.isEqual(hash, derivar(clave, sal));
        } catch (IllegalArgumentException ex) { return false; }
    }
    private byte[] derivar(String clave, byte[] sal) {
        char[] caracteres = clave.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(caracteres, sal, ITERACIONES, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (java.security.GeneralSecurityException ex) { throw new IllegalStateException("No se pudo proteger la contraseña.", ex); }
        finally { spec.clearPassword(); Arrays.fill(caracteres, '\0'); }
    }
}
