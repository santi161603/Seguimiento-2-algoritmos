public class III3SequentialBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Recorre por bloques en orden fila-columna para mejorar localidad de cache.
        for (int ii = 0; ii < n; ii += blockSize) {
            for (int jj = 0; jj < n; jj += blockSize) {
                for (int kk = 0; kk < n; kk += blockSize) {
                    int iMax = Math.min(ii + blockSize, n);
                    int jMax = Math.min(jj + blockSize, n);
                    int kMax = Math.min(kk + blockSize, n);

                    for (int i = ii; i < iMax; i++) {
                        for (int j = jj; j < jMax; j++) {
                            long suma = c[i][j];
                            for (int k = kk; k < kMax; k++) {
                                suma += (long) a[i][k] * b[k][j];
                            }
                            c[i][j] = suma;
                        }
                    }
                }
            }
        }
        return c;
    }
}

