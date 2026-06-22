package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class Practise7 {

    public static void main(String[] args) {
        //wait и notify могут вызываться только в синхроинизрованном методе, без этого будет ошибка
        Queue<String> tasks = new ArrayDeque<>();

        MessageBroker messageBroker = new MessageBroker();

        Thread producerThread = new Thread(createThreadInLoop(() -> messageBroker.produce(tasks), true));
        Thread producerThread2 = new Thread(createThreadInLoop(() -> messageBroker.produce(tasks), true));
        Thread producerThread3 = new Thread(createThreadInLoop(() -> messageBroker.produce(tasks), true));
        Thread consumerThread = new Thread(createThreadInLoop(() -> messageBroker.consume(tasks), false));

        producerThread.start();
        producerThread2.start();
        producerThread3.start();
        consumerThread.start();
    }

    private static Runnable createThreadInLoop(Runnable action, boolean isProducer) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (isProducer) {
                        TimeUnit.SECONDS.sleep(1);
                    } else {
                        TimeUnit.SECONDS.sleep(3);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                action.run();
            }
        };
    }

    static class MessageBroker {

        private int counter = 0;

        @SneakyThrows
        public synchronized void produce(Queue<String> tasks) {
            while (tasks.size() >= 5) {
                wait();
            }
            produceTask(tasks);
            notify();
        }

        @SneakyThrows
        private void produceTask(Queue<String> tasks) {
            String task = "Task-" + ++counter;
            tasks.add(task);
            System.out.println("Added " + task);
        }

        @SneakyThrows
        public synchronized void consume(Queue<String> tasks) {
            while (tasks.isEmpty())
                wait();
            consumeTask(tasks);
            notify();
        }

        @SneakyThrows
        private void consumeTask(Queue<String> tasks) {
            String task = tasks.poll();
            System.out.println("Took " + task);
        }
    }
}
