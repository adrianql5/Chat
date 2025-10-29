package GUI;

import Aplicacion.Cliente.IntCliente;
import Aplicacion.Servidor.IntServidor;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.List;

public class GUI extends javax.swing.JFrame {
    private IntServidor servidor;
    private IntCliente cliente;
    private String nombreUsuario;

    private ArrayList<String> mensajesRecibidos;
    private ArrayList<String> mensajesEnviados;
    private ConcurrentHashMap<String, IntCliente> amigosOnline;
    private ArrayList<String> usuariosRegistrados;
    private ArrayList<String> amigos;
    private ArrayList<String> solicitudesPendientes;

    // Componentes de la interfaz
    private JTabbedPane tabbedPane;

    // Panel de Login/Registro
    private JPanel panelLogin;
    private JTextField txtUsuarioLogin;
    private JPasswordField txtPasswordLogin;
    private JButton btnLogin;
    private JButton btnMostrarRegistro;

    // Panel de Registro
    private JPanel panelRegistro;
    private JTextField txtUsuarioRegistro;
    private JPasswordField txtPasswordRegistro;
    private JPasswordField txtConfirmarPassword;
    private JButton btnRegistrar;
    private JButton btnVolverLogin;

    // Panel de Chat
    private JPanel panelChat;
    private JTextArea areaMensajes;
    private JTextField txtMensaje;
    private JButton btnEnviar;
    private JList<String> listaAmigosOnline;
    private DefaultListModel<String> modeloAmigosOnline;

    // Panel de Amigos
    private JPanel panelAmigos;
    private JList<String> listaUsuariosRegistrados;
    private DefaultListModel<String> modeloUsuariosRegistrados;
    private JButton btnSolicitarAmistad;
    private JList<String> listaAmigos;
    private DefaultListModel<String> modeloAmigos;
    private JTextField txtBuscarUsuario;
    private JButton btnBuscarUsuario;

    // Panel de Solicitudes
    private JPanel panelSolicitudes;
    private JList<String> listaSolicitudes;
    private DefaultListModel<String> modeloSolicitudes;
    private JButton btnAceptarSolicitud;
    private JButton btnRechazarSolicitud;

    // Panel de Perfil
    private JPanel panelPerfil;
    private JLabel lblNombreUsuarioPerfil;
    private JPasswordField txtContraseñaActual;
    private JPasswordField txtNuevaContraseña;
    private JPasswordField txtConfirmarNuevaContraseña;
    private JButton btnCambiarContraseña;

    public GUI() {
        this(null, null);
    }

    public GUI(IntServidor servidor, IntCliente cliente) {
        this.servidor = servidor;
        this.cliente = cliente;
        mensajesRecibidos = new ArrayList<>();
        mensajesEnviados = new ArrayList<>();
        amigosOnline = new ConcurrentHashMap<>();
        usuariosRegistrados = new ArrayList<>();
        amigos = new ArrayList<>();
        solicitudesPendientes = new ArrayList<>();

        initComponents();
    }

    private void initComponents() {
        setTitle("Sistema de Chat RMI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Desconectar cuando se cierra la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconectarCliente();
            }
        });

        // Crear CardLayout para alternar entre login y aplicación principal
        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Crear paneles
        JPanel panelAuth = crearPanelAutenticacion();
        JPanel panelApp = crearPanelAplicacion();

        mainPanel.add(panelAuth, "AUTH");
        mainPanel.add(panelApp, "APP");

        add(mainPanel);

        // Iniciar en panel de autenticación
        cardLayout.show(mainPanel, "AUTH");

        // Listener para cambiar entre paneles
        btnLogin.addActionListener(e -> {
            if (iniciarSesion()) {
                cardLayout.show(mainPanel, "APP");
            }
        });

        btnMostrarRegistro.addActionListener(e -> {
            CardLayout cl = (CardLayout) panelAuth.getLayout();
            cl.show(panelAuth, "REGISTRO");
        });

        btnVolverLogin.addActionListener(e -> {
            CardLayout cl = (CardLayout) panelAuth.getLayout();
            cl.show(panelAuth, "LOGIN");
        });

        btnRegistrar.addActionListener(e -> {
            if (registrarUsuario()) {
                CardLayout cl = (CardLayout) panelAuth.getLayout();
                cl.show(panelAuth, "LOGIN");
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente. Por favor inicia sesión.");
            }
        });
    }

    private void desconectarCliente() {
        try {
            if (servidor != null && nombreUsuario != null && cliente != null) {
                servidor.desconectarCliente(nombreUsuario);
                System.out.println("✓ Cliente desconectado: " + nombreUsuario);
            }
        } catch (RemoteException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel crearPanelAutenticacion() {
        JPanel panel = new JPanel(new CardLayout());

        // Panel de Login
        panelLogin = new JPanel(new GridBagLayout());
        panelLogin.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelLogin.add(lblTitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelLogin.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        txtUsuarioLogin = new JTextField(20);
        panelLogin.add(txtUsuarioLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelLogin.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtPasswordLogin = new JPasswordField(20);
        panelLogin.add(txtPasswordLogin, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setPreferredSize(new Dimension(200, 30));
        panelLogin.add(btnLogin, gbc);

        gbc.gridy = 4;
        btnMostrarRegistro = new JButton("Crear Cuenta Nueva");
        panelLogin.add(btnMostrarRegistro, gbc);

        // Panel de Registro
        panelRegistro = new JPanel(new GridBagLayout());
        panelRegistro.setBorder(new EmptyBorder(20, 20, 20, 20));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTituloRegistro = new JLabel("Crear Cuenta");
        lblTituloRegistro.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelRegistro.add(lblTituloRegistro, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelRegistro.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        txtUsuarioRegistro = new JTextField(20);
        panelRegistro.add(txtUsuarioRegistro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panelRegistro.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtPasswordRegistro = new JPasswordField(20);
        panelRegistro.add(txtPasswordRegistro, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panelRegistro.add(new JLabel("Confirmar:"), gbc);

        gbc.gridx = 1;
        txtConfirmarPassword = new JPasswordField(20);
        panelRegistro.add(txtConfirmarPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setPreferredSize(new Dimension(200, 30));
        panelRegistro.add(btnRegistrar, gbc);

        gbc.gridy = 5;
        btnVolverLogin = new JButton("Volver al Login");
        panelRegistro.add(btnVolverLogin, gbc);

        panel.add(panelLogin, "LOGIN");
        panel.add(panelRegistro, "REGISTRO");

        return panel;
    }


    public boolean iniciarSesion() {
        String usuario = txtUsuarioLogin.getText().trim();
        String password = new String(txtPasswordLogin.getPassword());
        System.out.println("Usuario: " + usuario);
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if(servidor.conectarCliente(usuario, password, cliente)) {
                nombreUsuario = usuario;
                lblNombreUsuarioPerfil.setText(nombreUsuario);
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales inválidas o usuario ya conectado", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private boolean registrarUsuario() {
        String usuario = txtUsuarioRegistro.getText().trim();
        String password = new String(txtPasswordRegistro.getPassword());
        String confirmar = new String(txtConfirmarPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty() || confirmar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!password.equals(confirmar)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (servidor != null) {
                if (servidor.registrarUsuario(usuario, password)) {
                    txtUsuarioRegistro.setText("");
                    txtPasswordRegistro.setText("");
                    txtConfirmarPassword.setText("");
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar usuario", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }

    private JPanel crearPanelAplicacion() {
        JPanel panel = new JPanel(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Crear pestañas
        tabbedPane.addTab("Chat", crearPanelChat());
        tabbedPane.addTab("Amigos", crearPanelAmigos());
        tabbedPane.addTab("Solicitudes", crearPanelSolicitudes());
        tabbedPane.addTab("Perfil", crearPanelPerfil());  // Nueva pestaña

        tabbedPane.addChangeListener(e -> actualizarTodo());

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelChat() {
        panelChat = new JPanel(new BorderLayout(10, 10));
        panelChat.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel izquierdo - Lista de amigos
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setPreferredSize(new Dimension(200, 0));

        JLabel lblAmigos = new JLabel("Amigos conectados");
        lblAmigos.setHorizontalAlignment(SwingConstants.CENTER);
        panelIzquierdo.add(lblAmigos, BorderLayout.NORTH);

        modeloAmigosOnline = new DefaultListModel<>();
        listaAmigosOnline = new JList<>(modeloAmigosOnline);
        listaAmigosOnline.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollAmigos = new JScrollPane(listaAmigosOnline);
        panelIzquierdo.add(scrollAmigos, BorderLayout.CENTER);

        // Panel central - Área de mensajes
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));

        areaMensajes = new JTextArea();
        areaMensajes.setEditable(false);
        areaMensajes.setLineWrap(true);
        areaMensajes.setWrapStyleWord(true);
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        panelCentral.add(scrollMensajes, BorderLayout.CENTER);

        // Panel inferior - Enviar mensajes
        JPanel panelEnviar = new JPanel(new BorderLayout(5, 5));
        txtMensaje = new JTextField();
        btnEnviar = new JButton("Enviar");

        txtMensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMensaje();
                }
            }
        });

        btnEnviar.addActionListener(e -> enviarMensaje());

        panelEnviar.add(txtMensaje, BorderLayout.CENTER);
        panelEnviar.add(btnEnviar, BorderLayout.EAST);

        panelCentral.add(panelEnviar, BorderLayout.SOUTH);

        panelChat.add(panelIzquierdo, BorderLayout.WEST);
        panelChat.add(panelCentral, BorderLayout.CENTER);

        return panelChat;
    }

    private void enviarMensaje() {
        String destinatario = listaAmigosOnline.getSelectedValue();
        String mensaje = txtMensaje.getText().trim();

        if (destinatario == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un amigo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (mensaje.isEmpty()) {
            return;
        }

        try {
            if (servidor != null) {
                if (!amigosOnline.containsKey(destinatario)) {
                    JOptionPane.showMessageDialog(this,
                            "Este usuario no está conectado o no es tu amigo",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                IntCliente clienteDestinatario = amigosOnline.get(destinatario);
                if (clienteDestinatario != null) {
                    clienteDestinatario.recibirMensaje(mensaje, nombreUsuario);

                    SwingUtilities.invokeLater(() -> {
                        areaMensajes.append("[Tú -> " + destinatario + "]: " + mensaje + "\n");
                        txtMensaje.setText("");
                    });

                    añadirMensajeEnviado(mensaje);
                } else {
                    JOptionPane.showMessageDialog(this, "El usuario no está conectado", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error al enviar mensaje", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void añadirMensajeRecibido(String mensaje, String emisor) {
        mensajesRecibidos.add(mensaje);
        SwingUtilities.invokeLater(() -> {
            areaMensajes.append("[" + emisor + "]: " + mensaje + "\n");
        });
    }

    public void añadirMensajeEnviado(String mensaje) {
        mensajesEnviados.add(mensaje);
    }

    private JPanel crearPanelAmigos() {
        panelAmigos = new JPanel(new GridLayout(1, 2, 10, 10));
        panelAmigos.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel izquierdo - TODOS LOS USUARIOS
        JPanel panelUsuarios = new JPanel(new BorderLayout(5, 5));

        JLabel lblUsuarios = new JLabel("Todos los Usuarios");
        lblUsuarios.setHorizontalAlignment(SwingConstants.CENTER);
        panelUsuarios.add(lblUsuarios, BorderLayout.NORTH);

        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(5, 5));
        panelBusqueda.setBorder(new EmptyBorder(5, 5, 5, 5));

        txtBuscarUsuario = new JTextField();
        txtBuscarUsuario.setToolTipText("Ingrese nombre parcial del usuario");

        // Listener para buscar al presionar Enter
        txtBuscarUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarUsuarios();
                }
            }
        });

        btnBuscarUsuario = new JButton("Buscar");
        btnBuscarUsuario.addActionListener(e -> buscarUsuarios());

        panelBusqueda.add(txtBuscarUsuario, BorderLayout.CENTER);
        panelBusqueda.add(btnBuscarUsuario, BorderLayout.EAST);

        panelUsuarios.add(panelBusqueda, BorderLayout.NORTH);

        modeloUsuariosRegistrados = new DefaultListModel<>();
        listaUsuariosRegistrados = new JList<>(modeloUsuariosRegistrados);
        JScrollPane scrollUsuarios = new JScrollPane(listaUsuariosRegistrados);
        panelUsuarios.add(scrollUsuarios, BorderLayout.CENTER);

        btnSolicitarAmistad = new JButton("Enviar Solicitud de Amistad");
        btnSolicitarAmistad.addActionListener(e -> enviarSolicitudAmistad());
        panelUsuarios.add(btnSolicitarAmistad, BorderLayout.SOUTH);

        // Panel derecho - Mis amigos
        JPanel panelMisAmigos = new JPanel(new BorderLayout(5, 5));

        JLabel lblMisAmigos = new JLabel("Mis Amigos");
        lblMisAmigos.setHorizontalAlignment(SwingConstants.CENTER);
        panelMisAmigos.add(lblMisAmigos, BorderLayout.NORTH);

        modeloAmigos = new DefaultListModel<>();
        listaAmigos = new JList<>(modeloAmigos);
        JScrollPane scrollMisAmigos = new JScrollPane(listaAmigos);
        panelMisAmigos.add(scrollMisAmigos, BorderLayout.CENTER);

        panelAmigos.add(panelUsuarios);
        panelAmigos.add(panelMisAmigos);

        return panelAmigos;
    }

    private void buscarUsuarios() {
        String nombreParcial = txtBuscarUsuario.getText().trim();

        if (nombreParcial.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un nombre para buscar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (servidor != null) {
                List<String> resultados = servidor.obtenerUsuarios(nombreParcial);

                SwingUtilities.invokeLater(() -> {
                    modeloUsuariosRegistrados.clear();

                    if (resultados.isEmpty()) {
                        JOptionPane.showMessageDialog(this,
                                "No se encontraron usuarios con ese nombre",
                                "Búsqueda",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        for (String usuario : resultados) {
                            if (!usuario.equals(nombreUsuario)) {
                                modeloUsuariosRegistrados.addElement(usuario);
                            }
                        }
                    }
                });
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar usuarios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void enviarSolicitudAmistad() {
        String usuarioSeleccionado = listaUsuariosRegistrados.getSelectedValue();

        if (usuarioSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona un usuario", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuarioSeleccionado.equals(nombreUsuario)) {
            JOptionPane.showMessageDialog(this, "No puedes enviarte solicitud a ti mismo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (servidor != null) {
                if (amigos.contains(usuarioSeleccionado)) {
                    JOptionPane.showMessageDialog(this,
                            "Este usuario ya es tu amigo",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                if (servidor.solicitarAmistad(nombreUsuario, usuarioSeleccionado)) {
                    JOptionPane.showMessageDialog(this,
                            "Solicitud enviada exitosamente a " + usuarioSeleccionado,
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al enviar solicitud. Puede que ya exista una solicitud pendiente.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel crearPanelSolicitudes() {
        panelSolicitudes = new JPanel(new BorderLayout(10, 10));
        panelSolicitudes.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Solicitudes de Amistad Pendientes");
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelSolicitudes.add(lblTitulo, BorderLayout.NORTH);

        modeloSolicitudes = new DefaultListModel<>();
        listaSolicitudes = new JList<>(modeloSolicitudes);
        JScrollPane scrollSolicitudes = new JScrollPane(listaSolicitudes);
        panelSolicitudes.add(scrollSolicitudes, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAceptarSolicitud = new JButton("Aceptar");
        btnRechazarSolicitud = new JButton("Rechazar");

        btnAceptarSolicitud.addActionListener(e -> aceptarSolicitudAmistad());
        btnRechazarSolicitud.addActionListener(e -> rechazarSolicitudAmistad());

        panelBotones.add(btnAceptarSolicitud);
        panelBotones.add(btnRechazarSolicitud);

        panelSolicitudes.add(panelBotones, BorderLayout.SOUTH);

        return panelSolicitudes;
    }

    private void aceptarSolicitudAmistad() {
        String solicitante = listaSolicitudes.getSelectedValue();

        if (solicitante == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (servidor != null && servidor.aceptarAmistad(solicitante, nombreUsuario)) {
                JOptionPane.showMessageDialog(this, "Solicitud aceptada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al aceptar solicitud", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void rechazarSolicitudAmistad() {
        String solicitante = listaSolicitudes.getSelectedValue();

        if (solicitante == null) {
            JOptionPane.showMessageDialog(this, "Por favor selecciona una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (servidor != null && servidor.rechazarAmistad(solicitante, nombreUsuario)) {
                JOptionPane.showMessageDialog(this, "Solicitud rechazada", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al rechazar solicitud", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel crearPanelPerfil() {
        panelPerfil = new JPanel(new GridBagLayout());
        panelPerfil.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Mi Perfil");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelPerfil.add(lblTitulo, gbc);

        // Separador
        JSeparator separador1 = new JSeparator();
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 20, 10);
        panelPerfil.add(separador1, gbc);

        // Sección de información del usuario
        JLabel lblInfoTitulo = new JLabel("Información de Usuario");
        lblInfoTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelPerfil.add(lblInfoTitulo, gbc);

        // Nombre de usuario
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        panelPerfil.add(lblUsuario, gbc);

        gbc.gridx = 1;
        lblNombreUsuarioPerfil = new JLabel();
        lblNombreUsuarioPerfil.setFont(new Font("Arial", Font.BOLD, 14));
        panelPerfil.add(lblNombreUsuarioPerfil, gbc);

        // Separador
        JSeparator separador2 = new JSeparator();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 20, 10);
        panelPerfil.add(separador2, gbc);

        // Sección de cambio de contraseña
        JLabel lblCambioContraseña = new JLabel("Cambiar Contraseña");
        lblCambioContraseña.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 10, 10, 10);
        panelPerfil.add(lblCambioContraseña, gbc);

        // Contraseña actual
        gbc.gridwidth = 1;
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblContraseñaActual = new JLabel("Contraseña Actual:");
        panelPerfil.add(lblContraseñaActual, gbc);

        gbc.gridx = 1;
        txtContraseñaActual = new JPasswordField(20);
        panelPerfil.add(txtContraseñaActual, gbc);

        // Nueva contraseña
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel lblNuevaContraseña = new JLabel("Nueva Contraseña:");
        panelPerfil.add(lblNuevaContraseña, gbc);

        gbc.gridx = 1;
        txtNuevaContraseña = new JPasswordField(20);
        panelPerfil.add(txtNuevaContraseña, gbc);

        // Confirmar nueva contraseña
        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel lblConfirmarNueva = new JLabel("Confirmar Nueva:");
        panelPerfil.add(lblConfirmarNueva, gbc);

        gbc.gridx = 1;
        txtConfirmarNuevaContraseña = new JPasswordField(20);
        panelPerfil.add(txtConfirmarNuevaContraseña, gbc);

        // Botón cambiar contraseña
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        btnCambiarContraseña = new JButton("Cambiar Contraseña");
        btnCambiarContraseña.setPreferredSize(new Dimension(200, 35));
        btnCambiarContraseña.addActionListener(e -> cambiarContraseña());
        panelPerfil.add(btnCambiarContraseña, gbc);

        // Espacio adicional para empujar todo hacia arriba
        gbc.gridy = 10;
        gbc.weighty = 1.0;
        panelPerfil.add(new JLabel(), gbc);

        return panelPerfil;
    }

    private void cambiarContraseña() {
        String contraseñaActual = new String(txtContraseñaActual.getPassword());
        String nuevaContraseña = new String(txtNuevaContraseña.getPassword());
        String confirmarNueva = new String(txtConfirmarNuevaContraseña.getPassword());

        if (contraseñaActual.isEmpty() || nuevaContraseña.isEmpty() || confirmarNueva.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!nuevaContraseña.equals(confirmarNueva)) {
            JOptionPane.showMessageDialog(this,
                    "Las contraseñas nuevas no coinciden",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nuevaContraseña.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña debe tener al menos 4 caracteres",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contraseñaActual.equals(nuevaContraseña)) {
            JOptionPane.showMessageDialog(this,
                    "La nueva contraseña debe ser diferente a la actual",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (servidor != null) {
                if (servidor.modificarContraseña(nombreUsuario, contraseñaActual, nuevaContraseña)) {
                    JOptionPane.showMessageDialog(this,
                            "Contraseña cambiada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar campos
                    txtContraseñaActual.setText("");
                    txtNuevaContraseña.setText("");
                    txtConfirmarNuevaContraseña.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "La contraseña actual es incorrecta o error al cambiar la contraseña",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this,
                    "Error de conexión con el servidor",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    public void setServidor(IntServidor servidor) {
        this.servidor = servidor;
    }

    public void setCliente(IntCliente cliente) {
        this.cliente = cliente;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }


    public void actualizarAmigosConectados(ConcurrentHashMap<String, IntCliente> amigosConectados) {
        this.amigosOnline = amigosConectados;
        SwingUtilities.invokeLater(() -> {
            modeloAmigosOnline.clear();
            for (String amigo : amigosConectados.keySet()) {
                if (!amigo.equals(nombreUsuario)) {
                    modeloAmigosOnline.addElement(amigo);
                }
            }
        });
    }

    public void actualizarAmigos(ArrayList<String> strings) {
        this.amigos = strings;
        SwingUtilities.invokeLater(() -> {
            modeloAmigos.clear();
            for (String amigo : strings) {
                modeloAmigos.addElement(amigo);
            }
        });
    }

    public void actualizarSolicitudesPendientes(ArrayList<String> strings) {
        this.solicitudesPendientes = strings;
        SwingUtilities.invokeLater(() -> {
            modeloSolicitudes.clear();
            for (String solicitud : strings) {
                modeloSolicitudes.addElement(solicitud);
            }
        });
    }

    private void actualizarTodo(){
        SwingUtilities.invokeLater(() -> {
            modeloAmigosOnline.clear();
            System.out.println(amigosOnline.keySet());
            for (String amigo : amigosOnline.keySet()) {
                if (!amigo.equals(nombreUsuario)) {
                    modeloAmigosOnline.addElement(amigo);
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            modeloUsuariosRegistrados.clear();
            for (String usuario : usuariosRegistrados) {
                if (!usuario.equals(nombreUsuario)) {
                    modeloUsuariosRegistrados.addElement(usuario);
                }
            }
        });

        SwingUtilities.invokeLater(() -> {
            modeloAmigos.clear();
            for (String amigo : amigos) {
                modeloAmigos.addElement(amigo);
            }
        });

        SwingUtilities.invokeLater(() -> {
            modeloSolicitudes.clear();
            for (String solicitud : solicitudesPendientes) {
                modeloSolicitudes.addElement(solicitud);
            }
        });
    }

    public void añadirSolicitudAmistad(String usuarioSolicitante) {
        SwingUtilities.invokeLater(() -> {
            if (!solicitudesPendientes.contains(usuarioSolicitante)) {
                solicitudesPendientes.add(usuarioSolicitante);
                modeloSolicitudes.addElement(usuarioSolicitante);
            }

            // Mostrar notificación al usuario
            JOptionPane.showMessageDialog(this,
                    "Has recibido una solicitud de amistad de: " + usuarioSolicitante,
                    "Nueva Solicitud",
                    JOptionPane.INFORMATION_MESSAGE);

            // Cambiar a la pestaña de solicitudes para que el usuario la vea
            tabbedPane.setSelectedIndex(2); // Índice 2 corresponde a "Solicitudes"
        });
    }

    public void notificarAceptacionSolicitud(String usuarioAceptante) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    usuarioAceptante + " ha aceptado tu solicitud de amistad",
                    "Solicitud Aceptada",
                    JOptionPane.INFORMATION_MESSAGE);

            // Actualizar la lista de amigos si no está ya
            if (!amigos.contains(usuarioAceptante)) {
                amigos.add(usuarioAceptante);
                modeloAmigos.addElement(usuarioAceptante);
            }
        });
    }
}
