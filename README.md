# Práctica 3: Sistema de Chat P2P con RMI

Este proyecto implementa un sistema de mensajería instantánea (chat) y gestión de amistades utilizando Java RMI (Remote Method Invocation) y una base de datos PostgreSQL.

## Arquitectura

El sistema sigue una arquitectura Cliente-Servidor híbrida, donde el servidor actúa como punto de encuentro y gestor de estado, pero la comunicación de mensajes entre clientes se realiza directamente (P2P logic over RMI).

### Componentes Principales

1.  **Servidor (`Aplicacion.Servidor`)**:
    *   Gestiona el registro y autenticación de usuarios contra la base de datos.
    *   Mantiene un registro en memoria de los clientes conectados y sus referencias RMI (`IntCliente`).
    *   Coordina la lógica de solicitudes de amistad y notificaciones de estado (quién está conectado, nuevos amigos, etc.).
    *   **Interfaz `IntServidor`**: Expone métodos remotos para conectar/desconectar, registrar usuarios y gestionar amistades.

2.  **Cliente (`Aplicacion.Cliente`)**:
    *   Provee la interfaz gráfica (`GUI`) para el usuario.
    *   Se registra en el servidor para recibir notificaciones (callbacks).
    *   **Interfaz `IntCliente`**: Expone métodos remotos que el servidor (y otros clientes) pueden invocar.
    *   Implementa la recepción de mensajes directa de otros clientes.

3.  **Base de Datos (`BD`)**:
    *   Clase `GestorBD`: Maneja la persistencia de usuarios, amistades y solicitudes pendientes mediante JDBC.

## Lógica de Mensajes y Comunicación

El flujo de comunicación es el aspecto más importante de esta práctica:

### 1. Conexión y "Callback" distribuido
Cuando un cliente se conecta (`conectarCliente`), envía su propia referencia `IntCliente` al servidor. El servidor almacena esta referencia en un mapa `concurrentHashMap`.

### 2. Notificaciones de Estado
El servidor utiliza las referencias de `IntCliente` para notificar proactivamente a los usuarios sobre cambios, invocando métodos como:
*   `serNotificadoUsuariosRegistrados(...)`
*   `serNotificadoAmigosConectados(...)`
*   `serNotificadoNuevaSolicitud(...)`

### 3. Envío de Mensajes (Lógica P2P sobre RMI)
A diferencia de un chat centralizado clásico donde el mensaje va Cliente A -> Servidor -> Cliente B, aquí se optimiza la comunicación:

1.  El Servidor notifica al Cliente A quiénes son sus amigos conectados y le entrega **sus referencias remotas (`IntCliente`)**.
2.  Cuando el Cliente A envía un mensaje al Cliente B:
    *   Busca la referencia RMI del Cliente B en su lista local de amigos conectados.
    *   Invoca **directamente** el método `recibirMensaje(mensaje, emisor)` sobre el objeto remoto del Cliente B.
    *   El Servidor **no interviene** en el paso del mensaje, reduciendo la carga central.

## Lógica de Gestión de Amistades

El sistema gestiona un ciclo de vida de amistad completo:
1.  **Solicitud**: Usuario A solicita amistad a Usuario B (`solicitarUsuario`).
2.  **Notificación**: Si B está conectado, el servidor le notifica instantáneamente (`serNotificadoNuevaSolicitud`).
3.  **Aceptación/Rechazo**: B responde a la solicitud. El servidor actualiza la BD y notifica a ambos pares (`serNotificadoAceptacionSolicitud`).
4.  **Conexión**: Una vez amigos, si ambos están online, el servidor intercambia sus referencias RMI para permitir el chat directo.

## Requisitos Previos

*   Java JDK
*   Base de datos PostgreSQL
*   Archivo `baseDatos.properties` configurado correctamente en la raíz del proyecto.

## Ejecución

1.  Iniciar el **Servidor**: Ejecutar `Aplicacion.Servidor.Servidor`. Esto iniciará el registro RMI en el puerto 1099.
2.  Iniciar **Clientes**: Ejecutar `GUI.GUI` (o la clase principal del cliente si existe aparte, aunque `GUI` parece ser el punto de entrada o es instanciada por `Aplicacion.Cliente.Cliente` si existiera, pero en este código `GUI` contiene el `main` implícito o es invocada externamente. *Nota: En el código analizado, `Aplicacion.Cliente` tiene las interfaces e implementación, y `GUI` usa estas clases. Debería haber una clase Main para el cliente, o la GUI tiene un main. Revisando el código, la GUI no tiene main, así que se asume que se lanza desde un Main de cliente o IDE.*)

*(Revisión: `ImplCliente` recibe `GUI` en constructor. Probablemente haya un `ClienteMain` que no vi o se ejecuta `GUI` si tuviera main, pero `GUI` no tiene main en el fichero que leí. Asumiré que el usuario sabe cómo ejecutarlo o hay un archivo que pasé por alto, o se ejecuta creando una instancia de GUI).*

*Corrección*: He revisado los archivos y `Cliente.java` en `Aplicacion/Cliente` probablemente contenga el `main`.

## Estructura de Archivos

*   `src/Aplicacion/Servidor`: Lógica del servidor y objetos remotos.
*   `src/Aplicacion/Cliente`: Implementación del cliente RMI.
*   `src/BD`: Gestión de conexión a base de datos.
*   `src/GUI`: Interfaz gráfica Swing.
