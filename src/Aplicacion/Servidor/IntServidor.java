package Aplicacion.Servidor;

import Aplicacion.Cliente.IntCliente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IntServidor extends Remote {

    /*
    * Método que permite a un usuario conectarse al servidor.
    * Verifica sus credenciales y lo añade a la lista de clientes conectados.
    * Notifica a los demás clientes sobre el nuevo usuario conectado.
    */
    boolean conectarCliente(String nombre, String contraseña, IntCliente cliente) throws RemoteException;


    boolean desconectarCliente(String nombre) throws RemoteException;

    boolean registrarUsuario(String nombre, String contraseña) throws RemoteException;

    boolean modificarContraseña(String nombre, String ContraseñaVieja, String nuevaContraseña) throws RemoteException;

    List<String> obtenerUsuarios(String nombreParcial) throws RemoteException;

    boolean solicitarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;

    boolean aceptarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;

    boolean rechazarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;
}
