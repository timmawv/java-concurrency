package avlyakulov.timur.lesson_thread_priorities;

public class PrioritiesThread {

    private static final String MESSAGE_TEMPLATE_THREAD_NAME = "%s : %d\n";

    public static void main(String[] args) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY); // если установить больше 10 то будет exception, разрешеныые значения от 1-10

        Thread thread = new Thread(() -> printNameAndPriority(Thread.currentThread()));
        thread.start(); //интересно тут уже вывелось Thread-0 : 10 то есть все потоки неявно наследуються от main

        printNameAndPriority(Thread.currentThread()); // по дефолту это поток main имя его main и приоритет 5
    }

    private static void printNameAndPriority(Thread thread) {
        System.out.printf(MESSAGE_TEMPLATE_THREAD_NAME, thread.getName(), thread.getPriority());
    }
}
