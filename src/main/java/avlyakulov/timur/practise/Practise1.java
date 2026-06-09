package avlyakulov.timur.practise;

import java.util.stream.IntStream;

public class Practise1 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new ThreadClass();
        thread.start();

        Runnable runnable = () -> IntStream.range(0, 10).forEach(System.out::println);
        Thread threadInterface = new Thread(runnable);
        threadInterface.start();
    }

    static class ThreadClass extends Thread {
        @Override
        public void run() {
            IntStream.range(0, 10).forEach(System.out::println);
        }
    }
}
