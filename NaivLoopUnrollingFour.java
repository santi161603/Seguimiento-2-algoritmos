public class NaivLoopUnrollingFour {

    public static long[][] multiply(int[][] a, int[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Desenrolla el bucle interno de k de 4 en 4.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long suma = 0;
                int k = 0;
                for (; k <= n - 4; k += 4) {
                    suma += (long) a[i][k] * b[k][j];
                    suma += (long) a[i][k + 1] * b[k + 1][j];
                    suma += (long) a[i][k + 2] * b[k + 2][j];
                    suma += (long) a[i][k + 3] * b[k + 3][j];
                }
                for (; k < n; k++) {
                    suma += (long) a[i][k] * b[k][j];
                }
                c[i][j] = suma;
            }
        }
        return c;
    }
}

