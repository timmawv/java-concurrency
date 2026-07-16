package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Practise10_ImprovedSolution {

    //Задача 17. Парковка (Condition).
    //Но это улучшенное решение от прошлого, где не будет тех проблем, что я описал
    public static void main(String[] args) {
        Parking parking = new Parking();
        Thread car1 = new Thread(createRunnable(new Car(1), parking));
        Thread car2 = new Thread(createRunnable(new Car(2), parking));
        Thread car3 = new Thread(createRunnable(new Car(3), parking));
        Thread car4 = new Thread(createRunnable(new Car(4), parking));
        Thread car5 = new Thread(createRunnable(new Car(5), parking));
        Thread car6 = new Thread(createRunnable(new Car(6), parking));
        Thread car7 = new Thread(createRunnable(new Car(7), parking));

        car1.start();
        car2.start();
        car3.start();
        car4.start();
        car5.start();
        car6.start();
        car7.start();
    }

    private static Runnable createRunnable(Car car, Parking parking) {
        return () -> {
            while (!Thread.currentThread().isInterrupted())
                parking.parkCar(car);
        };
    }

    static class Parking {

        private final ReentrantLock reentrantLock = new ReentrantLock();
        private final Condition condition = reentrantLock.newCondition();

        private Car[] cars = new Car[2];
        private int currentParkPlace = 0;

        @SneakyThrows
        public void parkCar(Car car) {
            boolean isLocked = reentrantLock.tryLock();
            if (isLocked) {
                if (!isParkingFull()) {
                    try {
                        addCarToParking(car);
                    } finally {
                        reentrantLock.unlock();
                    }
                    System.out.println(car.getName() + " запарковалась");
                    TimeUnit.SECONDS.sleep(5);
                    boolean isLockInPark = reentrantLock.tryLock();
                    if (isLockInPark) {
                        try {
                            removeCarFromParking();
                            System.out.println(car.getName() + " покинула парковку");
                            condition.signal();
                        } finally {
                            reentrantLock.unlock();
                        }
                        TimeUnit.SECONDS.sleep(2);
                    } else {

                        System.out.println("ERROR IN CODE: Car can't leave the building");
                    }
                } else {
                    try {
                        System.out.println(car.getName() + " ждет свободное место");
                        while (isParkingFull())
                            condition.await();
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            } else {
                System.out.println("Reentrant Lock парковки заблокированный поэтому " + car.getName() + " ждет когда разблокируется");
            }
        }

        private void addCarToParking(Car car) {
            cars[currentParkPlace] = car;
            ++currentParkPlace;
        }

        private void removeCarFromParking() {
            cars[currentParkPlace - 1] = null;
            --currentParkPlace;
        }

        private boolean isParkingFull() {
            return currentParkPlace == cars.length;
        }
    }

    static class Car {

        private final int numberCar;

        public Car(int numberCar) {
            this.numberCar = numberCar;
        }

        public String getName() {
            return "Car-" + numberCar;
        }
    }
}
