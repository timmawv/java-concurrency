package avlyakulov.timur.lesson_thread_factory;

import java.util.concurrent.ThreadFactory;

public class ExampleWithFactory {

    private final static String MESSAGE_EXCEPTION_TEMPLATE = "Exception was thrown with message - %s in thread - %s.\n";

    public static void main(String[] args) throws InterruptedException {
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (thread, exception)
                -> System.out.printf(MESSAGE_EXCEPTION_TEMPLATE, exception.getMessage(), thread.getName());

        //пример фабрики потоков, которая сама в себе создает и настраивает его а мы потом просто вызываем
        ThreadFactory threadFactory = new DaemonThreadWithUncaughtExceptionHandlerFactory(uncaughtExceptionHandler);

        Thread thread = threadFactory.newThread(new Task());
        thread.start();

        Thread secondThread = threadFactory.newThread(new Task());
        secondThread.start();

        thread.join();
        secondThread.join();
    }

    private static final class Task implements Runnable {

        private static final String EXCEPTION_MESSAGE  = "I am exception";

        @Override
        public void run() {
            System.out.println(Thread.currentThread().isDaemon());
            throw new RuntimeException(EXCEPTION_MESSAGE);
        }
    }

    private static final class DaemonThreadWithUncaughtExceptionHandlerFactory implements ThreadFactory {
        private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

        public DaemonThreadWithUncaughtExceptionHandlerFactory(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);
            thread.setDaemon(true);
            return thread;
        }
    }
}
