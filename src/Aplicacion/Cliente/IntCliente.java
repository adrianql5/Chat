package Aplicacion.Cliente;

import java.rmi.Remote;
import java.util.List;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface IntCliente extends Remote{

    /**
     * Notifica al cliente sobre los amigos que están conectados.
     *
     * @param amigosConectados Un hashmap que contiene los nombres de usuario de los amigos conectados y sus respectivas referencias de IntCliente.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void serNotificadoAmigosConectados(ConcurrentHashMap<String, IntCliente> amigosConectados)throws RemoteException;

    /**
     * Notifica al cliente sobre su lista de amigos.
     *
     * @param amigos Una lista de nombres de usuario de los amigos del cliente.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void serNotificadoAmigos(List<String> amigos)throws RemoteException;

    /**
     * Notifica al cliente sobre sus solicitudes de amistad pendientes.
     *
     * @param solicitudesPendientes Una lista de nombres de usuario de los clientes que han enviado solicitudes de amistad pendientes.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void serNotificadoSolicitudesPendientes(List<String> solicitudesPendientes)throws RemoteException;

    /**
     * Recibe un mensaje de otro cliente.
     * Si clienteA quiere enviar un mensaje a clienteB,
     * clienteA haría clienteB.recibirMensaje;
     *
     * @param mensaje El contenido del mensaje recibido.
     * @param emisario El nombre de usuario del cliente que envió el mensaje.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void recibirMensaje(String mensaje, String emisario) throws RemoteException;

    /**
     * Notifica al cliente que su solicitud de amistad ha sido aceptada.
     *
     * @param clienteAceptante El nombre de usuario del cliente que aceptó la solicitud de amistad.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void serNotificadoAceptacionSolicitud(String clienteAceptante) throws RemoteException;;

    /**
     * Notifica al cliente sobre una nueva solicitud de amistad recibida.
     *
     * @param clienteSolicitante El nombre de usuario del cliente que envió la solicitud de amistad.
     * @throws RemoteException Si ocurre un error en la comunicación remota.
     */
    void serNotificadoNuevaSolicitud(String clienteSolicitante) throws RemoteException;
}
