package it.unibo.oop.workers01;

import java.util.List;
import java.util.stream.IntStream;

/**
 * This is an implementation using streams.
 *
 */
@SuppressWarnings("CPD-START")
public final class MultiThreadedListSumWithStreams implements SumList {

    private final int nthread;

    /**
     * Builds a multithreaded list sum using streams.
     *
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedListSumWithStreams(final int nthread) {
        this.nthread = nthread;
    }

    @Override
    public long sum(final List<Integer> list) {
        final int size = list.size() % nthread + list.size() / nthread;
        /*
         * Build a stream of workers
         */
        return IntStream.iterate(0, start -> start + size)
            .limit(nthread)
            .mapToObj(start -> new Worker(list, start, size))
            // Start them
            .peek(worker -> worker.thread.start())
            // Join them
            .peek(worker -> joinUninterruptibly(worker.thread))
            // Get their result and sum
            .mapToLong(Worker::getResult)
            .sum();
    }

    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Worker implements Runnable {
        private final List<Integer> list;
        private final int startpos;
        private final int nelem;
        private final Thread thread;
        private long res;

        /**
         * Build a new worker.
         *
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final List<Integer> list, final int startpos, final int nelem) {
            this.list = list;
            this.startpos = startpos;
            this.nelem = nelem;
            this.thread = new Thread(this);
        }

        @Override
        public synchronized void run() {
            IO.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.size() && i < startpos + nelem; i++) {
                this.res += this.list.get(i);
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         *
         * @return the sum of every element in the array
         */
        synchronized long getResult() {
            return this.res;
        }

    }
}
