package avlyakulov.timur.lesson_threads;

import static java.util.stream.IntStream.range;

public class RunnablePractise {

    private static final int CREATE_THREADS_AMOUNT = 10;

    public static void main(String[] args) {
        //это функциональный интерфейс этот вариант предпочтительнее
        // мы здесь отделяем этот механизм и потом мы передаем эту задачу
        final Runnable task = () -> System.out.println(Thread.currentThread().getName());
        final Thread thread = new Thread(task);
        thread.start();

        final Runnable taskDisplayThreadName = () -> System.out.println(Thread.currentThread().getName());
        final Runnable taskCreatingThreads = () ->
                range(0, CREATE_THREADS_AMOUNT)
                        .forEach(i -> startThread(taskDisplayThreadName));
        startThread(taskCreatingThreads);
    }

    private static void startThread(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        thread.start();
    }
}
