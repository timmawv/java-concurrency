package avlyakulov.timur.lesson_wait_notify_advance;

import java.util.Arrays;

public class AdvanceRunner {

    public static void main(String[] args) {
        int brokerMaxStoredMessage = 15;
        MessageBroker messageBroker = new MessageBroker(brokerMaxStoredMessage);

        MessageFactory messageFactory = new MessageFactory();

        Thread firstProducingThread = new Thread(new MessageProducingTask(messageBroker, messageFactory, brokerMaxStoredMessage, "PRODUCER_1"));
        Thread secondProducingThread = new Thread(new MessageProducingTask(messageBroker, messageFactory, 10, "PRODUCER_2"));
        Thread thirdProducingThread = new Thread(new MessageProducingTask(messageBroker, messageFactory, 5, "PRODUCER_3"));

        Thread firstConsumingThread = new Thread(new MessageConsumingTask(messageBroker, 0, "CONSUMER_1"));
        Thread secondConsumingThread = new Thread(new MessageConsumingTask(messageBroker, 6, "CONSUMER_2"));
        Thread thirdConsumingThread = new Thread(new MessageConsumingTask(messageBroker, 11, "CONSUMER_3"));

        startThreads(firstProducingThread, secondProducingThread, thirdProducingThread);
        startThreads(firstConsumingThread, secondConsumingThread, thirdConsumingThread);

        //мы здесь не пишем join() та как потоки независимы от вывода, они саме по себе работают и делают на фоне работе
        System.out.println("Main thread finished his work");
    }

    private static void startThreads(Thread... threads) {
        Arrays.stream(threads)
                .forEach(Thread::start);
    }
}
