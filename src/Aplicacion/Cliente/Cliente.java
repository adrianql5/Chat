package Aplicacion.Cliente;

import Aplicacion.Servidor.IntServidor;
import GUI.GUI;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {
    private static IntServidor servidor;
    private static IntCliente intCliente;
    private static GUI gui;

    public static void main(String[] args) {
        try {
            // Conectar al registro RMI del servidor
            Registry registro = LocateRegistry.getRegistry("localhost", 1099);
            servidor = (IntServidor) registro.lookup("Servidor");

            // Crear GUI primero (sin referencias)
            gui = new GUI();

            try {
                intCliente = new ImplCliente(gui);
                System.out.println("Cliente RMI creado");

            } catch(RemoteException e) {
                System.err.println("Error al crear cliente RMI");
                intCliente = null;
                e.printStackTrace();
            }

            // Configurar las referencias en GUI
            gui.setServidor(servidor);
            gui.setCliente(intCliente);

            // Mostrar GUI
            gui.setVisible(true);

            // Agregar shutdown hook para desconectar al cerrar
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (servidor != null && gui.getNombreUsuario() != null) {
                        servidor.desconectarCliente(gui.getNombreUsuario());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }));

        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error al conectar con el servidor");
            System.exit(1);
        }
    }

    public static IntServidor getServidor() {
        return servidor;
    }

    public static IntCliente getCliente() {
        return intCliente;
    }
}