package avlyakulov.timur.lesson_syncronized;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

public class SynchronizedExample {

    private static int firstCounter = 0;
    private static int secondCounter = 0;

    private static final int INCREMENT_AMOUNT_FIRST_COUNTER = 500;
    private static final int INCREMENT_AMOUNT_SECOND_COUNTER = 600;

    private static final Object LOCK_TO_INCREMENT_FIRST = new Object();
    private static final Object LOCK_TO_INCREMENT_SECOND = new Object();

    public static void main(String[] args) throws InterruptedException {
        Counter firstCounter = new Counter();
        Counter secondCounter = new Counter();

        Thread firstThread = createIncrementCounterThread(INCREMENT_AMOUNT_FIRST_COUNTER, i -> firstCounter.increment());
        Thread secondThread = createIncrementCounterThread(INCREMENT_AMOUNT_FIRST_COUNTER, i -> firstCounter.increment());

        Thread thirdThread = createIncrementCounterThread(INCREMENT_AMOUNT_SECOND_COUNTER, i -> secondCounter.increment());
        Thread fourhThread = createIncrementCounterThread(INCREMENT_AMOUNT_SECOND_COUNTER, i -> secondCounter.increment());

        startThreads(firstThread, secondThread, thirdThread, fourhThread);
        joinThreads(firstThread, secondThread, thirdThread, fourhThread);

        System.out.println("Result for two threads " + firstCounter.counter);
        System.out.println("Result for two threads " + secondCounter.counter);
    }

    private static final class Counter {
        private int counter;

        private void increment() {
            synchronized (this) {
                ++this.counter;
            }
        }
    }

    public static Thread createIncrementCounterThread(int incrementAmount, IntConsumer incrementingOperation) {
        return new Thread(() -> IntStream.range(0, incrementAmount).forEach(incrementingOperation));
    }

    private static void startThreads(Thread... threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }

    private static void joinThreads(Thread... threads) {
        Arrays.stream(threads).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private static synchronized void incrementFirstCounter() {
        synchronized (LOCK_TO_INCREMENT_FIRST) {
            ++firstCounter;
        }
    }

    private static synchronized void incrementSecondCounter() {
        synchronized (LOCK_TO_INCREMENT_SECOND) {
            ++secondCounter;
        }
    }

    //по сути в этом примере должно быть синхронизировано только 1 и 2 поток и 3 и 4 но не друг с другом
    //сейчас все правильно работает но synchronized лишний та как мы явно в 2 разных потоках 2 разные переменные поднимаем, мы блочим друг друга
    //здесь неявно берется монитор нашего класса текущего и это плохо
//    private static synchronized void incrementFirstCounter() {
//        ++firstCounter;
//    }

    // в данный момент 4 потока работают по монитору нашего класса и блочаться друг другом
//    private static synchronized void incrementSecondCounter() {
//        ++secondCounter;
//    }
}
