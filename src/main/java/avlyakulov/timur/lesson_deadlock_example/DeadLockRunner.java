package avlyakulov.timur.lesson_deadlock_example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockRunner {
    public static void main(String[] args) {
        Lock firstLock = new ReentrantLock();
        Lock secondLock = new ReentrantLock();

        //здесь получается deadLock та как 1 поток захватывает 1 лок, потом 2 поток 2 лок
        //и после 1 поток пытается 2 лок, а 2 поток 1 лок, но оба захваченые и уже блочатся
        //надо передавать 1 последовательности 1 2 лок, а не как тут
        Thread firstThread = new Thread(new Task(firstLock, secondLock));
        Thread secondThread = new Thread(new Task(secondLock, firstLock));

        firstThread.start();
        secondThread.start();
    }
}
