public class WinogradOriginal {

    public static long[][] multiply(int[][] a, int[][] b) {
        int n = a.length;
        long[][] c = new long[n][n];

        long[] rowFactor = new long[n];
        long[] colFactor = new long[n];

        // Precalculo por filas de A.
        for (int i = 0; i < n; i++) {
            long suma = 0;
            for (int k = 0; k < n / 2; k++) {
                suma += (long) a[i][2 * k] * a[i][2 * k + 1];
            }
            rowFactor[i] = suma;
        }

        // Precalculo por columnas de B.
        for (int j = 0; j < n; j++) {
            long suma = 0;
            for (int k = 0; k < n / 2; k++) {
                suma += (long) b[2 * k][j] * b[2 * k + 1][j];
            }
            colFactor[j] = suma;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                long valor = -rowFactor[i] - colFactor[j];
                for (int k = 0; k < n / 2; k++) {
                    valor += ((long) a[i][2 * k] + b[2 * k + 1][j])
                            * ((long) a[i][2 * k + 1] + b[2 * k][j]);
                }

                // Si n es impar, agrega el termino faltante.
                if (n % 2 != 0) {
                    valor += (long) a[i][n - 1] * b[n - 1][j];
                }
                c[i][j] = valor;
            }
        }

        return c;
    }
}

