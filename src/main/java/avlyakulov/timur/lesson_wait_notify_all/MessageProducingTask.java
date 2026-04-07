package avlyakulov.timur.lesson_wait_notify_all;

import java.util.concurrent.TimeUnit;

public class MessageProducingTask implements Runnable {

    private final MessageBroker messageBroker;
    private final MessageFactory messageFactory;

    private final int maximalAmountMessagesToProduce;
    private final String name;

    public MessageProducingTask(MessageBroker messageBroker, MessageFactory messageFactory, int maximalAmountMessagesToProduce, String name) {
        this.messageBroker = messageBroker;
        this.messageFactory = messageFactory;
        this.maximalAmountMessagesToProduce = maximalAmountMessagesToProduce;
        this.name = name;
    }

    public int getMaximalAmountMessagesToProduce() {
        return maximalAmountMessagesToProduce;
    }

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Message producedMessage = messageFactory.create();
            messageBroker.produce(producedMessage, this);
        }
    }
}
