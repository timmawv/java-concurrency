package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class BestExampleWithMonitorObject {

    public static void main(String[] args) {
        //wait и notify могут вызываться только в синхроинизрованном методе, без этого будет ошибка
        //этот пример хорош тем, что мы тут видим как работает метод synchronized. Как он захватывает монитор объекта в котором вызывается
        //И как работает метод wait() на том же объекте, мы вызываем wait разных потоках, но объект и монитор объекта у нас тот же и  мы вызываем sleep()
        //мы тем самым остаемся в том же методе в synchronized блоке и у нас тем самым захвачен монитор объекта и мы его удерживаем и только потом
        //мы вызываем метод notify() чтоб сказать объекту что можешь работать
        Queue<String> tasks = new ArrayDeque<>();

        MessageBroker messageBroker = new MessageBroker();

        Thread producerThread = new Thread(createThreadInLoop(() -> messageBroker.produce(tasks)));
        Thread consumerThread = new Thread(createThreadInLoop(() -> messageBroker.consume(tasks)));

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

    static class MessageBroker {
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

        @SneakyThrows
        public synchronized void consume(Queue<String> tasks) {
            if (tasks.isEmpty())
                wait();
            consumeTask(tasks);
            notify();
        }

        @SneakyThrows
        private void consumeTask(Queue<String> tasks) {
            String task = tasks.poll();
            System.out.println("Took " + task);
            TimeUnit.SECONDS.sleep(10);
        }
    }
}
