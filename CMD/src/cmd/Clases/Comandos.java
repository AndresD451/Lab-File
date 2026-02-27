package cmd.Clases;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

public class Comandos {

    private final JTextArea area;
    private File directorioActual;

    private boolean             modoEscritura    = false;
    private String              archivoEscritura = null;
    private final StringBuilder bufferEscritura  = new StringBuilder();

    public Comandos(JTextArea area) {
        this.area             = area;
        this.directorioActual = new File(System.getProperty("user.home"));

        imprimir("Microsoft Windows [Version 10.0.22621.521]");
        imprimir("(c) Microsoft Corporation. All rights reserved.\n");
        imprimirPrompt();
    }

    public void procesarComando(String input) {

        if (modoEscritura) {
            manejarModoEscritura(input);
            return;
        }

        area.append(input + "\n");
        area.setCaretPosition(area.getDocument().getLength());

        if (input.isEmpty()) {
            imprimirPrompt();
            return;
        }

        String[] partes = input.split("\\s+", 2);
        String   cmd    = partes[0];
        String   arg    = (partes.length > 1) ? partes[1].trim() : "";

        switch (cmd) {
            case "Mkdir" -> cmdMkdir(arg);
            case "Mfile" -> cmdMfile(arg);
            case "Rm"    -> cmdRm(arg);
            case "Cd"    -> cmdCd(arg);
            case "<...>" -> cmdBack();
            case "Dir"   -> cmdDir();
            case "Date"  -> cmdDate();
            case "Time"  -> cmdTime();
            case "Wr"    -> cmdWr(arg);
            case "Rd"    -> cmdRd(arg);
            default      -> imprimir("'" + cmd + "' no se reconoce como un comando interno.");
        }

        if (!modoEscritura) {
            imprimirPrompt();
        }
    }

    private void imprimir(String texto) {
        area.append(texto + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    
    public void imprimirPrompt() {
        area.append(directorioActual.getAbsolutePath() + ">");
        area.setCaretPosition(area.getDocument().getLength());
    }

    private void cmdMkdir(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Mkdir <nombre>");
            return;
        }
        File dir = new File(directorioActual, nombre);
        if (dir.mkdir()) {
            imprimir("Directorio creado: " + dir.getAbsolutePath());
        } else {
            imprimir("No se pudo crear '" + nombre + "' (ya existe o sin permisos).");
        }
    }

    private void cmdMfile(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Mfile <nombre.ext>");
            return;
        }
        File archivo = new File(directorioActual, nombre);
        try {
            if (archivo.createNewFile()) {
                imprimir("Archivo creado: " + archivo.getAbsolutePath());
            } else {
                imprimir("El archivo '" + nombre + "' ya existe.");
            }
        } catch (IOException e) {
            imprimir("Error al crear archivo: " + e.getMessage());
        }
    }

    private void cmdRm(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Rm <nombre>");
            return;
        }
        File objetivo = new File(directorioActual, nombre);
        if (!objetivo.exists()) {
            imprimir("No se encontro: " + nombre);
            return;
        }
        if (eliminarRecursivo(objetivo)) {
            imprimir("Eliminado: " + nombre);
        } else {
            imprimir("No se pudo eliminar: " + nombre);
        }
    }

    private boolean eliminarRecursivo(File archivo) {
        if (archivo.isDirectory()) {
            File[] hijos = archivo.listFiles();
            if (hijos != null) {
                for (File hijo : hijos) {
                    eliminarRecursivo(hijo);
                }
            }
        }
        return archivo.delete();
    }

    private void cmdCd(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Cd <nombre>");
            return;
        }
        File objetivo = new File(directorioActual, nombre);
        if (objetivo.exists() && objetivo.isDirectory()) {
            directorioActual = objetivo;
        } else {
            imprimir("No se encuentra el directorio: " + nombre);
        }
    }

    private void cmdBack() {
        File padre = directorioActual.getParentFile();
        if (padre != null) {
            directorioActual = padre;
        } else {
            imprimir("Ya esta en el directorio raiz.");
        }
    }

    private void cmdDir() {
        File[] entradas = directorioActual.listFiles();
        if (entradas == null || entradas.length == 0) {
            imprimir("El directorio esta vacio.");
            return;
        }
        imprimir("Directorio: " + directorioActual.getAbsolutePath() + "\n");
        for (File entrada : entradas) {
            String tipo = entrada.isDirectory() ? "<DIR>   " : "        ";
            imprimir("  " + tipo + entrada.getName());
        }
        imprimir("");
    }

    private void cmdDate() {
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        imprimir("Fecha actual: " + fecha);
    }

    
    private void cmdTime() {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        imprimir("Hora actual: " + hora);
    }

    private void cmdWr(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Wr <archivo.ext>");
            return;
        }
        File objetivo = new File(directorioActual, nombre);
        if (!objetivo.exists()) {
            imprimir("El archivo no existe. Creelo primero con: Mfile " + nombre);
            return;
        }
        modoEscritura    = true;
        archivoEscritura = nombre;
        imprimir("Escribiendo en: " + objetivo.getAbsolutePath());
        imprimir("(Escriba EXIT en mayuscula para terminar)");
    }

    private void manejarModoEscritura(String linea) {
        area.append(linea + "\n");
        area.setCaretPosition(area.getDocument().getLength());

        if (linea.equals("EXIT")) {
            guardarBuffer();
            modoEscritura    = false;
            archivoEscritura = null;
            bufferEscritura.setLength(0);
            imprimirPrompt();
        } else {
            bufferEscritura.append(linea).append(System.lineSeparator());
        }
    }

    private void guardarBuffer() {
        File objetivo = new File(directorioActual, archivoEscritura);
        try (FileWriter fw = new FileWriter(objetivo, true)) {
            fw.write(bufferEscritura.toString());
            imprimir("Texto guardado correctamente en: " + objetivo.getAbsolutePath());
        } catch (IOException e) {
            imprimir("Error al guardar: " + e.getMessage());
        }
    }

    private void cmdRd(String nombre) {
        if (nombre.isEmpty()) {
            imprimir("Uso: Rd <archivo.ext>");
            return;
        }
        File objetivo = new File(directorioActual, nombre);
        if (!objetivo.exists()) {
            imprimir("El archivo no existe: " + nombre);
            return;
        }
        try (FileReader fr     = new FileReader(objetivo);
             BufferedReader br = new BufferedReader(fr)) {

            imprimir("--- Contenido de " + nombre + " ---");
            String linea;
            while ((linea = br.readLine()) != null) {
                imprimir(linea);
            }
            imprimir("-----------------------------------");

        } catch (IOException e) {
            imprimir("Error al leer: " + e.getMessage());
        }
    }