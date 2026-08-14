package avlyakulov.timur.practisev2;

import avlyakulov.timur.utils.LoggerColor;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class Practise17 {

    //Задача 27. Автогонки ⭐⭐
    //CyclicBarrier
    public static void main(String[] args) {
        int cars = 6;
        //тут был вызван Runnable 2 аргументом, он вызывается, до разблокировки всех потоков и отрабатывает, потом потоки разблокируются и работают дальше
        CyclicBarrier cyclicBarrier = new CyclicBarrier(6, () -> LoggerColor.printMessageWithColor("The race has started", LoggerColor.Color.GREEN));
        ExecutorService executorService = Executors.newFixedThreadPool(cars);
        IntStream.rangeClosed(1, cars).forEach(i -> executorService.execute(createCar(cyclicBarrier)));
        executorService.shutdown();
    }

    private static Runnable createCar(CyclicBarrier cyclicBarrier) {
        return () -> {
            try {
                TimeUnit.SECONDS.sleep(getRandomNumber());
                LoggerColor.printMessageWithColor(Thread.currentThread().getName().concat(" has arrived to start line"), LoggerColor.Color.YELLOW);
                cyclicBarrier.await();
                LoggerColor.printMessageWithColor(Thread.currentThread().getName().concat(" has started the race"), LoggerColor.Color.BLUE);
                TimeUnit.SECONDS.sleep(getRandomNumber());
                LoggerColor.printMessageWithColor(Thread.currentThread().getName().concat(" has arrived to finish line"), LoggerColor.Color.GREEN);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private static int getRandomNumber() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }
}
