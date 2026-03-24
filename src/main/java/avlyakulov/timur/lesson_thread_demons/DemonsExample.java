package avlyakulov.timur.lesson_thread_demons;

import java.util.concurrent.TimeUnit;

public class DemonsExample {

    private static final String MESSAGE_THREAD_FINISHED = "Main thread is finished";

    public static void main(String[] args) {
        Thread thread = new Thread(new Task());
        thread.setDaemon(true);
        thread.start();

        System.out.println(MESSAGE_THREAD_FINISHED);
    }



    private static final class Task implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("I am working");
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
