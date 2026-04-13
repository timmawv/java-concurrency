package avlyakulov.timur.lesson_reentrant_lock;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class LockRunner {


    public static void main(String[] args) throws InterruptedException {
        EventNumberGenerator eventNumberGenerator = new EventNumberGenerator();
        Runnable generatingTask = () -> IntStream.range(0, 100).forEach(i -> {
            eventNumberGenerator.generateWithLock();
            System.out.println(eventNumberGenerator.getPreviousGenerated());
        });

        Thread firstThread = new Thread(generatingTask);
        Thread secondThread = new Thread(generatingTask);
        Thread thirdThread = new Thread(generatingTask);

        startThreads(firstThread, secondThread, thirdThread);
    }

    public static void startThreads(Thread... threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }

    static class EventNumberGenerator {

        private final Lock lock;
        private int previousGenerated;

        public EventNumberGenerator() {
            this.previousGenerated = -2;
            this.lock = new ReentrantLock();
        }

        public void generate() {
            ++previousGenerated;
        }

        public int generateWithLock() {
            return lock.tryLock() ? onSuccessAcquireLock() : onFailureAcquireLock();
        }

        private int onSuccessAcquireLock() {
            try {
                return this.previousGenerated += 2;
            } finally {
                lock.unlock();
            }
        }

        private int onFailureAcquireLock() {
            System.out.printf("Thread %s didn't acquire lock\n", Thread.currentThread().getName());
            throw new RuntimeException();
        }

        public int getPreviousGenerated() {
            return previousGenerated;
        }
    }
}
