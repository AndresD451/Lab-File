package cmd;

import cmd.Clases.Comandos;
import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    private JTextArea  areaTexto;
   
    private Comandos   comandos;

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

    areaTexto.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyPressed(java.awt.event.KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            e.consume();

            String texto = areaTexto.getText();
            String[] lineas = texto.split("\n");
            String ultimaLinea = lineas[lineas.length - 1];

            String prompt = "C:\\Users\\user>";

            String comando = ultimaLinea.replace(prompt, "").trim();

            areaTexto.append("\n");

            comandos.procesarComando(comando);

            areaTexto.append("C:\\Users\\user>");
        }
    }
});
        
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.getViewport().setBackground(Color.BLACK);
        scroll.setBorder(null);



        comandos = new Comandos(areaTexto);



        panel.add(scroll, BorderLayout.CENTER);
      
        setContentPane(panel);
        setVisible(true);
        
    }


}