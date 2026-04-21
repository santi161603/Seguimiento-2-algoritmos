public class NaivLoopUnrollingTwo {

    public static long[][] multiply(int[][] a, int[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Desenrolla el bucle interno de k de 2 en 2 para reducir control de iteraciones.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long suma = 0;
                int k = 0;
                for (; k <= n - 2; k += 2) {
                    suma += (long) a[i][k] * b[k][j];
                    suma += (long) a[i][k + 1] * b[k + 1][j];
                }
                if (k < n) {
                    suma += (long) a[i][k] * b[k][j];
                }
                c[i][j] = suma;
            }
        }
        return c;
    }
}

