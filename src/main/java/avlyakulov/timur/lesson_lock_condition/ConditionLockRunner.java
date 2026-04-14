package avlyakulov.timur.lesson_lock_condition;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ConditionLockRunner {

    public static void main(String[] args) {
        BoundedBuffer<Integer> boundedBuffer = new BoundedBuffer<>(5);

        Runnable producingTask = () -> Stream.iterate(0, i-> i+1).forEach(i -> {
            try {
                boundedBuffer.put(i);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread producingThread = new Thread(producingTask);

         Runnable consumingTask = () -> {
             try {
                 while(!Thread.currentThread().isInterrupted()) {
                     boundedBuffer.take();
                     TimeUnit.SECONDS.sleep(3);
                 }
             } catch (InterruptedException e) {
                 Thread.currentThread().interrupt();
             }
         };
         Thread consumingThread = new Thread(consumingTask);

         producingThread.start();
         consumingThread.start();
    }
}
