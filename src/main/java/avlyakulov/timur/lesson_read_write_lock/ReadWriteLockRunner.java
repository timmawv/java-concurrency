package avlyakulov.timur.lesson_read_write_lock;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;

//no Optional
//255858178 - ReentrantLock
//11172312 - ReadWriteLock
//with Optional
//54 - ReentrantLock
//200 - ReadWriteLock
public class ReadWriteLockRunner {
    //todo разобрать этот пример лучше. В целом понятно что ReadWriteLock лучше когда операции записи занимают определенное время
    public static void main(String[] args) throws InterruptedException {
        testCounter(() -> new CounterProtectedByLock());
    }

    private static void testCounter(Supplier<? extends AbstractCounter> counterFactory) throws InterruptedException {
        AbstractCounter counter = counterFactory.get();

        int amountThreadsGettingValue = 50;
        ReadingValueTask[] readingValueTasks = createReadingValueTask(counter, amountThreadsGettingValue);
        Thread[] readingValueThreads = mapToThreads(readingValueTasks);

        Runnable incrementingCounterTask = createIncrementingCounterTask(counter);
        int amountOfThreadsIncrementingCounter = 2;
        Thread[] incrementingCounterThreads = createThreads(incrementingCounterTask, amountOfThreadsIncrementingCounter);

        startThreads(readingValueThreads);
        startThreads(incrementingCounterThreads);

        TimeUnit.SECONDS.sleep(5);

        interruptThreads(readingValueThreads);
        interruptThreads(incrementingCounterThreads);

        waitUntilFinish(readingValueThreads);

        long totalAmountOfReads = findTotalAmountOfReads(readingValueTasks);
        System.out.printf("Amount of readings value: %d\n", totalAmountOfReads);
    }

    private static ReadingValueTask[] createReadingValueTask(AbstractCounter counter, int amountOfTasks) {
        return IntStream.range(0, amountOfTasks)
                .mapToObj(i -> new ReadingValueTask(counter))
                .toArray(ReadingValueTask[]::new);
    }

    private static Thread[] mapToThreads(Runnable[] tasks) {
        return Arrays.stream(tasks)
                .map(Thread::new)
                .toArray(Thread[]::new);
    }

    private static Runnable createIncrementingCounterTask(AbstractCounter counter) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                incrementCounter(counter);
            }
        };
    }

    private static void incrementCounter(AbstractCounter counter) {
        try {
            counter.increment();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static Thread[] createThreads(Runnable task, int amountOfThreads) {
        return IntStream.range(0, amountOfThreads)
                .mapToObj(i -> new Thread(task))
                .toArray(Thread[]::new);
    }

    private static void interruptThreads(Thread[] threads) {
        forEach(threads, Thread::interrupt);
    }

    private static void forEach(Thread[] threads, Consumer<Thread> action) {
        Arrays.stream(threads)
                .forEach(action);
    }

    private static void startThreads(Thread[] threads) {
        forEach(threads, Thread::start);
    }

    private static void waitUntilFinish(Thread[] threads) {
        forEach(threads, ReadWriteLockRunner::waitUntilFinish);
    }

    private static void waitUntilFinish(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static long findTotalAmountOfReads(ReadingValueTask[] tasks) {
        return Arrays.stream(tasks)
                .mapToLong(ReadingValueTask::getAmountOfReads)
                .sum();
    }

    static class ReadingValueTask implements Runnable {
        private final AbstractCounter counter;
        private long amountOfReads;

        public ReadingValueTask(AbstractCounter counter) {
            this.counter = counter;
        }

        public long getAmountOfReads() {
            return this.amountOfReads;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                this.counter.getValue();
                this.amountOfReads++;
            }
        }
    }
}
