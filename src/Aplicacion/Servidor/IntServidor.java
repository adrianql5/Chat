// Copyright (c) 2025 Adrián Quiroga Linares Lectura y referencia permitidas; reutilización y plagio prohibidos

package Aplicacion.Servidor;

import Aplicacion.Cliente.IntCliente;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IntServidor extends Remote {

    /**
     * Autentica y registra un cliente en el servidor.
     *
     * Verifica las credenciales del usuario, lo añade a la lista de clientes activos.
     * Se les notifica a sus amigos conectados, sus amigos conectados.
     * Se le notifica al cliente sus amigos, amigos online y solicitudes pendientes.
     *
     * @param nombre el nombre de usuario
     * @param contraseña la contraseña del usuario
     * @param cliente la interfaz remota del cliente para que pueda recibir notificaciones
     * @return true si la autenticación y conexión son exitosas, false en caso contrario
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean conectarCliente(String nombre, String contraseña, IntCliente cliente) throws RemoteException;

    /**
     * Desconecta un cliente del servidor.
     *
     * Elimina al cliente de la lista de clientes activos y notifica a sus amigos conectados
     * sobre la desconexión.
     *
     * @param nombre el nombre de usuario del cliente a desconectar
     * @return true si la desconexión es exitosa, false si el cliente no estaba conectado
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean desconectarCliente(String nombre) throws RemoteException;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * Añade un nuevo usuario a la base de datos si el nombre de usuario no está ya en uso.
     *
     * @param nombre el nombre de usuario deseado
     * @param contraseña la contraseña del usuario
     * @return true si el registro es exitoso, false si el nombre de usuario ya existe
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean registrarUsuario(String nombre, String contraseña) throws RemoteException;

    /**
     * Modifica la contraseña de un usuario existente.
     *
     * Verifica la contraseña antigua antes de actualizarla a la nueva contraseña proporcionada.
     *
     * @param nombre el nombre de usuario
     * @param ContraseñaVieja la contraseña actual del usuario
     * @param nuevaContraseña la nueva contraseña que se desea establecer
     * @return true si la contraseña se modifica exitosamente, false si la verificación falla
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean modificarContraseña(String nombre, String ContraseñaVieja, String nuevaContraseña) throws RemoteException;

    /**
     * Obtiene una lista de usuarios cuyo nombre coincida parcialmente con el patrón de búsqueda.
     *
     * @param nombreParcial el fragmento del nombre de usuario a buscar
     * @return una lista de nombres de usuario que coinciden con el criterio de búsqueda
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    List<String> obtenerUsuarios(String nombreParcial) throws RemoteException;

    /**
     * Envía una solicitud de amistad de un cliente a otro. Registra en la base de datos
     * la solicitud pendiente. Notifica la solicitud al cliente solicitado si está conectado
     * y le notifica las solicitudes pendientes.
     *
     * @param clienteSolicitante el nombre del cliente que envía la solicitud
     * @param clienteSolicitado el nombre del cliente que recibe la solicitud
     * @return true si la solicitud se envía exitosamente, false si ocurre un error
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean solicitarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;

    /**
     * Acepta una solicitud de amistad entre dos clientes.
     * Registra en la base de datosla nueva amistad y elimina la solicitud pendiente.
     * Notifica al solicitante que su solicitud ha sido aceptada, sus amigos conectados y sus amigos.
     * Notifica al solicitado que su lista de amigos conectados, amigos y solicitudes pendientes.
     *
     * @param clienteSolicitante el nombre del cliente que envió la solicitud
     * @param clienteSolicitado el nombre del cliente que acepta la solicitud
     * @return true si la solicitud se acepta exitosamente, false si ocurre un error
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean aceptarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;

    /**
     * Rechaza una solicitud de amistad entre dos clientes.
     * Elimina la solicitud pendiente de la base de datos.
     * Notifica al solicitado que su lista de solicitudes pendientes ha sido actualizada.
     *
     * @param clienteSolicitante el nombre del cliente que envió la solicitud
     * @param clienteSolicitado el nombre del cliente que rechaza la solicitud
     * @return true si la solicitud se rechaza exitosamente, false si ocurre un error
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    boolean rechazarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException;
}
