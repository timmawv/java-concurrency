package avlyakulov.timur.practise;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Practise4 {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        createListRunnable().forEach(executorService::execute);
        executorService.shutdown();
    }

    private static List<Runnable> createListRunnable() {
        List<Runnable> runnables = new ArrayList<>();
        IntStream.rangeClosed(0, 10).forEach(i -> runnables.add(createRunnable()));
        return runnables;
    }

    private static Runnable createRunnable() {
        return () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
