public class V4ParallelBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize, int numThreads) {
        int n = a.length;
        long[][] c = new long[n][n];
        Thread[] threads = new Thread[numThreads];

        int columnasPorHilo = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int inicioCol = t * columnasPorHilo;
            int finCol = (t == numThreads - 1) ? n : inicioCol + columnasPorHilo;

            threads[t] = new Thread(new Worker(a, b, c, blockSize, inicioCol, finCol));
            threads[t].start();
        }

        for (int t = 0; t < numThreads; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return c;
    }

    private static class Worker implements Runnable {
        private final int[][] a;
        private final int[][] b;
        private final long[][] c;
        private final int blockSize;
        private final int inicioCol;
        private final int finCol;

        public Worker(int[][] a, int[][] b, long[][] c, int blockSize, int inicioCol, int finCol) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.blockSize = blockSize;
            this.inicioCol = inicioCol;
            this.finCol = finCol;
        }

        public void run() {
            int n = a.length;
            for (int jj = inicioCol; jj < finCol; jj += blockSize) {
                for (int kk = 0; kk < n; kk += blockSize) {
                    for (int ii = 0; ii < n; ii += blockSize) {
                        int jMax = Math.min(jj + blockSize, finCol);
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
        }
    }
}

