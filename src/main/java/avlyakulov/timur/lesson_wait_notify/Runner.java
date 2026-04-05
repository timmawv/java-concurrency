package avlyakulov.timur.lesson_wait_notify;

public class Runner {

    public static void main(String[] args) {
        int brokerMaxStoredMessage = 5;
        MessageBroker messageBroker = new MessageBroker(brokerMaxStoredMessage);

        Thread producingThread = new Thread(new MessageProducingTask(messageBroker));
        Thread consumingThread = new Thread(new MessageConsumingTask(messageBroker));

        producingThread.start();
        consumingThread.start();
    }
}
