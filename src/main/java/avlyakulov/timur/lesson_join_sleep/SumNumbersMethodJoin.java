package avlyakulov.timur.lesson_join_sleep;

import java.util.stream.IntStream;

public class SumNumbersMethodJoin {

    private static final int FROM_NUMBER_FIRST_THREAD = 1;
    private static final int TO_NUMBER_FIRST_THREAD = 500;

    private static final int FROM_NUMBER_SECOND_THREAD = 501;
    private static final int TO_NUMBER_SECOND_THREAD = 1000;

    private static final String TEMPLATE_MESSAGE_THREAD_NAME_AND_NUMBER = "%s : %d\n";

    public static void main(String[] args) throws InterruptedException {
        TaskSummingNumbers firstTask = new TaskSummingNumbers(FROM_NUMBER_FIRST_THREAD, TO_NUMBER_FIRST_THREAD);
        final Thread firstThread = new Thread(firstTask);
        firstThread.start();

        TaskSummingNumbers secondTask = new TaskSummingNumbers(FROM_NUMBER_SECOND_THREAD, TO_NUMBER_SECOND_THREAD);
        final Thread secondThread = new Thread(secondTask);
        secondThread.start();

        waitThreadForFinish(firstThread, secondThread);//здесь уже другая ситуация мы уже реально ждем окончания нашей работы, чтобы получить нужный результат

        final int resultNumber = firstTask.getResultNumber() + secondTask.getResultNumber();

        printThreadNameAndNumber(resultNumber);
    }

    private static void waitThreadForFinish(final Thread... threads) throws InterruptedException {
        for (Thread thread : threads)
            thread.join();
    }

    private static void printThreadNameAndNumber(final int number) {
        System.out.printf(TEMPLATE_MESSAGE_THREAD_NAME_AND_NUMBER, Thread.currentThread().getName(), number);
    }

    private static final class TaskSummingNumbers implements Runnable {
        private static final int INITIAL_VALUE_RESULT = 0;

        private final int fromNumber;
        private final int toNumber;
        private int resultNumber;

        public TaskSummingNumbers(int fromNumber, int toNumber) {
            this.fromNumber = fromNumber;
            this.toNumber = toNumber;
            this.resultNumber = INITIAL_VALUE_RESULT;
        }

        @Override
        public void run() {
            IntStream.rangeClosed(fromNumber, toNumber).forEach(i -> resultNumber += i);
            printThreadNameAndNumber(resultNumber);
        }

        public int getResultNumber() {
            return this.resultNumber;
        }
    }
}