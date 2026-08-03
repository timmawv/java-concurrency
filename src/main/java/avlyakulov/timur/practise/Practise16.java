package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.SneakyThrows;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Practise16 {

    //Задача 25. Подготовка ракеты ⭐
    //CountDownLatch
    @SneakyThrows
    public static void main(String[] args) {
        int rocketParts = 5;
        CountDownLatch latch = new CountDownLatch(rocketParts);
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(rocketParts);
        IntStream.rangeClosed(1, rocketParts).forEach(i -> executorService.execute(createRunnable(latch, random)));
        System.out.println("Preparing to launch rocket");
        latch.await();
        System.out.println("Rocket was launched");
        executorService.shutdown();
    }

    private static Runnable createRunnable(CountDownLatch latch, Random random) {
        return () -> {
            int timeToSleep = random.nextInt(2, 6);
            try {
                TimeUnit.SECONDS.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                LoggerColor.printMessageWithColor(Thread.currentThread().getName().concat(" finished his part"), LoggerColor.Color.GREEN);
                latch.countDown();
            }
        };
    }
}