package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
        Semaphore semaphore = new Semaphore(3);
        ArrayDeque<Car> cars = IntStream.rangeClosed(1, 6).mapToObj(Car::new).collect(Collectors.toCollection(ArrayDeque::new));
        Parking parking = new Parking();
        ParkWorker parkWorker = new ParkWorker(cars, parking, semaphore);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        IntStream.rangeClosed(1, 6).forEach(i -> executorService.execute(parkWorker));
    }

    @AllArgsConstructor
    static class ParkWorker implements Runnable {
        private Queue<Car> cars;
        private Parking parking;
        private Semaphore semaphore;


        @SneakyThrows
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Car car = cars.poll();
                try {
                    semaphore.acquire();
                    parking.addCardToParking(car);
                    TimeUnit.SECONDS.sleep(3);
                    parking.removeCardToParking(car.getId());
                } finally {
                    semaphore.release();
                }
                cars.add(car);
            }
        }
    }

    @Getter
    static class Parking {
        private List<Car> carsOnParking = new ArrayList<>();

        public void addCardToParking(Car car) {
            carsOnParking.add(car);
            LoggerColor.printMessageWithColor("Car with this id %d joined the parking".formatted(car.getId()), LoggerColor.Color.GREEN);
        }

        public void removeCardToParking(int carId) {
            if (carsOnParking.removeIf(car -> car.getId() == carId))
                LoggerColor.printMessageWithColor("Car with this id %d left the parking".formatted(carId), LoggerColor.Color.RED);
            else throw new IllegalArgumentException();
        }
    }



    @Getter
    @AllArgsConstructor
    static class Car {
        private int id;
    }
}
