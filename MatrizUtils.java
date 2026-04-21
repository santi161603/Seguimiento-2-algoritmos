import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MatrizUtils {

    public static boolean esPotenciaDeDos(int n) {
        if (n <= 0) {
            return false;
        }
        while (n % 2 == 0) {
            n = n / 2;
        }
        return n == 1;
    }

    public static int[][] generarMatrizAleatoria(int n) {
        int[][] matriz = new int[n][n];
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matriz[i][j] = 100000 + random.nextInt(900000);
            }
        }
        return matriz;
    }

    public static void guardarMatrizEnTxt(long[][] matriz, String rutaArchivo) throws IOException {
        guardarMatrizLongConSeparador(matriz, rutaArchivo, " ");
    }

    public static void guardarMatrizEnTxt(int[][] matriz, String rutaArchivo) throws IOException {
        guardarMatrizIntConSeparador(matriz, rutaArchivo, " ");
    }

    public static void guardarLineasEnTxt(List<String> lineas, String rutaArchivo) throws IOException {
        guardarLineas(lineas, rutaArchivo);
    }

    public static void guardarMatrizEnCsv(long[][] matriz, String rutaArchivo) throws IOException {
        guardarMatrizLongConSeparador(matriz, rutaArchivo, ",");
    }

    public static void guardarMatrizEnCsv(int[][] matriz, String rutaArchivo) throws IOException {
        guardarMatrizIntConSeparador(matriz, rutaArchivo, ",");
    }

    public static void guardarLineasEnCsv(List<String> lineas, String rutaArchivo) throws IOException {
        guardarLineas(lineas, rutaArchivo);
    }

    public static boolean sonIguales(long[][] a, long[][] b) {
        if (a.length != b.length || a[0].length != b[0].length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private static BufferedWriter crearWriter(String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        File padre = archivo.getParentFile();
        if (padre != null && !padre.exists()) {
            padre.mkdirs();
        }
        return new BufferedWriter(new FileWriter(archivo));
    }

    private static void guardarMatrizLongConSeparador(long[][] matriz, String rutaArchivo, String separador) throws IOException {
        BufferedWriter writer = crearWriter(rutaArchivo);
        for (int i = 0; i < matriz.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(matriz[i][j]);
                if (j < matriz[i].length - 1) {
                    sb.append(separador);
                }
            }
            writer.write(sb.toString());
            writer.newLine();
        }
        writer.close();
    }

    private static void guardarMatrizIntConSeparador(int[][] matriz, String rutaArchivo, String separador) throws IOException {
        BufferedWriter writer = crearWriter(rutaArchivo);
        for (int i = 0; i < matriz.length; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(matriz[i][j]);
                if (j < matriz[i].length - 1) {
                    sb.append(separador);
                }
            }
            writer.write(sb.toString());
            writer.newLine();
        }
        writer.close();
    }

    private static void guardarLineas(List<String> lineas, String rutaArchivo) throws IOException {
        BufferedWriter writer = crearWriter(rutaArchivo);
        for (int i = 0; i < lineas.size(); i++) {
            writer.write(lineas.get(i));
            writer.newLine();
        }
        writer.close();
    }
}
