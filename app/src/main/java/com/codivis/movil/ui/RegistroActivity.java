package com.codivis.movil.ui;

import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import com.codivis.movil.R;

/** Registra cuentas con confirmación, validación y contraseña protegida. */
public final class RegistroActivity extends BaseActivity {
    @Override public void onCreate(Bundle estado) {
        super.onCreate(estado);
        if(app().sesion.activa()){abrirClientes();return;}
        LinearLayout root=Ui.pantalla(this);
        Ui.cabecera(this,root,"Crea tu cuenta","Elige un usuario sencillo, por ejemplo pedro. La contraseña debe tener al menos 8 caracteres.");
        EditText usuario=Ui.campo(this,root,"Usuario",InputType.TYPE_CLASS_TEXT,R.id.usuario);
        EditText clave=Ui.campo(this,root,"Contraseña",Ui.tipoClave(),R.id.clave);
        EditText confirmar=Ui.campo(this,root,"Confirmar contraseña",Ui.tipoClave(),R.id.confirmacion);
        TextView error=Ui.error(this,root,R.id.error);
        Button guardar=Ui.boton(this,"Crear cuenta",true,R.id.registrar);root.addView(guardar,new LinearLayout.LayoutParams(-1,-2));
        guardar.setOnClickListener(v->{
            error.setText("");guardar.setEnabled(false);guardar.setText("Creando cuenta…");
            String nombre=usuario.getText().toString(),password=clave.getText().toString(),repetida=confirmar.getText().toString();
            ejecutar(()->app().autenticacion.registrar(nombre,password,repetida),id->{Toast.makeText(this,"Cuenta creada. Ya puedes iniciar sesión.",Toast.LENGTH_LONG).show();finish();},mensaje->{error.setText(mensaje);guardar.setEnabled(true);guardar.setText("Crear cuenta");});
        });
        Ui.espacio(this,root,14);Button volver=Ui.boton(this,"Volver al inicio de sesión",false,R.id.volver);root.addView(volver,new LinearLayout.LayoutParams(-1,-2));volver.setOnClickListener(v->finish());
    }
}
