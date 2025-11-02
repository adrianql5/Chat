// Copyright (c) 2025 Adrián Quiroga Linares Lectura y referencia permitidas; reutilización y plagio prohibidos

package Aplicacion.Cliente;

import GUI.GUI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ImplCliente extends UnicastRemoteObject implements IntCliente{
    private GUI gui;

    public ImplCliente(GUI gui) throws RemoteException {
        super();
        this.gui = gui;
    }

    @Override
    public void recibirMensaje(String mensaje, String emisario) throws RemoteException {
        System.out.println("Mensaje recibido de " + emisario + ": " + mensaje);
        if (gui != null) {
            gui.añadirMensajeRecibido(mensaje, emisario);
        }
    }

    @Override
    public void serNotificadoAmigosConectados(ConcurrentHashMap<String, IntCliente> amigosConectados)throws RemoteException{
        System.out.println("Notificación recibida - Usuarios conectados: " + amigosConectados.keySet());
        if (gui != null) {
            gui.actualizarAmigosConectados(amigosConectados);
        }
    }

    @Override
    public void serNotificadoAmigos(List<String> amigos)throws RemoteException{
        System.out.println("Notificación recibida - Amigos: " + amigos);
        if (gui != null) {
            gui.actualizarAmigos(new ArrayList<>(amigos));
        }

    }

    @Override
    public void serNotificadoSolicitudesPendientes(List<String> solicitudesPendientes)throws RemoteException{
        System.out.println("Notificación recibida - Solicitudes pendientes: " + solicitudesPendientes);
        if (gui != null) {
            gui.actualizarSolicitudesPendientes(new ArrayList<>(solicitudesPendientes));
        }
    }

    @Override
    public void serNotificadoNuevaSolicitud(String usuarioSolicitante) throws RemoteException {
        System.out.println("Nueva solicitud de amistad de: " + usuarioSolicitante);
        if (gui != null) {
            gui.añadirSolicitudAmistad(usuarioSolicitante);
        }
    }

    @Override
    public void serNotificadoAceptacionSolicitud(String usuarioAceptante) throws RemoteException {
        System.out.println(usuarioAceptante + " aceptó tu solicitud de amistad");
        if (gui != null) {
            gui.notificarAceptacionSolicitud(usuarioAceptante);
        }
    }
}
