package cmd.Clases;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 * Comandos - Logica de todos los comandos de la consola.
 * Recibe el JTextArea del GUI para escribir la salida.
 */
public class Comandos {

    private final JTextArea area;
    private File directorioActual;

    // Estado modo escritura (Wr)
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

    // Metodo principal llamado desde GUI al presionar Enter
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

        switch (cmd.toLowerCase()) {
            case "mkdir" -> cmdMkdir(arg);
            case "mfile" -> cmdMfile(arg);
            case "rm"    -> cmdRm(arg);
            case "cd"    -> cmdCd(arg);
            case "<...>" -> cmdBack();
            case "dir"   -> cmdDir();
            case "date"  -> cmdDate();
            case "time"  -> cmdTime();
            case "wr"    -> cmdWr(arg);
            case "rd"    -> cmdRd(arg);
            case "cls"   -> cmdCls();
            case "help"  -> cmdHelp();
            default      -> imprimir("'" + cmd + "' no se reconoce como un comando interno.");
        }

        if (!modoEscritura) {
            imprimirPrompt();
        }
    }

    // Escribe texto con salto de linea
    private void imprimir(String texto) {
        area.append(texto + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    // Imprime el prompt con la ruta real del sistema
    public void imprimirPrompt() {
        area.append(directorioActual.getAbsolutePath() + ">");
        area.setCaretPosition(area.getDocument().getLength());
    }

    // Mkdir <nombre> - Crea carpeta real en el sistema
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

    // Mfile <nombre.ext> - Crea archivo real en el sistema
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

    // Rm <nombre> - Elimina archivo o carpeta real del sistema
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

    // Borra recursivamente si es directorio
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

    // Cd <nombre> - Entra a un subdirectorio real
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

    // <...> - Sube al directorio padre
    private void cmdBack() {
        File padre = directorioActual.getParentFile();
        if (padre != null) {
            directorioActual = padre;
        } else {
            imprimir("Ya esta en el directorio raiz.");
        }
    }

    // Dir - Lista el contenido real del directorio actual
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

    // Date - Fecha real del sistema
    private void cmdDate() {
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        imprimir("Fecha actual: " + fecha);
    }

    // Time - Hora real del sistema
    private void cmdTime() {
        String hora = new SimpleDateFormat("HH:mm:ss").format(new Date());
        imprimir("Hora actual: " + hora);
    }

    // Wr <archivo.ext> - Inicia escritura real en archivo con FileWriter
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

    // Acumula lineas en buffer hasta recibir EXIT
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

    // Guarda el buffer en el archivo usando FileWriter
    private void guardarBuffer() {
        File objetivo = new File(directorioActual, archivoEscritura);
        try (FileWriter fw = new FileWriter(objetivo, true)) {
            fw.write(bufferEscritura.toString());
            imprimir("Texto guardado correctamente en: " + objetivo.getAbsolutePath());
        } catch (IOException e) {
            imprimir("Error al guardar: " + e.getMessage());
        }
    }

    // Rd <archivo.ext> - Lee archivo real con FileReader y muestra contenido
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

    // Cls - Limpia la pantalla
    private void cmdCls() {
        area.setText("");
        imprimirPrompt();
    }

    // Help - Lista todos los comandos
    private void cmdHelp() {
        imprimir("Comandos disponibles:");
        imprimir("  Mkdir <nombre>       - Crear nueva carpeta");
        imprimir("  Mfile <nombre.ext>   - Crear nuevo archivo");
        imprimir("  Rm <nombre>          - Eliminar carpeta o archivo");
        imprimir("  Cd <nombre>          - Cambiar de directorio");
        imprimir("  <...>                - Regresar al directorio padre");
        imprimir("  Dir                  - Listar contenido del directorio");
        imprimir("  Date                 - Ver fecha actual");
        imprimir("  Time                 - Ver hora actual");
        imprimir("  Wr <archivo.ext>     - Escribir en archivo (EXIT para terminar)");
        imprimir("  Rd <archivo.ext>     - Leer contenido de archivo");
        imprimir("  Cls                  - Limpiar pantalla");
        imprimir("  Help                 - Mostrar esta ayuda");
    }
}