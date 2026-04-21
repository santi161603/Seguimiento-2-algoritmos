public class V3SequentialBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Orden columna-columna: organiza por bloques de columnas y acumula por k.
        for (int jj = 0; jj < n; jj += blockSize) {
            for (int kk = 0; kk < n; kk += blockSize) {
                for (int ii = 0; ii < n; ii += blockSize) {
                    int jMax = Math.min(jj + blockSize, n);
                    int kMax = Math.min(kk + blockSize, n);
                    int iMax = Math.min(ii + blockSize, n);

                    for (int j = jj; j < jMax; j++) {
                        for (int k = kk; k < kMax; k++) {
                            long bkj = b[k][j];
                            for (int i = ii; i < iMax; i++) {
                                c[i][j] += (long) a[i][k] * bkj;
                            }
                        }
                    }
                }
            }
        }
        return c;
    }
}

