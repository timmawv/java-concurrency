package avlyakulov.timur.lesson_2;

import java.util.stream.IntStream;

public class SumNumbersMethodSleep {

    private static final int FROM_NUMBER_FIRST_THREAD = 1;
    private static final int TO_NUMBER_FIRST_THREAD = 500;

    private static final int FROM_NUMBER_SECOND_THREAD = 501;
    private static final int TO_NUMBER_SECOND_THREAD = 1000;

    private static final String TEMPLATE_MESSAGE_THREAD_NAME_AND_NUMBER = "%s : %d\n";

    public static void main(String[] args) throws InterruptedException {
        TaskSummingNumbers firstTask = startSubTask(FROM_NUMBER_FIRST_THREAD, TO_NUMBER_FIRST_THREAD);
        TaskSummingNumbers secondTask = startSubTask(FROM_NUMBER_SECOND_THREAD, TO_NUMBER_SECOND_THREAD);

        waitForOneSecond(); //щас мы получаем правильный ответ, но это не сильно правильно, та как мы заведомо не можем знать сколько задача будет выполняться

        final int resultNumber = firstTask.getResultNumber() + secondTask.getResultNumber();
        printThreadNameAndNumber(resultNumber);
    }

    private static TaskSummingNumbers startSubTask(final int fromNumber, final int toNumber) {
        final TaskSummingNumbers subTask = new TaskSummingNumbers(fromNumber, toNumber);
        final Thread thread = new Thread(subTask);
        thread.start();
        return subTask;
    }

    private static void waitForOneSecond() throws InterruptedException {
        Thread.sleep(1000);
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