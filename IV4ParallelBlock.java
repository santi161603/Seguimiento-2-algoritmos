public class IV4ParallelBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize, int numThreads) {
        int n = a.length;
        long[][] c = new long[n][n];
        Thread[] threads = new Thread[numThreads];

        int filasPorHilo = n / numThreads;

        for (int t = 0; t < numThreads; t++) {
            int inicioFila = t * filasPorHilo;
            int finFila = (t == numThreads - 1) ? n : inicioFila + filasPorHilo;

            threads[t] = new Thread(new Worker(a, b, c, blockSize, inicioFila, finFila));
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
        private final int inicioFila;
        private final int finFila;

        public Worker(int[][] a, int[][] b, long[][] c, int blockSize, int inicioFila, int finFila) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.blockSize = blockSize;
            this.inicioFila = inicioFila;
            this.finFila = finFila;
        }

        public void run() {
            int n = a.length;
            for (int ii = inicioFila; ii < finFila; ii += blockSize) {
                for (int kk = 0; kk < n; kk += blockSize) {
                    for (int jj = 0; jj < n; jj += blockSize) {
                        int iMax = Math.min(ii + blockSize, finFila);
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
        }
    }
}

