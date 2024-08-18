package apptcp_mensajegrupal;

import java.awt.Color;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrincipalSrv extends javax.swing.JFrame {
    private final int PORT = 12345;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private List<PrintWriter> clientWriters = new ArrayList<>();

    public PrincipalSrv() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        this.setTitle("Servidor ...");

        bIniciar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        mensajesTxt = new JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        bIniciar.setFont(new java.awt.Font("Spectral", 1, 11));
        bIniciar.setText("INICIAR SERVIDOR");
        bIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bIniciarActionPerformed(evt);
            }
        });
        getContentPane().add(bIniciar);
        bIniciar.setBounds(100, 50, 250, 40);
        bIniciar.setBackground(Color.WHITE); // Fondo blanco
        bIniciar.setForeground(Color.BLACK); // Texto negro

        jLabel1.setFont(new java.awt.Font("Spectral", 1, 12));
        jLabel1.setText("SERVIDOR TCP");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(200, 10, 160, 17);

        mensajesTxt.setColumns(25);
        mensajesTxt.setRows(5);

        jScrollPane1.setViewportView(mensajesTxt);
        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 110, 410, 100);

        setSize(new java.awt.Dimension(480, 290));
        this.getContentPane().setBackground(Color.WHITE);
        setLocationRelativeTo(null);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PrincipalSrv().setVisible(true);
            }
        });
    }

    private void bIniciarActionPerformed(java.awt.event.ActionEvent evt) {
        iniciarServidor();
    }

    private void iniciarServidor() {
        threadPool = Executors.newCachedThreadPool();
        new Thread(new Runnable() {
            public void run() {
                try {
                    InetAddress addr = InetAddress.getLocalHost();
                    serverSocket = new ServerSocket(PORT);
                    mensajesTxt.append("Servidor TCP en ejecución: " + addr + " ,Puerto " + serverSocket.getLocalPort() + "\n");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        threadPool.execute(new ClientHandler(clientSocket));
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    mensajesTxt.append("Error en el servidor: " + ex.getMessage() + "\n");
                }
            }
        }).start();
    }

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                String line;
                while ((line = in.readLine()) != null) {
                    if (clientName == null) {
                        clientName = line; // First message should be the client's name
                        mensajesTxt.append(clientName + " se ha unido.\n");
                    } else {
                        mensajesTxt.append(line + "\n");
                        broadcast(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                mensajesTxt.append("Error de cliente: " + ex.getMessage() + "\n");
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                // Notify others that the client has disconnected
                if (clientName != null) {
                    String disconnectMessage = clientName + " se ha desconectado.";
                    broadcast(disconnectMessage);
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }
    }

    private javax.swing.JButton bIniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea mensajesTxt;
    private javax.swing.JScrollPane jScrollPane1;
}
    