package avlyakulov.timur.lesson_race_condition;

import java.util.stream.IntStream;

public class SynchronizedExample {

    private static int counter = 0;

    private static final int INCREMENT_AMOUNT_FIRST_THREAD  = 500;
    private static final int INCREMENT_AMOUNT_SECOND_THREAD  = 600;

    public static void main(String[] args) throws InterruptedException {
        Thread firstThread = createIncrementCounterThread(INCREMENT_AMOUNT_FIRST_THREAD);
        Thread secondThread = createIncrementCounterThread(INCREMENT_AMOUNT_SECOND_THREAD);

        firstThread.start();
        secondThread.start();

        firstThread.join();
        secondThread.join();

        //мы ожидаем, что каждый поток сделает свои операции и мы получим 1100
        //но в результате я получил 945, мы потеряли 155 операций, что является большой потерей
        //работает очень непредсказуемо, что то не то
        System.out.println("Result for two threads " + counter);
    }

    public static Thread createIncrementCounterThread(int incrementAmount) {
        //здесь мы выполняем операцию инкремента, она же в свою очередь атомарной не является, на самом деле тут выполняется 3 шага
        //1 шаг чтение из памяти int counter
        //2 шаг добавление 1 к нашему значению
        //3 шаг запись обновленного значения
        //Атомарные операции это присваиваные сылок и присваивание примитивов кроме double и long
        return new Thread(() -> IntStream.range(0, incrementAmount).forEach(i -> incrementCounter()));
    }

    //указали ключевое слово synchronized чтобы эта операция стала атомарной и не было проблем.
    private synchronized static void incrementCounter() {
        ++counter;
    }
}
