package avlyakulov.timur.lesson_thread_demons;

public class DemonsExampleInheritance {

    private static final String MESSAGE_TEMPLATE_THREAD_NAME_AND_DEMON_STATUS = "%s : %b\n";

    public static void main(String[] args)  {
        Thread thread = new Thread(() -> printThreadNameAndDemonStatus(Thread.currentThread()));
        thread.start();
        thread.setDaemon(true); //Здесь будет ошибка, та как поток уже начат и мы пытаемся ему установить статус.
    }

    //Здесь мы просто создали поток который запускает другой демон, пример того что один поток неявно наследуется от другого, то есть 2 поток уже демон с начала
    private static void exampleWithRunning() throws InterruptedException {
        Thread firstDaemonThread = new Thread(() -> {
            try {
                printThreadNameAndDemonStatus(Thread.currentThread());
                Thread secondDaemonThread = new Thread(() -> printThreadNameAndDemonStatus(Thread.currentThread()));
                secondDaemonThread.start();
                secondDaemonThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        firstDaemonThread.setDaemon(true);
        firstDaemonThread.start();

        firstDaemonThread.join();
    }

    private static void printThreadNameAndDemonStatus(Thread thread) {
        System.out.printf(MESSAGE_TEMPLATE_THREAD_NAME_AND_DEMON_STATUS, thread.getName(), thread.isDaemon());
    }
}
