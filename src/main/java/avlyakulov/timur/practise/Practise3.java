package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.stream.IntStream;

public class Practise3 {

    private static int counter = 0;

    @SneakyThrows
    public static void main(String[] args) {
        Thread[] threads = createThreads();
        runThreads(threads);
        joinThreads(threads);
        System.out.println(counter);
    }

    private static Thread[] createThreads() {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; ++i) {
            Thread thread = new ThreadExample();
            threads[i] = thread;
        }
        return threads;
    }

    private static void runThreads(Thread... threads)  {
        for (Thread thread : threads) {
            thread.start();
        }
    }

    @SneakyThrows
    private static void joinThreads(Thread... threads)  {
        for (Thread thread : threads) {
            thread.join();
        }
    }

    static class ThreadExample extends Thread {

        @Override
        public void run() {
            IntStream.rangeClosed(0, 10_000).forEach(i -> increase());
        }

        private void increase() {
            ++counter;
        }
    }
}
