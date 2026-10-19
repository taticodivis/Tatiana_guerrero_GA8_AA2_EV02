package com.codivis.movil.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.View;
import android.widget.*;

/** Componentes visuales reutilizables para las pantallas y los formularios. */
public final class Ui {
    public static final int FONDO=Color.rgb(247,246,252), TINTA=Color.rgb(35,29,58), MUTED=Color.rgb(112,102,132), MORADO=Color.rgb(91,70,214);
    private Ui() { }
    public static int dp(Activity a,float value) { return Math.round(value*a.getResources().getDisplayMetrics().density); }
    public static LinearLayout columna(Activity a) {
        LinearLayout c=new LinearLayout(a);c.setOrientation(LinearLayout.VERTICAL);c.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));return c;
    }
    public static LinearLayout pantalla(Activity a) {
        a.getWindow().setStatusBarColor(FONDO);a.getWindow().setNavigationBarColor(FONDO);
        a.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        ScrollView scroll=new ScrollView(a);scroll.setFillViewport(true);scroll.setBackgroundColor(FONDO);
        LinearLayout c=columna(a);int p=dp(a,24);c.setPadding(p,p,p,p);scroll.addView(c);a.setContentView(scroll);
        int original=c.getPaddingTop(),bottom=c.getPaddingBottom();
        c.setOnApplyWindowInsetsListener((v,insets)->{v.setPadding(p,original+insets.getSystemWindowInsetTop(),p,bottom+insets.getSystemWindowInsetBottom());return insets;});
        return c;
    }
    public static GradientDrawable fondo(int color,int radio,int borde) {
        GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radio);if(borde!=0)d.setStroke(1,borde);return d;
    }
    public static void espacio(Activity a,LinearLayout padre,int alto) { Space s=new Space(a);padre.addView(s,new LinearLayout.LayoutParams(1,dp(a,alto))); }
    public static TextView texto(Activity a,String valor,int size,int color,boolean bold) {
        TextView t=new TextView(a);t.setText(valor);t.setTextSize(size);t.setTextColor(color);t.setLineSpacing(dp(a,3),1);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    public static void cabecera(Activity a,LinearLayout root,String titulo,String descripcion) {
        root.addView(texto(a,"codivis.",29,MORADO,true));espacio(a,root,32);
        root.addView(texto(a,titulo,30,TINTA,true));espacio(a,root,10);
        root.addView(texto(a,descripcion,14,MUTED,false));espacio(a,root,24);
    }
    public static EditText campo(Activity a,LinearLayout parent,String label,int tipo,int id) {
        parent.addView(texto(a,label,13,TINTA,true));espacio(a,parent,7);
        EditText e=new EditText(a);e.setId(id);e.setSingleLine(true);e.setTextSize(16);e.setTextColor(TINTA);e.setHintTextColor(MUTED);
        e.setInputType(tipo);e.setPadding(dp(a,14),dp(a,10),dp(a,14),dp(a,10));e.setMinHeight(dp(a,52));
        e.setBackground(fondo(Color.WHITE,dp(a,12),Color.rgb(221,216,234)));parent.addView(e,new LinearLayout.LayoutParams(-1,-2));espacio(a,parent,17);return e;
    }
    public static Button boton(Activity a,String text,boolean principal,int id) {
        Button b=new Button(a);b.setId(id);b.setText(text);b.setAllCaps(false);b.setTextSize(14);b.setMinHeight(dp(a,50));
        b.setTextColor(principal?Color.WHITE:MORADO);b.setBackground(fondo(principal?MORADO:Color.rgb(237,233,254),dp(a,12),0));return b;
    }
    public static TextView error(Activity a,LinearLayout parent,int id) {
        TextView t=texto(a,"",13,Color.rgb(166,53,69),false);t.setId(id);t.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);parent.addView(t);espacio(a,parent,12);return t;
    }
    public static int tipoClave() { return InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD; }
}
