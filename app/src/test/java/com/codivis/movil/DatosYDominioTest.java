package com.codivis.movil;

import android.app.Application;
import android.content.Context;
import com.codivis.movil.datos.*;
import com.codivis.movil.dominio.*;
import com.codivis.movil.modelo.*;
import java.util.List;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

/** Pruebas locales contra SQLite real del entorno Robolectric, con datos aislados. */
@RunWith(RobolectricTestRunner.class)
@Config(sdk=28,application=Application.class)
public class DatosYDominioTest {
    private Context contexto;
    private BaseDatos base;
    private SQLiteRepositorio repo;
    private AutenticacionService auth;
    private ClienteService clientes;
    private long pedro,luis;
    @Before public void preparar() {
        contexto=RuntimeEnvironment.getApplication();contexto.deleteDatabase("codivis.db");
        base=new BaseDatos(contexto);repo=new SQLiteRepositorio(base);
        auth=new AutenticacionService(repo,new PasswordHasher());clientes=new ClienteService(repo);
        pedro=repo.crearUsuario("pedro","hash_solo_fixture");luis=repo.crearUsuario("luis","hash_solo_fixture");
    }
    @After public void cerrar(){base.close();contexto.deleteDatabase("codivis.db");}
    @Test public void registroNormalizaYProtegeLaClave(){
        long id=auth.registrar("  MARIA  ","Codivis2026!","Codivis2026!");
        assertTrue(id>0);Usuario u=repo.buscarUsuario("maria");assertNotNull(u);assertNotEquals("Codivis2026!",u.claveHash());
    }
    @Test public void noPermiteUsuarioDuplicado(){assertThrows(ReglaException.class,()->auth.registrar(" PEDRO ","Codivis2026!","Codivis2026!"));}
    @Test public void validaLongitudDeUsuario(){assertThrows(ReglaException.class,()->auth.registrar("ab","Codivis2026!","Codivis2026!"));}
    @Test public void validaCaracteresDeUsuario(){assertThrows(ReglaException.class,()->auth.registrar("Pedro Luis","Codivis2026!","Codivis2026!"));}
    @Test public void validaClaveCorta(){assertThrows(ReglaException.class,()->auth.registrar("ana","123","123"));}
    @Test public void validaConfirmacion(){assertThrows(ReglaException.class,()->auth.registrar("ana","Codivis2026!","diferente"));}
    @Test public void autenticaUnaCuentaPersistida(){
        auth.registrar("ana","Codivis2026!","Codivis2026!");base.close();base=new BaseDatos(contexto);repo=new SQLiteRepositorio(base);
        AutenticacionService reabierto=new AutenticacionService(repo,new PasswordHasher());assertEquals("ana",reabierto.iniciarSesion(" ANA ","Codivis2026!").nombre());
    }
    @Test public void loginRechazaClaveIncorrecta(){auth.registrar("ana","Codivis2026!","Codivis2026!");assertThrows(ReglaException.class,()->auth.iniciarSesion("ana","incorrecta"));}
    @Test public void loginRechazaUsuarioInexistente(){assertThrows(ReglaException.class,()->auth.iniciarSesion("nadie","Codivis2026!"));}
    @Test public void loginRechazaDatosVacios(){assertThrows(ReglaException.class,()->auth.iniciarSesion("",""));}
    @Test public void creaClienteConDatosNormalizados(){
        long id=clientes.crear(pedro,"  Pedro Luis  "," PEDRO@EXAMPLE.COM ","3001234567");Cliente c=clientes.obtener(pedro,id);
        assertEquals("Pedro Luis",c.nombre());assertEquals("pedro@example.com",c.correo());assertEquals(pedro,c.usuarioId());
    }
    @Test public void validaNombreObligatorio(){assertThrows(ReglaException.class,()->clientes.crear(pedro,"","a@example.com",""));}
    @Test public void validaCorreo(){assertThrows(ReglaException.class,()->clientes.crear(pedro,"Ana","sin-arroba",""));}
    @Test public void validaTelefono(){assertThrows(ReglaException.class,()->clientes.crear(pedro,"Ana","ana@example.com","abc"));}
    @Test public void permiteTelefonoVacio(){long id=clientes.crear(pedro,"Ana","ana@example.com","");assertEquals("",clientes.obtener(pedro,id).telefono());}
    @Test public void impideCorreoDuplicadoEnMismaCuenta(){clientes.crear(pedro,"Ana","ana@example.com","");assertThrows(ReglaException.class,()->clientes.crear(pedro,"Otra","ANA@EXAMPLE.COM",""));}
    @Test public void permiteMismoCorreoEnCuentasSeparadas(){clientes.crear(pedro,"Ana","ana@example.com","");assertTrue(clientes.crear(luis,"Ana","ana@example.com","")>0);}
    @Test public void buscaPorNombreYCorreo(){
        clientes.crear(pedro,"María Pérez","maria@example.com","");clientes.crear(pedro,"Luis","luis@example.com","");
        assertEquals(1,clientes.listar(pedro,"MARÍA").size());assertEquals(1,clientes.listar(pedro,"luis@").size());assertTrue(clientes.listar(pedro,"nadie").isEmpty());
    }
    @Test public void listaSoloClientesDelUsuario(){clientes.crear(pedro,"Ana","ana@example.com","");clientes.crear(luis,"Luis","luis@example.com","");assertEquals(1,clientes.listar(pedro,"").size());}
    @Test public void actualizaYConservaId(){long id=clientes.crear(pedro,"Ana","ana@example.com","");clientes.actualizar(pedro,id,"Ana María","nueva@example.com","3101234567");Cliente c=clientes.obtener(pedro,id);assertEquals(id,c.id());assertEquals("3101234567",c.telefono());assertEquals("Ana María",c.nombre());}
    @Test public void puedeEditarSinCambiarCorreo(){long id=clientes.crear(pedro,"Ana","ana@example.com","");clientes.actualizar(pedro,id,"Ana María","ana@example.com","");assertEquals("Ana María",clientes.obtener(pedro,id).nombre());}
    @Test public void noActualizaConCorreoDeOtroCliente(){long id=clientes.crear(pedro,"Ana","ana@example.com","");clientes.crear(pedro,"Luis","luis@example.com","");assertThrows(ReglaException.class,()->clientes.actualizar(pedro,id,"Ana","luis@example.com",""));assertEquals("ana@example.com",clientes.obtener(pedro,id).correo());}
    @Test public void eliminaYNoSePuedeVolverAConsultar(){long id=clientes.crear(pedro,"Ana","ana@example.com","");clientes.eliminar(pedro,id);assertTrue(clientes.listar(pedro,"").isEmpty());assertThrows(ReglaException.class,()->clientes.obtener(pedro,id));}
    @Test public void otraCuentaNoPuedeConsultarEditarOEliminar(){long id=clientes.crear(pedro,"Ana","ana@example.com","");assertThrows(ReglaException.class,()->clientes.obtener(luis,id));assertThrows(ReglaException.class,()->clientes.actualizar(luis,id,"Otra","otra@example.com",""));assertThrows(ReglaException.class,()->clientes.eliminar(luis,id));assertNotNull(clientes.obtener(pedro,id));}
    @Test public void sinSesionNoPuedeOperar(){assertThrows(ReglaException.class,()->clientes.listar(0,""));assertThrows(ReglaException.class,()->clientes.crear(0,"Ana","a@example.com",""));assertThrows(ReglaException.class,()->clientes.obtener(0,1));assertThrows(ReglaException.class,()->clientes.actualizar(0,1,"Ana","a@example.com",""));assertThrows(ReglaException.class,()->clientes.eliminar(0,1));}
    @Test public void conservaClientesAlReabrirBase(){long id=clientes.crear(pedro,"Ana","ana@example.com","");base.close();base=new BaseDatos(contexto);ClienteService nuevo=new ClienteService(new SQLiteRepositorio(base));assertEquals("Ana",nuevo.obtener(pedro,id).nombre());}
    @Test public void baseImpideClienteConPropietarioInexistente(){assertThrows(ReglaException.class,()->repo.crearCliente(new Cliente(0,999,"Ana","ana@example.com","")));}
}
