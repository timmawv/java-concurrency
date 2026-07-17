package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Practise11 {

    public static Object lockA = new Object();
    public static Object lockB = new Object();

    public static Lock reentrantLockA = new ReentrantLock();
    public static Lock reentrantLockB = new ReentrantLock();


    //Задача 18. Создай deadlock
    @SneakyThrows
    public static void main(String[] args) {
        //создал deadlock когда 2 потока в синхронизации пытаются захватить monitor другого объекта
        //создал deadlock когда 2 потока в lock пытаются захватить lock другого lock
        Thread thread = new Thread(createRunnableInSynchronized(lockA, lockB));
        Thread thread1 = new Thread(createRunnableInSynchronized(lockB, lockA));

        thread.start();
        thread1.start();

        TimeUnit.MILLISECONDS.sleep(100);

        Thread thread2 = new Thread(createRunnableInReentrantLock(reentrantLockA, reentrantLockB));
        Thread thread3 = new Thread(createRunnableInReentrantLock(reentrantLockB, reentrantLockA));

        thread2.start();
        thread3.start();
    }


    public static Runnable createRunnableInSynchronized(Object lockA, Object lockB) {
        return () -> {
            synchronized (lockA) {
                try {
                    System.out.println(Thread.currentThread().getName() + " захватил Lock A");
                    TimeUnit.MILLISECONDS.sleep(100);
                    System.out.println(Thread.currentThread().getName() + " сейчас будет захватывать Lock B");
                } catch (InterruptedException e) {
                    System.out.println("ERROR");
                }
                synchronized (lockB) {
                    System.out.println(Thread.currentThread().getName() + " захватил Lock B");
                }
                System.out.println("We have left lock B");
            }
            System.out.println("We have left lock A");
        };
    }

    public static Runnable createRunnableInReentrantLock(Lock reentrantLockA, Lock reentrantLockB) {
        return () -> {
            reentrantLockA.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " захватил Reentrant Lock A");
                TimeUnit.MILLISECONDS.sleep(100);
                System.out.println(Thread.currentThread().getName() + " сейчас будет захватывать Reentrant Lock B");
                reentrantLockB.lock();
                try {
                    System.out.println(Thread.currentThread().getName() + " захватил Reentrant Lock B");
                } finally {
                    reentrantLockB.unlock();
                    System.out.println("We have left lock B");
                }
            } catch (InterruptedException e) {
                System.out.println("ERROR");
            } finally {
                reentrantLockA.unlock();
                System.out.println("We have left lock A");
            }
        };
    }
}
