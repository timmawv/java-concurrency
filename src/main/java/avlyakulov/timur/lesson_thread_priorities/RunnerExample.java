package avlyakulov.timur.lesson_thread_priorities;

import java.util.stream.IntStream;

public class RunnerExample {

    private static final String MESSAGE_MAIN_THREAD_FINISHED = "Main thread is finished";

    public static void main(String[] args) {
        Thread thread = new Thread(new Task());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();

        System.out.println(MESSAGE_MAIN_THREAD_FINISHED); //поток main с приоритетом 5 выполниться быстрее чем с наш с 10 приоритетом.
        //Мы не можем сами на это повлиять никак, поэтому лучше не манипулировать приоритетом потоков.
    }

    private static final class Task implements Runnable {

        @Override
        public void run() {
            IntStream.range(1, 100).forEach(System.out::println);
        }
    }
}
