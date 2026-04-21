public class IV3SequentialBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize) {
        int n = a.length;
        long[][] c = new long[n][n];

        // Orden fila-fila: fija i y k para recorrer j de forma continua.
        for (int ii = 0; ii < n; ii += blockSize) {
            for (int kk = 0; kk < n; kk += blockSize) {
                for (int jj = 0; jj < n; jj += blockSize) {
                    int iMax = Math.min(ii + blockSize, n);
                    int kMax = Math.min(kk + blockSize, n);
                    int jMax = Math.min(jj + blockSize, n);

                    for (int i = ii; i < iMax; i++) {
                        for (int k = kk; k < kMax; k++) {
                            long aik = a[i][k];
                            for (int j = jj; j < jMax; j++) {
                                c[i][j] += aik * b[k][j];
                            }
                        }
                    }
                }
            }
        }
        return c;
    }
}

