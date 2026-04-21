public class IV5EnhancedParallelBlock {

    public static long[][] multiply(int[][] a, int[][] b, int blockSize, int numThreads) {
        int n = a.length;
        long[][] c = new long[n][n];
        Thread[] threads = new Thread[numThreads];

        int totalBloquesFila = (n + blockSize - 1) / blockSize;

        for (int t = 0; t < numThreads; t++) {
            threads[t] = new Thread(new Worker(a, b, c, blockSize, t, numThreads, totalBloquesFila));
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
        private final int idHilo;
        private final int totalHilos;
        private final int totalBloquesFila;

        public Worker(int[][] a, int[][] b, long[][] c, int blockSize, int idHilo, int totalHilos, int totalBloquesFila) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.blockSize = blockSize;
            this.idHilo = idHilo;
            this.totalHilos = totalHilos;
            this.totalBloquesFila = totalBloquesFila;
        }

        public void run() {
            int n = a.length;
            for (int bloqueFila = idHilo; bloqueFila < totalBloquesFila; bloqueFila += totalHilos) {
                int ii = bloqueFila * blockSize;
                int iMax = Math.min(ii + blockSize, n);

                for (int kk = 0; kk < n; kk += blockSize) {
                    int kMax = Math.min(kk + blockSize, n);
                    for (int jj = 0; jj < n; jj += blockSize) {
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

