package avlyakulov.timur.lesson_wait_notify_advance;

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
        try {
            while (!Thread.currentThread().isInterrupted()) {
                TimeUnit.SECONDS.sleep(3);
                messageBroker.consume(this)
                        .orElseThrow(RuntimeException::new);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
