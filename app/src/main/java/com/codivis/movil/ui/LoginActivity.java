package com.codivis.movil.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import com.codivis.movil.R;

/** Pantalla pública de inicio de sesión. No existen cuentas preinstaladas. */
public final class LoginActivity extends BaseActivity {
    @Override public void onCreate(Bundle estado) {
        super.onCreate(estado);
        if(app().sesion.activa()){abrirClientes();return;}
        LinearLayout root=Ui.pantalla(this);
        Ui.cabecera(this,root,"Bienvenida a tu CRM","Inicia sesión para organizar tus clientes desde el móvil.");
        EditText usuario=Ui.campo(this,root,"Usuario",InputType.TYPE_CLASS_TEXT,R.id.usuario);
        EditText clave=Ui.campo(this,root,"Contraseña",Ui.tipoClave(),R.id.clave);
        TextView error=Ui.error(this,root,R.id.error);
        Button entrar=Ui.boton(this,"Iniciar sesión",true,R.id.entrar);root.addView(entrar,new LinearLayout.LayoutParams(-1,-2));
        entrar.setOnClickListener(v->{
            error.setText("");entrar.setEnabled(false);entrar.setText("Entrando…");
            String nombre=usuario.getText().toString(),password=clave.getText().toString();
            ejecutar(()->app().autenticacion.iniciarSesion(nombre,password),cuenta->{app().sesion.abrir(cuenta);abrirClientes();},mensaje->{error.setText(mensaje);entrar.setEnabled(true);entrar.setText("Iniciar sesión");});
        });
        Ui.espacio(this,root,14);
        Button registro=Ui.boton(this,"Crear una cuenta",false,R.id.ir_registro);root.addView(registro,new LinearLayout.LayoutParams(-1,-2));
        registro.setOnClickListener(v->startActivity(new Intent(this,RegistroActivity.class)));
        Ui.espacio(this,root,24);root.addView(Ui.texto(this,"Tus datos permanecen en este dispositivo.",12,Ui.MUTED,false));
    }
    @Override protected void onResume(){super.onResume();if(app().sesion.activa())abrirClientes();}
}
