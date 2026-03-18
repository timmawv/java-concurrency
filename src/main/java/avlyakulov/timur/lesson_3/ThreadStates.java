package avlyakulov.timur.lesson_3;

public class ThreadStates {
    private static final String MESSAGE_TEMPLATE_THREAD_STATE = "%s : %s\n";


    public static void main(String[] args) throws InterruptedException {
        final Thread thread = new Thread(() -> {
            throw new RuntimeException();
        });
        thread.start();
        thread.join(); // дожидаемся пока он выполниться
        // Поток все равно выполнился, та как runtime был в другом потоке а main работает полностью правильно и нет проблем
        showThreadState(thread); // Terminated он уже полностью выполнил свою работу теперь ставится финальное состояние.
    }


    public static void exampleWithTimedWaiting() throws InterruptedException {
        final Thread mainThread = Thread.currentThread();
        final Thread thread = new Thread(() -> {
            try {
                mainThread.join(2000);
                showThreadState(Thread.currentThread());
            } catch (InterruptedException interruptedException) {

            }
        });
        thread.start();
        Thread.sleep(1000);
        showThreadState(thread);// здесь будет TIMED_WAITING та как мы задали время методу join()
    }

    public static void exampleWithWaiting() throws InterruptedException {
        //здесь логика понятна, мы в потоке вызываем main.join() это значит что мы ждем пока полностью выполниться сам поток main все его составляющие
        //после мы ждем, соотетвественно наш поток будет waiting состояние. После всего что выполниться уже запуститься наш поток будет Runnable
        final Thread mainThread = Thread.currentThread();
        final Thread thread = new Thread(() -> {
            try {
                mainThread.join();
                showThreadState(Thread.currentThread());// RUNNABLE после того как main выполнит работу, мы тут запустим
            } catch (InterruptedException interruptedException) {

            }
        });
        thread.start();
        Thread.sleep(1000);
        showThreadState(thread); // WAITING здесь будет waiting та как мы еще ждем что наш поток main выполняется мы его сделали join в нашем потоке
    }

    public static void exampleWithNewRunnable() {
        final Thread thread = new Thread(() -> showThreadState(Thread.currentThread()));

        showThreadState(thread); //тут state NEW

        thread.start(); // state RUNNABLE
    }

    private static void showThreadState(final Thread thread) {
        System.out.printf(String.format(MESSAGE_TEMPLATE_THREAD_STATE, thread.getName(), thread.getState()));
    }
}
