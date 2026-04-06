package avlyakulov.timur.lesson_wait_notify_advance;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class MessageBroker {

    private static final String MESSAGE_IS_PRODUCED = "%s is produced by %s. Amount of messages before producing: %d\n";
    private final String MESSAGE_IS_CONSUMED = "%s is consumed by %s. Amount of messages before consuming: %d\n";

    private final Queue<Message> messagesToBeConsumed;

    private final int maxStoredMessages;

    public MessageBroker(int maxStoredMessages) {
        this.messagesToBeConsumed = new ArrayDeque<>(maxStoredMessages);
        this.maxStoredMessages = maxStoredMessages;
    }

    public synchronized void produce(Message message, MessageProducingTask producingTask) {
        try {
            while (!isShouldProduce(producingTask)) {
                super.wait();
            }
            this.messagesToBeConsumed.add(message);
            System.out.printf(MESSAGE_IS_PRODUCED, message, producingTask.getName(), this.messagesToBeConsumed.size() - 1);
            super.notify();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized Optional<Message> consume(MessageConsumingTask consumingTask) {
        try {
            while (!isShouldConsume(consumingTask)) {
                super.wait();
            }
            Message consumedMessage = messagesToBeConsumed.poll();
            System.out.printf(MESSAGE_IS_CONSUMED, consumedMessage, consumingTask.getName(), this.messagesToBeConsumed.size() + 1);
            super.notify();
            return Optional.ofNullable(consumedMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }

    private boolean isShouldConsume(MessageConsumingTask consumingTask) {
        return !this.messagesToBeConsumed.isEmpty() && this.messagesToBeConsumed.size() >= consumingTask.getMinimalAmountMessageToConsume();
    }

    private boolean isShouldProduce(MessageProducingTask producingTask) {
        return this.messagesToBeConsumed.size() < maxStoredMessages
                && messagesToBeConsumed.size() < producingTask.getMaximalAmountMessagesToProduce();
    }
}
