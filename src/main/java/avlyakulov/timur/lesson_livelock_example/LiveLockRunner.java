package avlyakulov.timur.lesson_livelock_example;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiveLockRunner {

    public static void main(String[] args) {
        Lock firstLock = new ReentrantLock();
        Lock secondLock = new ReentrantLock();
        //в логах выводится немного не точно. Щас потоки пытаются захватить локи друг друга то есть 1 поток 1 лок 2 поток 2 лок
        //После они пытаются захватить другие локи 1 поток 2 лок а 2 поток 1 лок, но они уступают друг другу место чтоб другой захватил и не захватывают в итоге ничего
        Thread firstThread = new Thread(new Task(firstLock, secondLock));
        Thread secondThread = new Thread(new Task(secondLock, firstLock));

        firstThread.start();
        secondThread.start();
    }
}
