package avlyakulov.timur.lesson_volatile;

import java.util.concurrent.TimeUnit;

public class PrintingTaskRunner {

    public static void main(String[] args) throws InterruptedException {
        //у каждого потока есть свой кеш где хранятся объекты, и если внешний поток изменил переменную которую использует наш поток
        //то есть вероятность того, что наш поток будет и дальше использовать закешивированую переменную в нашем потоке
        //для этого есть волатайл который как бы говорит что всегда читает переменную из стека допустим
        PrintingTask printingTask = new PrintingTask();
        Thread thread = new Thread(printingTask);

        thread.start();

        TimeUnit.SECONDS.sleep(3);

        printingTask.setShouldPrint(false);
        System.out.println("Printing task should be stopped");
    }
}
