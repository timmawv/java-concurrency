package avlyakulov.timur.lesson_livelock_example;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class Task implements Runnable {
    private static String MESSAGE_TEMPLATE_TRY_ACQUIRE_LOCK = "Thread %s is trying to acquire lock %s\n";
    private static String MESSAGE_TEMPLATE_SUCCESS_ACQUIRE_LOCK = "Thread %s acquired lock %s\n";
    private static String MESSAGE_TEMPLATE_RELEASED_LOCK = "Thread %s released lock %s\n";

    private static String NAME_FIRST_LOCK = "first Lock";
    private static String NAME_SECOND_LOCK = "second Lock";

    private final Lock firstLock;
    private final Lock secondLock;

    public Task(Lock firstLock, Lock secondLock) {
        this.firstLock = firstLock;
        this.secondLock = secondLock;
    }

    @Override
    @SneakyThrows
    public void run() {
        String currentThreadName = Thread.currentThread().getName();
        System.out.printf(MESSAGE_TEMPLATE_TRY_ACQUIRE_LOCK, currentThreadName, NAME_FIRST_LOCK);
        firstLock.lock();
        System.out.printf(MESSAGE_TEMPLATE_SUCCESS_ACQUIRE_LOCK, currentThreadName, NAME_FIRST_LOCK);
        try {
            TimeUnit.MILLISECONDS.sleep(50);
            while(!tryAcquireSecondLock()) {
                TimeUnit.MILLISECONDS.sleep(50);
                firstLock.unlock();
                System.out.printf(MESSAGE_TEMPLATE_RELEASED_LOCK, currentThreadName, NAME_FIRST_LOCK);
                TimeUnit.MILLISECONDS.sleep(50);
                System.out.printf(MESSAGE_TEMPLATE_TRY_ACQUIRE_LOCK, currentThreadName, NAME_FIRST_LOCK);
                this.firstLock.lock();
                System.out.printf(MESSAGE_TEMPLATE_SUCCESS_ACQUIRE_LOCK, currentThreadName, NAME_FIRST_LOCK);
                TimeUnit.MILLISECONDS.sleep(50);
            }
            try {
                System.out.printf(MESSAGE_TEMPLATE_SUCCESS_ACQUIRE_LOCK, currentThreadName, NAME_SECOND_LOCK);
            } finally {
                secondLock.unlock();
                System.out.printf(MESSAGE_TEMPLATE_RELEASED_LOCK, currentThreadName, NAME_SECOND_LOCK);
            }
        } finally {
            firstLock.unlock();
            System.out.printf(MESSAGE_TEMPLATE_RELEASED_LOCK, currentThreadName, NAME_FIRST_LOCK);
        }
    }

    private boolean tryAcquireSecondLock() {
        String currentThreadName = Thread.currentThread().getName();
        System.out.printf(MESSAGE_TEMPLATE_TRY_ACQUIRE_LOCK, currentThreadName, NAME_SECOND_LOCK);
        return secondLock.tryLock();
    }
}
