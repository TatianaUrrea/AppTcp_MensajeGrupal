package apptcp_mensajegrupal;

import java.awt.Color;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PrincipalCli extends javax.swing.JFrame {
    private final int PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final String clienteNombre; // Nuevo campo para el nombre del cliente
    private JLabel jLabelStatus; // Nuevo JLabel para mostrar el estado

    public PrincipalCli(String clienteNombre) {
        this.clienteNombre = clienteNombre;
        initComponents();
        Estado("DESCONECTADO"); // Inicialmente está desconectado
        Nombre(clienteNombre);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("Cliente");

        bConectar = new javax.swing.JButton();
        jLabelNombre = new javax.swing.JLabel();
        jLabelEstado = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mensajesTxt = new javax.swing.JTextArea();
        mensajeTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btEnviar = new javax.swing.JButton();
        bDesconectar = new javax.swing.JButton();
        bSalir = new javax.swing.JButton();
        new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        //BOTON CONECTAR
        bConectar.setFont(new java.awt.Font("Spectral", 1, 11));
        bConectar.setText("CONECTAR");
        bConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bConectarActionPerformed(evt);
            }
        });
        getContentPane().add(bConectar);
        bConectar.setBounds(150, 45, 200, 30);
        bConectar.setBackground(Color.WHITE); // Fondo blanco
        bConectar.setForeground(Color.BLACK); // Texto negro

        // TITULO CLIENTE CON ESTADO
        jLabelNombre.setFont(new java.awt.Font("Spectral", 2, 10));
        getContentPane().add(jLabelNombre);
        jLabelNombre.setBounds(45, 10, 300, 17);
        jLabelEstado.setFont(new java.awt.Font("Spectral", 2, 10));
        getContentPane().add(jLabelEstado);
        jLabelEstado.setBounds(350, 10, 300, 17);
        
        //MENSAJE RECIBIDO
        mensajesTxt.setColumns(20);
        mensajesTxt.setRows(5);
        mensajesTxt.setEnabled(false);
        jScrollPane1.setViewportView(mensajesTxt);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 90, 430, 170);

        //MENSAJE A ENVIAR
        jLabel2.setFont(new java.awt.Font("Spectral", 1, 11));
        jLabel2.setText("Mensaje:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 270, 120, 30);

        mensajeTxt.setFont(new java.awt.Font("Spectral", 0, 11));
        getContentPane().add(mensajeTxt);
        mensajeTxt.setBounds(20, 300, 320, 30);

        //BOTON ENVIAR
        btEnviar.setFont(new java.awt.Font("Spectral", 1, 11));
        btEnviar.setText("ENVIAR");
        btEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEnviarActionPerformed(evt);
            }
        });
        getContentPane().add(btEnviar);
        btEnviar.setBounds(350, 300, 100, 30);
        btEnviar.setBackground(Color.WHITE); // Fondo blanco
        btEnviar.setForeground(Color.BLACK); // Texto negro

        //BOTON DESCONECTAR
        bDesconectar.setFont(new java.awt.Font("Spectral", 1, 11));
        bDesconectar.setText("DESCONECTAR");
        bDesconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDesconectarActionPerformed(evt);
            }
        });
        getContentPane().add(bDesconectar);
        bDesconectar.setBounds(100, 350, 150, 30);
        bDesconectar.setBackground(Color.WHITE); // Fondo blanco
        bDesconectar.setForeground(Color.BLACK); // Texto negro

        // BOTON SALIR
        bSalir.setFont(new java.awt.Font("Spectral", 1, 11));
        bSalir.setText("SALIR");
        bSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bSalirActionPerformed(evt);
            }
        });
        getContentPane().add(bSalir);
        bSalir.setBounds(260, 350, 150, 30);
        bSalir.setBackground(Color.WHITE); // Fondo blanco
        bSalir.setForeground(Color.BLACK); // Texto negro

        //DIMENSIONES VENTANA
        setSize(new java.awt.Dimension(491, 435));
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.WHITE);
    }

    private void Estado(String status) {
        jLabelEstado.setText(status);
    }
    
    private void Nombre(String clienteNombre){
        jLabelNombre.setText("CLIENTE: "+clienteNombre);
    }

    private void bConectarActionPerformed(java.awt.event.ActionEvent evt) {
        if (clienteNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese su nombre.");
            return;
        }
        conectar();
    }

    private void btEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        enviarMensaje();
    }

    private void bDesconectarActionPerformed(java.awt.event.ActionEvent evt) {
        closeConnection();
        Estado("DESCONECTADO"); 
        bConectar.setEnabled(true);
        bDesconectar.setEnabled(false);
        mensajeTxt.setEnabled(false);
        btEnviar.setEnabled(false);
        JOptionPane.showMessageDialog(this, "Desconectado del servidor.");
    }

    private void bSalirActionPerformed(java.awt.event.ActionEvent evt) {
        closeConnection();
        System.exit(0);
    }

    private void conectar() {
        try {
            socket = new Socket("localhost", PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out.println(clienteNombre); // Enviar nombre del cliente al servidor

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        mensajesTxt.append(fromServer + "\n");
                    }
                } catch (IOException ex) {
                    reconectar();
                }
            }).start();

            Estado("CONECTADO"); // Actualizar estado a conectado
            bConectar.setEnabled(false);
            bDesconectar.setEnabled(true);
            mensajeTxt.setEnabled(true);
            btEnviar.setEnabled(true);
            bSalir.setEnabled(true); // Habilitar el botón Salir

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor. Verifique la conexión e intente nuevamente.");
            reconectar(); // Intentar reconexión si no se pudo conectar
        }
    }

    private void reconectar() {
        Estado("DESCONECTADO"); // Actualizar estado a desconectado
        bConectar.setEnabled(true);
        bDesconectar.setEnabled(false);
        mensajeTxt.setEnabled(false);
        btEnviar.setEnabled(false);
    }

    private void enviarMensaje() {
        if (out != null) {
            String mensaje = mensajeTxt.getText();
            out.println(clienteNombre + ": " + mensaje);
            mensajeTxt.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "No estás conectado al servidor.");
        }
    }

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                out.println(clienteNombre + " se ha desconectado.");
                socket.close();
                Estado("DESCONECTADO"); // Actualizar estado a desconectado
                bConectar.setEnabled(true);
                bDesconectar.setEnabled(false);
                mensajeTxt.setEnabled(false);
                btEnviar.setEnabled(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cerrar la conexión: " + e.getMessage());
        }
    }

    private javax.swing.JButton bConectar;
    private javax.swing.JButton bDesconectar;
    private javax.swing.JButton bSalir;
    private javax.swing.JButton btEnviar;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JTextField mensajeTxt;
    private javax.swing.JLabel jLabelNombre;
    private javax.swing.JLabel jLabelEstado;
}
