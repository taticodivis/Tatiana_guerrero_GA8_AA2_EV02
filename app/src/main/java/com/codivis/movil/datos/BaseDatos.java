package com.codivis.movil.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Base privada local. Las claves foráneas y los índices refuerzan las reglas del dominio. */
public final class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context) { super(context.getApplicationContext(), "codivis.db", null, 1); }
    @Override public void onConfigure(SQLiteDatabase db) { db.setForeignKeyConstraintsEnabled(true); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL UNIQUE COLLATE NOCASE, clave_hash TEXT NOT NULL)");
        db.execSQL("CREATE TABLE clientes (id INTEGER PRIMARY KEY AUTOINCREMENT, usuario_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE, nombre TEXT NOT NULL, correo TEXT NOT NULL COLLATE NOCASE, telefono TEXT NOT NULL DEFAULT '', UNIQUE(usuario_id, correo))");
        db.execSQL("CREATE INDEX idx_clientes_usuario ON clientes(usuario_id)");
    }
    @Override public void onUpgrade(SQLiteDatabase db, int anterior, int nueva) {
        // Una actualización futura necesita una migración explícita, sin borrar los datos.
        throw new IllegalStateException("Falta definir la migración de la base de datos.");
    }
}
