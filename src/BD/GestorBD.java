// Copyright (c) 2025 Adrián Quiroga Linares Lectura y referencia permitidas; reutilización y plagio prohibidos

package BD;

import java.util.*;
import java.io.*;
import java.sql.*;

public class GestorBD {
    private java.sql.Connection conexion;

    public GestorBD() {
        Properties configuracion = new Properties();
        FileInputStream arqConfiguracion;

        try {
            arqConfiguracion = new FileInputStream("baseDatos.properties");
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();

            Properties usuario = new Properties();

            String gestor = configuracion.getProperty("gestor");
            usuario.setProperty("user", configuracion.getProperty("usuario"));
            usuario.setProperty("password", configuracion.getProperty("clave"));

            String urlConexion = "jdbc:" + gestor + "://" +
                    configuracion.getProperty("servidor") + ":" +
                    configuracion.getProperty("puerto") + "/" +
                    configuracion.getProperty("baseDatos");

            System.out.println("Conectando a: " + urlConexion);
            System.out.println("Usuario DB: " + usuario.getProperty("user"));

            this.conexion = java.sql.DriverManager.getConnection(urlConexion, usuario);

            System.out.println("Conexión a base de datos establecida");

        } catch (IOException i) {
            System.err.println("ERROR al leer configuración: " + i.getMessage());
            i.printStackTrace();
        } catch (java.sql.SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            e.printStackTrace();
        }
    }


    public boolean validarUsuario(String usuario, String contrasena) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return false;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // SELECT para validar credenciales
            String sql = "SELECT id, nombre FROM usuarios WHERE nombre = ? AND contrasena = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            rs = ps.executeQuery();
            boolean valido = rs.next();

            if (valido) {
                System.out.println("Login exitoso para usuario: " + usuario);
            } else {
                System.out.println("Login fallido: credenciales incorrectas");
            }

            return valido;

        } catch (SQLException e) {
            System.err.println("ERROR en validación:");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean registrarUsuario(String usuario, String clave) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return false;
        }

        PreparedStatement ps = null;

        try {
            // INSERT para crear nuevo usuario
            String sql = "INSERT INTO usuarios (nombre, contrasena) VALUES (?, ?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, clave);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Usuario '" + usuario + "' registrado correctamente");
                return true;
            } else {
                System.out.println("No se pudo registrar el usuario");
                return false;
            }

        } catch (SQLException e) {
            // Error de usuario duplicado (código 23505 en PostgreSQL)
            if (e.getSQLState().equals("23505")) {
                System.err.println("El usuario '" + usuario + "' ya existe");
            } else {
                System.err.println("ERROR en registro:");
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public Boolean existeUsuario(String usuario) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return false;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conexion.prepareStatement("SELECT nombre FROM usuarios WHERE nombre = ?");
            ps.setString(1, usuario);
            rs = ps.executeQuery();

            boolean existe = rs.next();

            if (existe) {
                System.out.println("Usuario '" + usuario + "' existe");
            } else {
                System.out.println("Usuario '" + usuario + "' no existe");
            }

            return existe;
        } catch (SQLException e) {
            System.err.println("ERROR al buscar usuario:");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void registrarSolicitudAmistad(String clienteSolicitante, String clienteSolicitado) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return;
        }

        PreparedStatement ps = null;

        try {
            ps = conexion.prepareStatement(
                    "SELECT * FROM solicitudes_amistad WHERE solicitante = ? AND solicitado = ?"
            );
            ps.setString(1, clienteSolicitante);
            ps.setString(2, clienteSolicitado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Ya existe una solicitud de " + clienteSolicitante + " a " + clienteSolicitado);
                return;
            }
            rs.close();
            ps.close();

            String sql = "INSERT INTO solicitudes_amistad (solicitante, solicitado, estado) VALUES (?, ?, 'pendiente')";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, clienteSolicitante);
            ps.setString(2, clienteSolicitado);
            ps.executeUpdate();

            System.out.println("✓ Solicitud de amistad registrada: " + clienteSolicitante + " → " + clienteSolicitado);

        } catch (SQLException e) {
            System.err.println("ERROR al registrar solicitud de amistad:");
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void eliminarSolicitudAmistad(String clienteSolicitante, String clienteAceptante) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return;
        }

        PreparedStatement ps = null;

        try {
            String sql = "DELETE FROM solicitudes_amistad WHERE solicitante = ? AND solicitado = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, clienteSolicitante);
            ps.setString(2, clienteAceptante);
            int filasEliminadas = ps.executeUpdate();

            if (filasEliminadas > 0) {
                System.out.println("Solicitud eliminada: " + clienteSolicitante + " → " + clienteAceptante);
            } else {
                System.out.println("No se encontró solicitud para eliminar");
            }

        } catch (SQLException e) {
            System.err.println("ERROR al eliminar solicitud:");
            e.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * REGISTRAR AMISTAD (bidireccional)
     */
    public boolean registrarAmistad(String usuario1, String usuario2) {
        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return false;
        }

        PreparedStatement ps = null;

        try {
            // Insertar en ambas direcciones para amistad bidireccional
            String sql = "INSERT INTO amistades (usuario1, usuario2) VALUES (?, ?), (?, ?)";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, usuario1);
            ps.setString(2, usuario2);
            ps.setString(3, usuario2);
            ps.setString(4, usuario1);
            ps.executeUpdate();

            System.out.println("✓ Amistad registrada: " + usuario1 + " ↔ " + usuario2);
            return true;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                System.err.println("La amistad ya existe entre " + usuario1 + " y " + usuario2);
            } else {
                System.err.println("ERROR al registrar amistad:");
                e.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public List<String> obtenerListaAmigos(String cliente) {
        List<String> amigos = new ArrayList<>();

        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return amigos;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT usuario2 FROM amistades WHERE usuario1 = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, cliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                amigos.add(rs.getString("usuario2"));
            }

            System.out.println("✓ Amigos de '" + cliente + "': " + amigos);

        } catch (SQLException e) {
            System.err.println("ERROR al obtener lista de amigos:");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return amigos;
    }


    public List<String> obtenerSolicitudesAmistad(String cliente) {
        List<String> solicitudes = new ArrayList<>();

        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return solicitudes;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT solicitante FROM solicitudes_amistad WHERE solicitado = ? AND estado = 'pendiente'";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, cliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                solicitudes.add(rs.getString("solicitante"));
            }

            System.out.println("Solicitudes pendientes para '" + cliente + "': " + solicitudes);

        } catch (SQLException e) {
            System.err.println("ERROR al obtener solicitudes:");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return solicitudes;
    }


    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión a base de datos cerrada");
            }
        } catch (SQLException e) {
            System.err.println("ERROR al cerrar conexión:");
            e.printStackTrace();
        }
    }

    public List<String> obtenerListaUsuarios() {
        List<String> usuarios = new ArrayList<>();

        if (conexion == null) {
            System.err.println("ERROR: No hay conexión a la base de datos");
            return usuarios;
        }

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT nombre FROM usuarios";
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                usuarios.add(rs.getString("nombre"));
            }

            System.out.println("Lista de usuarios: " + usuarios);

        } catch (SQLException e) {
            System.err.println("ERROR al obtener lista de usuarios:");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return usuarios;
    }
}