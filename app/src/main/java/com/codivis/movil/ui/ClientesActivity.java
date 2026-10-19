package com.codivis.movil.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.*;
import com.codivis.movil.R;
import com.codivis.movil.modelo.Cliente;
import java.util.List;

/** Listado privado con búsqueda, formulario compartido y confirmación de eliminación. */
public final class ClientesActivity extends BaseActivity {
    private LinearLayout lista;
    private EditText buscar;
    private TextView total,error;
    private long versionCarga;
    @Override public void onCreate(Bundle estado) {
        super.onCreate(estado);if(!exigirSesion())return;
        LinearLayout root=Ui.pantalla(this);
        Ui.cabecera(this,root,"Tus clientes, en orden","Hola, "+app().sesion.nombre()+". Organiza tus contactos desde un solo lugar.");
        total=Ui.texto(this,"Cargando clientes…",13,Ui.MORADO,true);total.setId(R.id.total_clientes);root.addView(total);Ui.espacio(this,root,18);
        Button nuevo=Ui.boton(this,"+  Nuevo cliente",true,R.id.nuevo_cliente);root.addView(nuevo,new LinearLayout.LayoutParams(-1,-2));nuevo.setOnClickListener(v->ClienteDialog.mostrar(this,null));
        Ui.espacio(this,root,20);buscar=Ui.campo(this,root,"Buscar por nombre o correo",InputType.TYPE_CLASS_TEXT,R.id.buscar);
        error=Ui.error(this,root,R.id.error);lista=Ui.columna(this);lista.setId(R.id.lista_clientes);root.addView(lista);
        Ui.espacio(this,root,22);Button salir=Ui.boton(this,"Cerrar sesión",false,R.id.salir);root.addView(salir,new LinearLayout.LayoutParams(-1,-2));
        salir.setOnClickListener(v->{app().sesion.cerrar();startActivity(new Intent(this,LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));finish();});
        buscar.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}public void onTextChanged(CharSequence s,int start,int before,int count){cargar();}public void afterTextChanged(Editable s){}});
        cargar();
    }
    @Override protected void onResume(){super.onResume();if(lista!=null&&!isFinishing())exigirSesion();}
    public void cargar() {
        if(!exigirSesion())return;
        long version=++versionCarga,usuarioId=app().sesion.usuarioId();String filtro=buscar.getText().toString();
        ejecutar(()->app().clientes.listar(usuarioId,filtro),clientes->{if(version==versionCarga){error.setText("");dibujar(clientes);}},mensaje->{if(version==versionCarga)error.setText(mensaje);});
    }
    private void dibujar(List<Cliente> clientes) {
        lista.removeAllViews();total.setText(clientes.size()+ (clientes.size()==1?" cliente":" clientes")+(buscar.getText().length()>0?" encontrados":" en tu cuenta"));
        if(clientes.isEmpty()){
            LinearLayout vacio=tarjeta();vacio.addView(Ui.texto(this,"Todavía no hay resultados",19,Ui.TINTA,true));Ui.espacio(this,vacio,8);
            vacio.addView(Ui.texto(this,buscar.getText().length()>0?"Prueba con otro nombre o correo.":"Crea tu primer cliente para empezar.",14,Ui.MUTED,false));lista.addView(vacio);return;
        }
        for(Cliente c:clientes){
            LinearLayout card=tarjeta();card.addView(Ui.texto(this,c.nombre(),20,Ui.TINTA,true));Ui.espacio(this,card,6);
            card.addView(Ui.texto(this,c.correo(),14,Ui.MUTED,false));if(!c.telefono().isEmpty())card.addView(Ui.texto(this,c.telefono(),14,Ui.MUTED,false));
            Ui.espacio(this,card,15);LinearLayout acciones=new LinearLayout(this);acciones.setOrientation(LinearLayout.HORIZONTAL);
            Button editar=Ui.boton(this,"Editar",false,R.id.editar_cliente),eliminar=Ui.boton(this,"Eliminar",false,R.id.eliminar_cliente);
            LinearLayout.LayoutParams e=new LinearLayout.LayoutParams(0,-2,1);e.setMarginEnd(Ui.dp(this,8));acciones.addView(editar,e);acciones.addView(eliminar,new LinearLayout.LayoutParams(0,-2,1));card.addView(acciones);
            editar.setOnClickListener(v->ClienteDialog.mostrar(this,c));eliminar.setOnClickListener(v->confirmarEliminar(c));
            lista.addView(card);Ui.espacio(this,lista,14);
        }
    }
    private LinearLayout tarjeta(){LinearLayout c=Ui.columna(this);int p=Ui.dp(this,20);c.setPadding(p,p,p,p);c.setBackground(Ui.fondo(Color.WHITE,Ui.dp(this,18),Color.rgb(227,222,239)));return c;}
    private void confirmarEliminar(Cliente c){
        new AlertDialog.Builder(this).setTitle("Eliminar cliente").setMessage("¿Quieres eliminar a "+c.nombre()+"? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar",null).setPositiveButton("Eliminar",(d,w)->ejecutar(()->{app().clientes.eliminar(app().sesion.usuarioId(),c.id());return true;},ok->{cargar();Toast.makeText(this,"Cliente eliminado.",Toast.LENGTH_SHORT).show();},mensaje->error.setText(mensaje))).show();
    }
}
