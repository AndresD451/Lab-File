package cmd;

import cmd.Clases.Comandos;
import javax.swing.*;
import java.awt.*;

/**
 * GUI - Ventana principal de la consola estilo CMD.
 */
public class GUI extends JFrame {

    private JTextArea  areaTexto;
    private JTextField campoInput;
    private Comandos   comandos;

    public GUI() {
        setTitle("Administrator: Command Prompt");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        // Area donde se muestra la salida
        areaTexto = new JTextArea();
        areaTexto.setBackground(Color.BLACK);
        areaTexto.setForeground(new Color(200, 200, 200));
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaTexto.setEditable(false);
        areaTexto.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(null);

        // Campo donde el usuario escribe
        campoInput = new JTextField();
        campoInput.setBackground(Color.BLACK);
        campoInput.setForeground(new Color(200, 200, 200));
        campoInput.setCaretColor(Color.WHITE);
        campoInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        campoInput.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Conectar con la clase Comandos
        comandos = new Comandos(areaTexto);

        campoInput.addActionListener(e -> {
            String input = campoInput.getText().trim();
            campoInput.setText("");
            comandos.procesarComando(input);
        });

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(campoInput, BorderLayout.SOUTH);
        setContentPane(panel);
        setVisible(true);
        campoInput.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}