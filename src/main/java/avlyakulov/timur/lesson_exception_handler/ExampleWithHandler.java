package avlyakulov.timur.lesson_exception_handler;

public class ExampleWithHandler {

    private final static String MESSAGE_EXCEPTION_TEMPLATE = "Exception was thrown with message - %s in thread - %s.\n";

    public static void main(String[] args) {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, exception)
                -> System.out.printf(MESSAGE_EXCEPTION_TEMPLATE, exception.getMessage(), thread.getName());

        //будет устанавливаться в каждый поток в нашей программе, по дефолту
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

        Thread thread = new Thread(new Task());
        thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        thread.start();

        //в этих котоках будут устанавливаться наш обработчик который обрабатывает исключения
        //в метод run с него мы можем кинуть только непроверяемое иключение
        Thread secondThread = new Thread(new Task());
        secondThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        secondThread.start();
    }

    private static final class Task implements Runnable {

        private static final String EXCEPTION_MESSAGE  = "I am exception";

        @Override
        public void run() {
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
    }
}
