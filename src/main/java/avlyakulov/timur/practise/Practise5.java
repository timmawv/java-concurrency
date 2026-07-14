package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class Practise5 {

    //задача пример разницы Callable and Runnable
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        RunnableExample runnableExample = new RunnableExample();
        CallableExample callableExample = new CallableExample();
        executorService.submit(runnableExample);
        Future<Long> callable = executorService.submit(callableExample);
        System.out.println(callable.get());
        System.out.println(runnableExample.count);

        executorService.shutdown();
    }

    static class RunnableExample implements Runnable {

        private Long count = 0L;

        @Override
        public void run() {
            count = IntStream.rangeClosed(0, 100).count();
        }
    }

    static class CallableExample implements Callable<Long> {

        @Override
        public Long call() {
            return IntStream.rangeClosed(0, 100).count();
        }
    }
}
