package avlyakulov.timur.lesson_wait_notify;

import java.util.concurrent.TimeUnit;

public class MessageConsumingTask implements Runnable{

    private final MessageBroker messageBroker;
    private final String MESSAGE_IS_CONSUMED = "%s is consumed\n";

    public MessageConsumingTask(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                TimeUnit.SECONDS.sleep(1);
                Message consumedMessage = messageBroker.consume();
                System.out.printf(MESSAGE_IS_CONSUMED, consumedMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
