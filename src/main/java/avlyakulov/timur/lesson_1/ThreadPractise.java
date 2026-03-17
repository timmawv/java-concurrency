package avlyakulov.timur.lesson_1;

public class ThreadPractise {

    public static void main(String[] args) {
        System.out.println(Thread.currentThread().getName()); //получили текущий main поток

        final Thread thread = new MyThread();
        thread.start(); //тут уже другое имя, та как это наш наш новый поток Thread-0

        final Thread thread2 = new Thread() {
            @Override
            public void run() {
                System.out.println(currentThread().getName());
            }
        };

        thread2.run(); //если вызывать этот метод, то тут мы вызываем main поток
        thread2.start(); // теперь тут вызывается наш нужный метод, который создает поток, то что надо
    }


    private static final class MyThread extends Thread {

        @Override
        public void run() {
            System.out.println(currentThread().getName());
        }
    }
}
