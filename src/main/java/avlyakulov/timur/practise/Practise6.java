package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Practise6 {

    public static void main(String[] args) {
        //wait и notify могут вызываться только в синхроинизрованном методе, без этого будет ошибка
        Queue<String> tasks = new ArrayDeque<>();

        Producer producer = new Producer();
        Consumer consumer = new Consumer();

        Thread producerThread = new Thread(createThreadInLoop(() -> producer.produce(tasks)));
        Thread consumerThread = new Thread(createThreadInLoop(() -> consumer.consume(tasks)));

        producerThread.start();
        consumerThread.start();
    }

    private static Runnable createThreadInLoop(Runnable action) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                action.run();
            }
        };
    }

    static class Producer {

        private int counter = 0;

        @SneakyThrows
        public synchronized void produce(Queue<String> tasks) {
            if (tasks.size() >= 10) {
                wait();
            }
            produceTask(tasks);
            notify();
        }

        @SneakyThrows
        private void produceTask(Queue<String> tasks) {
            ++counter;
            String task = "Task-" + counter;
            tasks.add(task);
            System.out.println("Added " + task);
            TimeUnit.SECONDS.sleep(1);
        }
    }

    static class Consumer {

        @SneakyThrows
        public synchronized void consume(Queue<String> tasks) {
            //todo решить проблему зацикливания, щас потоки засыпают сном
            if (tasks.isEmpty())
                wait();
            consumeTask(tasks);
            notify();
        }

        @SneakyThrows
        private void consumeTask(Queue<String> tasks) {
            String task = tasks.poll();
            System.out.println("Took " + task);
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
