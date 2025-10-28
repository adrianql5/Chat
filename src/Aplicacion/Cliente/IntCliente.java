package Aplicacion.Cliente;

import java.rmi.Remote;
import java.util.List;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface IntCliente extends Remote{
    void serNotificadoUsuariosRegistrados(List<String> usuarios) throws RemoteException;
    void serNotificadoAmigosConectados(ConcurrentHashMap<String, IntCliente> amigosConectados)throws RemoteException;
    void serNotificadoAmigos(List<String> amigos)throws RemoteException;
    void serNotificadoSolicitudesPendientes(List<String> solicitudesPendientes)throws RemoteException;

    void recibirMensaje(String mensaje, String emisario) throws RemoteException;

    void serNotificadoAceptacionSolicitud(String clienteAceptante) throws RemoteException;;
    void serNotificadoNuevaSolicitud(String clienteSolicitante) throws RemoteException;
}
