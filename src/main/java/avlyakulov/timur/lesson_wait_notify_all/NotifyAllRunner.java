package avlyakulov.timur.lesson_wait_notify_all;

import java.util.Arrays;

public class NotifyAllRunner {

    //https://www.youtube.com/watch?v=Vyx5PMSkwTI&t=1s
    public static void main(String[] args) {
        //Может быть ситуация когда мы используем много потоков у которые разное условие сна и они делают разную работу, если мы используем notifiy()
        //То может бысть ситуация когда все потоки будут ждать, та как вызываем notify() мы освобождаем 1 случайный поток который спит
        //Тем самым нет гарантии какой проснется и может привести что все будут спать
        //notifyAll() решение, но оно всех будет, которые на этом потоке сидят, прям всех, слабая производительность
        int brokerMaxStoredMessage = 1;
        MessageBroker messageBroker = new MessageBroker(brokerMaxStoredMessage);

        MessageFactory messageFactory = new MessageFactory();

        Thread firstProducingThread = new Thread(new MessageProducingTask(messageBroker, messageFactory, brokerMaxStoredMessage, "PRODUCER_1"));
        Thread secondProducingThread = new Thread(new MessageProducingTask(messageBroker, messageFactory, 10, "PRODUCER_2"));

        Thread firstConsumingThread = new Thread(new MessageConsumingTask(messageBroker, 0, "CONSUMER_1"));

        startThreads(firstProducingThread, secondProducingThread);
        startThreads(firstConsumingThread);

        //мы здесь не пишем join() та как потоки независимы от вывода, они саме по себе работают и делают на фоне работе
        System.out.println("Main thread finished his work");
    }

    private static void startThreads(Thread... threads) {
        Arrays.stream(threads)
                .forEach(Thread::start);
    }
}
