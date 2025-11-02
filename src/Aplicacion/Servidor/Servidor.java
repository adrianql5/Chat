// Copyright (c) 2025 Adrián Quiroga Linares Lectura y referencia permitidas; reutilización y plagio prohibidos

package Aplicacion.Servidor;

import BD.GestorBD;

import java.rmi.RemoteException;
import java.rmi.registry.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            GestorBD gestorBD = new GestorBD();

            IntServidor servidor = new ImplServidor(gestorBD);

            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Registro RMI creado en puerto 1099");
            } catch (RemoteException e) {
                System.out.println("Registro RMI ya existe, obteniendo referencia...");
                registry = LocateRegistry.getRegistry(1099);
            }

            registry.rebind("Servidor", servidor);
            System.out.println("Servidor iniciado y registrado correctamente...");
            System.out.println("Esperando conexiones de clientes...");

        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor");
            e.printStackTrace();
        }
    }
}