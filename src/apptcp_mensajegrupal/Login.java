package apptcp_mensajegrupal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Login extends JFrame {

    private JTextField nombreTxt;
    private JButton bConectar;

    public Login() {
        initComponents();
    }

    private void initComponents() {
        this.setTitle("INICIO");
        this.setLayout(null);
        this.getContentPane().setBackground(Color.WHITE);

        JLabel jLabel1 = new JLabel("NOMBRE USUARIO");
        jLabel1.setFont(new Font("Spectral", Font.PLAIN, 11));
        jLabel1.setBounds(50, 30, 150, 30);
        getContentPane().add(jLabel1);
        
        nombreTxt = new JTextField();
        nombreTxt.setFont(new Font("Spectral", Font.PLAIN, 11));
        nombreTxt.setBackground(Color.WHITE); // Fondo blanco
        nombreTxt.setForeground(Color.BLACK); // Texto negro
        nombreTxt.setBounds(50, 60, 200, 30);

        // Convertir texto a mayúsculas con KeyListener
        nombreTxt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = nombreTxt.getText();
                nombreTxt.setText(text.toUpperCase());
            }
        });

        getContentPane().add(nombreTxt);
        
        bConectar = new JButton("CONECTAR");
        bConectar.setFont(new Font("Spectral", Font.BOLD, 11));
        bConectar.setBackground(Color.WHITE); // Fondo blanco
        bConectar.setForeground(Color.BLACK); // Texto negro
        bConectar.setBounds(50, 100, 200, 30);
        bConectar.addActionListener(evt -> bConectarActionPerformed());
        getContentPane().add(bConectar);


        this.setSize(300, 200);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void bConectarActionPerformed() {
        String clientName = nombreTxt.getText();
        if (clientName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese su nombre.");
            return;
        }
        this.dispose(); // Cerrar la ventana de inicio de sesión
        new PrincipalCli(clientName).setVisible(true); // Mostrar la ventana principal
    }

    public static void main(String[] args) {
        // Crear y mostrar la interfaz gráfica directamente
        Login login = new Login();
        login.setVisible(true);
    }
}
