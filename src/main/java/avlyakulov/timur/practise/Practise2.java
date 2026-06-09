package avlyakulov.timur.practise;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class Practise2 {

    public static void main(String[] args) throws InterruptedException {
        //2 task
        Thread ping = createThreadWithPrinting("ping");
        Thread pong = createThreadWithPrinting("pong");
        runThreads(ping, pong);
        //3 task
        AtomicInteger number = new AtomicInteger();
        Runnable runnable = () -> IntStream.rangeClosed(0, 1_000_000).forEach(number::addAndGet);
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join();
        System.out.println(number);
    }

    private static Thread createThreadWithPrinting(String word) {
        Runnable runnable = () -> IntStream.range(0, 11).forEach((i) -> System.out.println(word));
        return new Thread(runnable);
    }

    private static void runThreads(Thread... threads) {
        for (Thread thread : threads)
            thread.start();
    }
}
