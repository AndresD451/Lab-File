package cmd;

import cmd.Clases.Comandos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {
    private JTextArea areaTexto;
    private Comandos  comandos;

    // Guarda hasta donde llega el texto protegido (salida + prompt)
    private int posicionProtegida = 0;

    public GUI() {
        setTitle("Administrator: Command Prompt");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.BLACK);

        areaTexto = new JTextArea();
        areaTexto.setBackground(Color.BLACK);
        areaTexto.setForeground(new Color(200, 200, 200));
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 14));
        areaTexto.setEditable(true);
        areaTexto.setLineWrap(true);

        areaTexto.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                // ENTER: ejecutar comando
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    String texto    = areaTexto.getText();
                    String[] lineas = texto.split("\n");
                    String ultimaLinea = lineas[lineas.length - 1];

                    // Extraer solo lo que escribio el usuario (despues del >)
                    int indicePrompt = ultimaLinea.lastIndexOf('>');
                    String comando = (indicePrompt >= 0)
                            ? ultimaLinea.substring(indicePrompt + 1).trim()
                            : ultimaLinea.trim();

                    areaTexto.append("\n");
                    comandos.procesarComando(comando);

                    // Actualizar posicion protegida despues de imprimir la salida y el nuevo prompt
                    posicionProtegida = areaTexto.getText().length();
                    return;
                }

                // BACKSPACE / DELETE: bloquear si el cursor toca texto protegido
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE ||
                    e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (areaTexto.getCaretPosition() <= posicionProtegida) {
                        e.consume();
                    }
                    return;
                }

                // Cualquier otra tecla: si el cursor esta en zona protegida, moverlo al final
                if (areaTexto.getCaretPosition() < posicionProtegida) {
                    areaTexto.setCaretPosition(areaTexto.getText().length());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(null);

        comandos = new Comandos(areaTexto);

        // Posicion protegida inicial (bienvenida + primer prompt)
        posicionProtegida = areaTexto.getText().length();

        panel.add(scroll, BorderLayout.CENTER);
        setContentPane(panel);
        setVisible(true);
        areaTexto.setCaretPosition(areaTexto.getText().length());
    }
}