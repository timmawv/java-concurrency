package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Practise15 {

    //Задача 23. Парковка 2.0 ⭐
    //Semaphore
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2, true);
        Random random = new Random();
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.rangeClosed(1, 6).forEach(i -> executorService.execute(new Car(i, semaphore, random)));
    }


    @Getter
    @AllArgsConstructor
    static class Car implements Runnable {

        private int id;
        private Semaphore semaphore;
        private Random random;

        @SneakyThrows
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    semaphore.acquire();
                    LoggerColor.printMessageWithColor("Car with this id %d went to the parking".formatted(this.id), LoggerColor.Color.GREEN);
                    int timeToSleep = random.nextInt(3, 5);
                    TimeUnit.SECONDS.sleep(timeToSleep);
                } catch (InterruptedException e) {
                    LoggerColor.printMessageWithColor("ERROR ERROR!", LoggerColor.Color.RED);
                } finally {
                    LoggerColor.printMessageWithColor("Car with this id %d left the parking".formatted(this.id), LoggerColor.Color.RED);
                    semaphore.release();
                }
            }
        }
    }
}
