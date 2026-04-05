package avlyakulov.timur.lesson_wait_notify;

import java.util.ArrayDeque;
import java.util.Queue;

public class MessageBroker {

    private final Queue<Message> messagesToBeConsumed;

    private final int maxStoredMessages;

    public MessageBroker(int maxStoredMessages) {
        this.messagesToBeConsumed = new ArrayDeque<>(maxStoredMessages);
        this.maxStoredMessages = maxStoredMessages;
    }

    public synchronized void produce(Message message) {
        try {
            while (messagesToBeConsumed.size() >= maxStoredMessages) {
                super.wait();
            }
            this.messagesToBeConsumed.add(message);
            super.notify();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized Message consume() {
        try {
            while (messagesToBeConsumed.isEmpty()) {
                super.wait();
            }
            Message consumedMessage = messagesToBeConsumed.poll();
            super.notify();
            return consumedMessage;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
