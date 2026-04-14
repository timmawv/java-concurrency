package avlyakulov.timur.lesson_reentrant_lock_advantages;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class AdvanceLockRunner {

    public static void main(String[] args) throws InterruptedException {
        //здесь рассмотрели дополнительные возможности ReentrantLock
        //new ReentrantLock(boolean fairness) - параметр указывает на то что сначала будут браться потоки которые дольше всех в очереди стоят
        //самый долгий поток потом сразу отработает
        Counter counter = new Counter();

        int incrementAmount = 10;
        Thread incementingThread = new Thread(createTaskDoingOperation(counter, i -> counter.increment(), incrementAmount));

        int decrementAmount = 10;
        Thread decrementingThread = new Thread(createTaskDoingOperation(counter, i -> counter.decrement(), decrementAmount));

        startThreads(incementingThread, decrementingThread);
        waitUntilFinish(incementingThread, decrementingThread);

        System.out.printf("Counter's value %d\n", counter.getValue());
    }

    private static void startThreads(final Thread... threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }

    private static void waitUntilFinish(Thread... threads) throws InterruptedException {
        for (Thread thread : threads)
            thread.join();
    }

    private static Runnable createTaskDoingOperation(Counter counter, IntConsumer operation, int times) {
        return () -> {
            counter.lock();
            try {
                IntStream.range(0, times).forEach(operation);
            } finally {
                counter.unlock();
            }
        };
    }

    private static class Counter {
        private final Lock lock = new ReentrantLock();
        private int value;

        public void lock() {
            lock.lock();
            printThreadMessage("Thread %s locked counter\n");
        }

        public void increment() {
            value++;
            printThreadMessage("Thread %s incremented our counter\n");
        }

        public void decrement() {
            value--;
            printThreadMessage("Thread %s decremented our counter\n");
        }

        public void unlock() {
            printThreadMessage("Thread %s unlocked counter\n");
            lock.unlock();
        }

        public int getValue() {
            return value;
        }

        private static void printThreadMessage(String message) {
            System.out.printf(message, Thread.currentThread().getName());
        }
    }
}
