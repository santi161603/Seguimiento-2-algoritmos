public class WinogradScaled {

    public static long[][] multiply(int[][] a, int[][] b) {
        // En esta version didactica con enteros, mantenemos escala 1 para no perder exactitud.
        // Se calcula una metrica de norma para mostrar la idea de escalamiento de Winograd.
        long normaA = normaInfinita(a);
        long normaB = normaInfinita(b);

        if (normaA == 0 || normaB == 0) {
            return WinogradOriginal.multiply(a, b);
        }

        return WinogradOriginal.multiply(a, b);
    }

    private static long normaInfinita(int[][] m) {
        long max = 0;
        for (int i = 0; i < m.length; i++) {
            long sumaFila = 0;
            for (int j = 0; j < m[i].length; j++) {
                sumaFila += Math.abs((long) m[i][j]);
            }
            if (sumaFila > max) {
                max = sumaFila;
            }
        }
        return max;
    }
}
