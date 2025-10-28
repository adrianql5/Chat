package Aplicacion.Servidor;

import Aplicacion.Cliente.IntCliente;
import BD.GestorBD;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImplServidor extends UnicastRemoteObject implements IntServidor {
    private ConcurrentHashMap<String, IntCliente> clientesConectados = new ConcurrentHashMap();
    private GestorBD gestorBD;

    public ImplServidor(GestorBD gestorBD) throws  RemoteException {
        super();
        this.gestorBD = gestorBD;
    }

    @Override
    public synchronized boolean registrarUsuario(String nombre, String contraseña) throws RemoteException{
        if(gestorBD.existeUsuario(nombre)){
            return false;
        }
        gestorBD.registrarUsuario(nombre, contraseña);
        return true;
    }


    @Override
    public synchronized boolean conectarCliente(String nombre,String contraseña, IntCliente cliente) throws RemoteException{
        if(clientesConectados.containsKey(nombre)){
            return false;
        }

        if(!gestorBD.validarUsuario(nombre, contraseña)){
            return false;
        }


        clientesConectados.put(nombre,cliente);

        for(Map.Entry<String, IntCliente> entry : clientesConectados.entrySet()){
            try {
                entry.getValue().serNotificadoAmigosConectados(getAmigosConectados(entry.getKey()));
                entry.getValue().serNotificadoAmigos(gestorBD.obtenerListaAmigos(entry.getKey()));
                entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(entry.getKey()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public synchronized boolean desconectarCliente(String nombre) throws RemoteException{
        if(!clientesConectados.containsKey(nombre)){
            return false;
        }

        clientesConectados.remove(nombre);

        for(Map.Entry<String, IntCliente> entry : clientesConectados.entrySet()){
            try {
                entry.getValue().serNotificadoAmigosConectados(getAmigosConectados(entry.getKey()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public ConcurrentHashMap<String,IntCliente> getAmigosConectados(String usuario){
        List<String> amigos= gestorBD.obtenerListaAmigos(usuario);
        ConcurrentHashMap<String,IntCliente> amigosConectados = new ConcurrentHashMap<>();
        for(String nombre : amigos){
            if(clientesConectados.containsKey(nombre)){
                amigosConectados.put(nombre,clientesConectados.get(nombre));
            }
        }
        return amigosConectados;
    }



    @Override
    public boolean solicitarAmistad(String clienteSolicitante, String clienteSolicitado) throws RemoteException {
        if(gestorBD.existeUsuario(clienteSolicitante) && gestorBD.existeUsuario(clienteSolicitado)){
            gestorBD.registrarSolicitudAmistad(clienteSolicitante, clienteSolicitado);

            for(Map.Entry<String, IntCliente> entry : clientesConectados.entrySet()){
                if(entry.getKey().equals(clienteSolicitado)){
                    try {
                        entry.getValue().serNotificadoNuevaSolicitud(clienteSolicitante);
                        entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(clienteSolicitado));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean aceptarAmistad(String clienteSolicitante, String clienteAceptante) throws RemoteException {
        if (gestorBD.existeUsuario(clienteSolicitante) && gestorBD.existeUsuario(clienteAceptante)) {

            gestorBD.registrarAmistad(clienteSolicitante, clienteAceptante);

            gestorBD.eliminarSolicitudAmistad(clienteSolicitante, clienteAceptante);

            for (Map.Entry<String, IntCliente> entry : clientesConectados.entrySet()) {
                if (entry.getKey().equals(clienteSolicitante)) {
                    try {
                        entry.getValue().serNotificadoAceptacionSolicitud(clienteAceptante);
                        entry.getValue().serNotificadoAmigosConectados(getAmigosConectados(entry.getKey()));
                        entry.getValue().serNotificadoAmigos(gestorBD.obtenerListaAmigos(entry.getKey()));
                        entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(entry.getKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(entry.getKey().equals(clienteAceptante)){
                    try {
                        entry.getValue().serNotificadoAmigosConectados(getAmigosConectados(entry.getKey()));
                        entry.getValue().serNotificadoAmigos(gestorBD.obtenerListaAmigos(entry.getKey()));
                        entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(entry.getKey()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean rechazarAmistad(String clienteSolicitante, String clienteAceptante) throws RemoteException{
        if (gestorBD.existeUsuario(clienteSolicitante) && gestorBD.existeUsuario(clienteAceptante)) {
            gestorBD.eliminarSolicitudAmistad(clienteSolicitante, clienteAceptante);

            for (Map.Entry<String, IntCliente> entry : clientesConectados.entrySet()) {
                if (entry.getKey().equals(clienteSolicitante)) {
                    try {
                        entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(clienteAceptante));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(entry.getKey().equals(clienteAceptante)){
                    try {
                        entry.getValue().serNotificadoSolicitudesPendientes(gestorBD.obtenerSolicitudesAmistad(entry.getKey()));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> obtenerUsuarios(String nombreParcial) throws RemoteException {
        return gestorBD.obtenerUsuariosPorPatron(nombreParcial);
    }

    @Override
    public boolean modificarContraseña(String nombre, String contraseñaVieja, String nuevaContraseña) throws RemoteException {
        if(!gestorBD.validarUsuario(nombre, contraseñaVieja)){
            return false;
        }
        return gestorBD.modificarContraseña(nombre, nuevaContraseña);
    }
}
