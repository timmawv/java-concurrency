package avlyakulov.timur.lesson_4;

import java.util.concurrent.TimeUnit;

public class InterruptionThreads {

    private static final String MESSAGE_REQUEST_WAS_SENT = "Request was sent.\n";
    private static final String MESSAGE_RESPONSE_WAS_RECEIVED = "Request was received.\n";
    private static final String MESSAGE_SERVER_WAS_STOPPED = "Server was stopped.";
    private static final String MESSAGE_THREAD_WAS_INTERRUPTED = "Thread was interrupted";

    public static void main(String[] args) throws InterruptedException {
        Thread communicateThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    doRequest();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();//здесь мы сами прерываем, чтоб точно понимать, что поток был прерван.
                System.out.println(Thread.currentThread().isInterrupted());// здесь будет false. Та как мы отловили исключение.
                System.out.println(MESSAGE_THREAD_WAS_INTERRUPTED); //было проброшено исключение из за метода sleep в doRequests();
            }
        });
        communicateThread.start();

        Thread stoppingThread = new Thread(() -> {
            if (isServerShouldBeTurnedOff()) {
                communicateThread.interrupt(); //мы здесь просто ставим признак isInterrupted = true. Мы как бы просим прерваться
                stopServer();
            }
        });
        TimeUnit.SECONDS.sleep(3);
        stoppingThread.start();
    }

    private static void doRequest() throws InterruptedException {
        System.out.println(MESSAGE_REQUEST_WAS_SENT);
        TimeUnit.SECONDS.sleep(1);
        System.out.println(MESSAGE_RESPONSE_WAS_RECEIVED);
    }

    private static boolean isServerShouldBeTurnedOff() {
        return true;
    }

    private static void stopServer() {
        System.out.println(MESSAGE_SERVER_WAS_STOPPED);
    }

}
