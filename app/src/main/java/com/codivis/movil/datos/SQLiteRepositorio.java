package com.codivis.movil.datos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import com.codivis.movil.dominio.ReglaException;
import com.codivis.movil.modelo.Cliente;
import com.codivis.movil.modelo.Usuario;
import java.util.ArrayList;
import java.util.List;

/** Implementa el repositorio con consultas parametrizadas y filtros por propietario. */
public final class SQLiteRepositorio implements Repositorio {
    private final BaseDatos base;
    public SQLiteRepositorio(BaseDatos base) { this.base = base; }
    @Override public Usuario buscarUsuario(String nombre) {
        try (Cursor c = base.getReadableDatabase().query("usuarios", new String[]{"id","nombre","clave_hash"}, "nombre = ?", new String[]{nombre}, null, null, null)) {
            return c.moveToFirst() ? new Usuario(c.getLong(0),c.getString(1),c.getString(2)) : null;
        }
    }
    @Override public long crearUsuario(String nombre, String hash) {
        ContentValues datos = new ContentValues(); datos.put("nombre",nombre); datos.put("clave_hash",hash);
        try { return base.getWritableDatabase().insertOrThrow("usuarios",null,datos); }
        catch (SQLiteConstraintException ex) { throw new ReglaException("Ese usuario ya está registrado."); }
    }
    @Override public List<Cliente> listarClientes(long usuarioId) {
        List<Cliente> lista = new ArrayList<>();
        try (Cursor c = base.getReadableDatabase().query("clientes",null,"usuario_id = ?",new String[]{String.valueOf(usuarioId)},null,null,"nombre COLLATE NOCASE, id")) {
            while(c.moveToNext()) lista.add(cliente(c));
        }
        return lista;
    }
    @Override public Cliente buscarCliente(long usuarioId,long id) {
        try (Cursor c = base.getReadableDatabase().query("clientes",null,"usuario_id = ? AND id = ?",new String[]{String.valueOf(usuarioId),String.valueOf(id)},null,null,null)) {
            return c.moveToFirst() ? cliente(c) : null;
        }
    }
    @Override public boolean correoOcupado(long usuarioId,String correo,long exceptoId) {
        try (Cursor c = base.getReadableDatabase().query("clientes",new String[]{"id"},"usuario_id = ? AND correo = ? AND id != ?",new String[]{String.valueOf(usuarioId),correo,String.valueOf(exceptoId)},null,null,null)) {
            return c.moveToFirst();
        }
    }
    @Override public long crearCliente(Cliente cliente) {
        try { return base.getWritableDatabase().insertOrThrow("clientes",null,valores(cliente)); }
        catch (SQLiteConstraintException ex) { throw new ReglaException("No se pudo guardar: revisa el usuario y el correo del cliente."); }
    }
    @Override public boolean actualizarCliente(Cliente cliente) {
        try { return base.getWritableDatabase().update("clientes",valores(cliente),"usuario_id = ? AND id = ?",new String[]{String.valueOf(cliente.usuarioId()),String.valueOf(cliente.id())}) == 1; }
        catch (SQLiteConstraintException ex) { throw new ReglaException("Ese correo ya pertenece a otro cliente de tu cuenta."); }
    }
    @Override public boolean eliminarCliente(long usuarioId,long id) {
        return base.getWritableDatabase().delete("clientes","usuario_id = ? AND id = ?",new String[]{String.valueOf(usuarioId),String.valueOf(id)}) == 1;
    }
    private ContentValues valores(Cliente c) {
        ContentValues v = new ContentValues();v.put("usuario_id",c.usuarioId());v.put("nombre",c.nombre());v.put("correo",c.correo());v.put("telefono",c.telefono());return v;
    }
    private Cliente cliente(Cursor c) {
        return new Cliente(c.getLong(c.getColumnIndexOrThrow("id")),c.getLong(c.getColumnIndexOrThrow("usuario_id")),c.getString(c.getColumnIndexOrThrow("nombre")),c.getString(c.getColumnIndexOrThrow("correo")),c.getString(c.getColumnIndexOrThrow("telefono")));
    }
}
