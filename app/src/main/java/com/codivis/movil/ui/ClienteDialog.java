package com.codivis.movil.ui;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.*;
import com.codivis.movil.R;
import com.codivis.movil.modelo.Cliente;

/** Formulario reutilizado para crear y editar; conserva los datos si la validación falla. */
public final class ClienteDialog {
    private ClienteDialog() { }
    public static void mostrar(ClientesActivity activity,Cliente cliente) {
        LinearLayout form=Ui.columna(activity);int p=Ui.dp(activity,22);form.setPadding(p,p,p,0);
        ScrollView scroll=new ScrollView(activity);scroll.addView(form);
        EditText nombre=Ui.campo(activity,form,"Nombre completo",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_WORDS,R.id.nombre_cliente);
        EditText correo=Ui.campo(activity,form,"Correo electrónico",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,R.id.correo_cliente);
        EditText telefono=Ui.campo(activity,form,"Teléfono (opcional)",InputType.TYPE_CLASS_PHONE,R.id.telefono_cliente);
        TextView error=Ui.error(activity,form,R.id.error_cliente);
        if(cliente!=null){nombre.setText(cliente.nombre());correo.setText(cliente.correo());telefono.setText(cliente.telefono());}
        AlertDialog dialog=new AlertDialog.Builder(activity).setTitle(cliente==null?"Nuevo cliente":"Editar cliente")
                .setView(scroll).setNegativeButton("Cancelar",null).setPositiveButton("Guardar",null).create();
        dialog.setOnShowListener(d->dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
            error.setText("");Button guardar=dialog.getButton(AlertDialog.BUTTON_POSITIVE);guardar.setEnabled(false);
            String n=nombre.getText().toString(),c=correo.getText().toString(),t=telefono.getText().toString();
            activity.ejecutar(()->{
                long propietario=activity.app().sesion.usuarioId();
                if(cliente==null)activity.app().clientes.crear(propietario,n,c,t);
                else activity.app().clientes.actualizar(propietario,cliente.id(),n,c,t);
                return true;
            },ok->{dialog.dismiss();activity.cargar();Toast.makeText(activity,"Cliente guardado.",Toast.LENGTH_SHORT).show();},mensaje->{error.setText(mensaje);guardar.setEnabled(true);});
        }));
        dialog.show();
    }
}
