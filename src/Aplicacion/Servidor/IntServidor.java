package Aplicacion.Servidor;

import Aplicacion.Cliente.IntCliente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IntServidor extends Remote {
    boolean conectarCliente(String nombre, String contraseña, IntCliente cliente) throws RemoteException;
    boolean desconectarCliente(String nombre) throws RemoteException;
    boolean registrarUsuario(String nombre, String contraseña) throws RemoteException;

    List<String> obtenerUsuarios(String nombreParcial) throws RemoteException;

    void modificarContraseña(String nombre, String nuevaContraseña) throws RemoteException;

    boolean solicitarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;
    boolean aceptarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;
    boolean rechazarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;
}
