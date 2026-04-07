package avlyakulov.timur.lesson_wait_notify_all;

import java.util.concurrent.TimeUnit;

public class MessageConsumingTask implements Runnable{

    private final MessageBroker messageBroker;
    private final int minimalAmountMessageToConsume;
    private final String name;

    public MessageConsumingTask(MessageBroker messageBroker, int minimalAmountMessageToConsume, String name) {
        this.messageBroker = messageBroker;
        this.minimalAmountMessageToConsume = minimalAmountMessageToConsume;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMinimalAmountMessageToConsume() {
        return minimalAmountMessageToConsume;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            messageBroker.consume(this)
                    .orElseThrow(RuntimeException::new);
        }
    }
}
