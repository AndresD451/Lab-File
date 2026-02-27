/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author user
 */
public class GUI extends JFrame {
    private JTextArea areaTexto;
    private JTextField campoInput;
    
    
    public GUI(){
        setTitle("Mi Ventana");
        setSize(500,400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.black);
        
        
        JPanel panel = new JPanel (new BorderLayout());
        panel.setBackground(Color.black);
        
        
        areaTexto = new JTextArea();
        areaTexto.setBackground(Color.black);
        areaTexto.setForeground(Color.white);
        areaTexto.setFont(new Font("Consola", Font.PLAIN, 14));
        areaTexto.setEditable(false);
        areaTexto.setText("C:\\Users\\Usuario> ");
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.getViewport().setBackground(Color.black);
        scroll.setBorder(null);
        
        
        campoInput = new JTextField();
        campoInput.setBackground(Color.black);
        campoInput.setForeground(Color.white);
        campoInput.setCaretColor(Color.white);
        campoInput.setFont(new Font("Consola", Font.PLAIN, 14));
        campoInput.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
      
        
        campoInput.addActionListener(e -> {
            String comando = campoInput.getText();
            procesarComando(comando);
            campoInput.setText("");
        });
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(campoInput, BorderLayout.SOUTH);

        setContentPane(panel);
        setVisible(true);
        campoInput.requestFocus();
    }
    

    private void procesarComando(String comando) {
        areaTexto.append(comando + "\n");

        switch (comando.toLowerCase().trim()) {
            case "help":
                areaTexto.append("Comandos: help, cls, fecha, salir\n");
                break;
            case "cls":
                areaTexto.setText("");
                break;
            case "fecha":
                areaTexto.append(new java.util.Date().toString() + "\n");
                break;
            case "salir":
                System.exit(0);
                break;
            default:
                areaTexto.append("'" + comando + "' no se reconoce como comando.\n");
                break;
        }

        areaTexto.append("\nC:\\Users\\Usuario> ");
        areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
    }

        
         
        
        
        
        
        
        
        
    }
    

