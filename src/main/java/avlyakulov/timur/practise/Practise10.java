package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Practise10 {

    //Задача 17. Парковка (Condition)
    public static void main(String[] args) {
        //С Condition аналогичная ситуация как с synchronized, мы не можем вызвать его вне блока tryLock() and lock
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
            //если использовать tryLock() щас мы пытаемся запарковаться и если нет то просто уходим и потом еще раз цыклом заходим
            //Лучше использовать lock() тогда мы становимся в очередь и будем ждать пока лок освободиться чтоб потом зайти и захватить его
            boolean isLocked = reentrantLock.tryLock();
            //У этой реализации есть большой минус. Когда все потоки спят, то 1 поток если убрать Thread.sleep() будет постояно заезжать и уезжать
            //потому что как другой поток проснется, он просто не успеет захватить лок и он уже будет занят тем потоком который освободился и захватил его снова
            //итого 1 машина заезжает выезжает, захватывает поток и прочее. Получается поток который спал, проснулся освободил лок, 1 машина уже снова заехала
            if (isLocked) {
                if (!isParkingFull()) {
                    try {
                        addCarToParking(car);
                    } finally {
                        reentrantLock.unlock();
                    }
                    System.out.println(car.getName() + " запарковалась");
                    //Из за того что потоки спят одинаковое время потом вместе захватывают лок и 1 машина покидает парковку, 2 не может ее покинуть и остается навсегда в массиве
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
                        TimeUnit.SECONDS.sleep(2); //После того как покинули парковку, надо сделать паузу либо лок будет моментально захвачен снова тем же потоком
                    } else {
                        //если этот кейс отработал то машина просто исчезает из парковки но при этом остается в массиве и пыается снова заехать, при этом находясь в массиве
                        System.out.println("ERROR IN CODE: Car can't leave the building");
                    }
                } else {
                    try {
                        System.out.println(car.getName() + " ждет свободное место");
                        while (isParkingFull())
                            //очень интересный момент. Ломал голову почему с захваченным локом мы можем спать и потом просыпаться и работать дальше
                            //Оказывается когда мы идем в сон, мы освобождаем Lock и потом время от времени проверяем условие, если уже не занята парковка и есть место
                            //Мы пытаемся захватить Lock и если он захвачем, мы снова то мы просыпаемся и выходим и делаем действия что нужны.
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
