import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorMatrices {

    public static void main(String[] args) {
        int n = leerTamano(args);
        if (!MatrizUtils.esPotenciaDeDos(n)) {
            System.out.println("Error: n debe ser potencia de 2.");
            return;
        }

        int blockSize = calcularBlockSize(n);
        int numThreads = calcularNumeroHilos(n);

        System.out.println("Generando matrices de tamano " + n + "x" + n + " ...");
        int[][] a = MatrizUtils.generarMatrizAleatoria(n);
        int[][] b = MatrizUtils.generarMatrizAleatoria(n);

        try {
            MatrizUtils.guardarMatrizEnCsv(a, "salida/MatrizA_" + n + ".csv");
            MatrizUtils.guardarMatrizEnCsv(b, "salida/MatrizB_" + n + ".csv");
        } catch (IOException e) {
            System.out.println("No se pudieron guardar las matrices de entrada: " + e.getMessage());
            return;
        }

        String[] algoritmos = {
                "NaivOnArray",
                "NaivLoopUnrollingTwo",
                "NaivLoopUnrollingFour",
                "WinogradOriginal",
                "WinogradScaled",
                "StrassenNaiv",
                "StrassenWinograd",
                "III3SequentialBlock",
                "III4ParallelBlock",
                "III5EnhancedParallelBlock",
                "IV3SequentialBlock",
                "IV4ParallelBlock",
                "IV5EnhancedParallelBlock",
                "V3SequentialBlock",
                "V4ParallelBlock"
        };

        List<String> lineasTiempos = new ArrayList<String>();
        lineasTiempos.add("tamano,block_size,hilos");
        lineasTiempos.add(n + "x" + n + "," + blockSize + "," + numThreads);
        lineasTiempos.add("algoritmo,tiempo_ms,verificacion");

        long[][] referencia = null;

        for (int i = 0; i < algoritmos.length; i++) {
            String nombre = algoritmos[i];

            long inicio = System.nanoTime();
            long[][] resultado = ejecutarAlgoritmo(nombre, a, b, blockSize, numThreads);
            long fin = System.nanoTime();

            double tiempoMs = (fin - inicio) / 1_000_000.0;
            boolean coincide = true;

            if (referencia == null) {
                referencia = resultado;
            } else {
                coincide = MatrizUtils.sonIguales(referencia, resultado);
            }

            String estado = coincide ? "OK" : "DIFIERE";
            String lineaConsola = nombre + " -> " + tiempoMs + " ms | Verificacion: " + estado;
            lineasTiempos.add(nombre + "," + tiempoMs + "," + estado);
            System.out.println(lineaConsola);

            try {
                MatrizUtils.guardarMatrizEnCsv(resultado, "salida/Resultado_" + nombre + "_" + n + ".csv");
            } catch (IOException e) {
                System.out.println("No se pudo guardar resultado de " + nombre + ": " + e.getMessage());
            }
        }

        try {
            MatrizUtils.guardarLineasEnCsv(lineasTiempos, "salida/Tiempos_" + n + ".csv");
            System.out.println("Archivo de tiempos guardado en salida/Tiempos_" + n + ".csv");
        } catch (IOException e) {
            System.out.println("No se pudo guardar el archivo de tiempos: " + e.getMessage());
        }
    }

    private static int leerTamano(String[] args) {
        if (args != null && args.length > 0) {
            return Integer.parseInt(args[0]);
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el tamano n (potencia de 2): ");
        int n = scanner.nextInt();
        return n;
    }

    private static int calcularBlockSize(int n) {
        if (n >= 512) {
            return 64;
        }
        if (n >= 128) {
            return 32;
        }
        return Math.max(2, n / 2);
    }

    private static int calcularNumeroHilos(int n) {
        int disponibles = Runtime.getRuntime().availableProcessors();
        int hilos = Math.max(2, disponibles);
        if (hilos > n) {
            hilos = n;
        }
        return hilos;
    }

    private static long[][] ejecutarAlgoritmo(String nombre, int[][] a, int[][] b, int blockSize, int numThreads) {
        if ("NaivOnArray".equals(nombre)) {
            return NaivOnArray.multiply(a, b);
        }
        if ("NaivLoopUnrollingTwo".equals(nombre)) {
            return NaivLoopUnrollingTwo.multiply(a, b);
        }
        if ("NaivLoopUnrollingFour".equals(nombre)) {
            return NaivLoopUnrollingFour.multiply(a, b);
        }
        if ("WinogradOriginal".equals(nombre)) {
            return WinogradOriginal.multiply(a, b);
        }
        if ("WinogradScaled".equals(nombre)) {
            return WinogradScaled.multiply(a, b);
        }
        if ("StrassenNaiv".equals(nombre)) {
            return StrassenNaiv.multiply(a, b);
        }
        if ("StrassenWinograd".equals(nombre)) {
            return StrassenWinograd.multiply(a, b);
        }
        if ("III3SequentialBlock".equals(nombre)) {
            return III3SequentialBlock.multiply(a, b, blockSize);
        }
        if ("III4ParallelBlock".equals(nombre)) {
            return III4ParallelBlock.multiply(a, b, blockSize, numThreads);
        }
        if ("III5EnhancedParallelBlock".equals(nombre)) {
            return III5EnhancedParallelBlock.multiply(a, b, blockSize, numThreads);
        }
        if ("IV3SequentialBlock".equals(nombre)) {
            return IV3SequentialBlock.multiply(a, b, blockSize);
        }
        if ("IV4ParallelBlock".equals(nombre)) {
            return IV4ParallelBlock.multiply(a, b, blockSize, numThreads);
        }
        if ("IV5EnhancedParallelBlock".equals(nombre)) {
            return IV5EnhancedParallelBlock.multiply(a, b, blockSize, numThreads);
        }
        if ("V3SequentialBlock".equals(nombre)) {
            return V3SequentialBlock.multiply(a, b, blockSize);
        }
        if ("V4ParallelBlock".equals(nombre)) {
            return V4ParallelBlock.multiply(a, b, blockSize, numThreads);
        }

        throw new IllegalArgumentException("Algoritmo no soportado: " + nombre);
    }
}

