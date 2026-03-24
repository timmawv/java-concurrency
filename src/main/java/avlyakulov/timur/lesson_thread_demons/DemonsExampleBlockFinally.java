package avlyakulov.timur.lesson_thread_demons;

import java.util.concurrent.TimeUnit;

public class DemonsExampleBlockFinally {


    public static void main(String[] args) throws InterruptedException {
        //всегда нужно помнить, что приложение завершит все потоки демоны в каком бы они моменте не были б
        Thread thread = new Thread(new Task());
        thread.setDaemon(true);
        thread.start();

        TimeUnit.SECONDS.sleep(1);
    }


    private static final class Task implements Runnable {

        private static final String MESSAGE_START_WORKING = "I am working";
        private static final String MESSAGE_END_WORKING = "I am finished";

        @Override
        public void run() {
            try {
                System.out.println(MESSAGE_START_WORKING);
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                //в данном случае это не выполниться, та как это поток демон и он моментально заканчивает работу
                System.out.println(MESSAGE_END_WORKING);
            }
        }
    }
}
