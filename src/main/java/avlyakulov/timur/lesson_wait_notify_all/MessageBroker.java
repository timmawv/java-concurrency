package avlyakulov.timur.lesson_wait_notify_all;

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
            //мы используем while() потому что потоки могут проснуться в любой момент, если использовать if они проснуться и будет дальше непонятная логика.
            //При while() они просыпаються и видят что логика еще не выполнилась и спят дальше
            while (messagesToBeConsumed.size() >= maxStoredMessages) {
                //Добавил пример когда мы можем задать время сколько поток ждать, тем самым мы можем предотвратить то что у нас все заснут
                //та как поток сам проснется через заданое время и будет
//                super.wait(1000);
                super.wait();
            }
            this.messagesToBeConsumed.add(message);
            System.out.printf(MESSAGE_IS_PRODUCED, message, producingTask.getName(), this.messagesToBeConsumed.size() - 1);
            super.notifyAll();
//            super.notify();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized Optional<Message> consume(MessageConsumingTask consumingTask) {
        try {
            while (this.messagesToBeConsumed.isEmpty()) {
                super.wait();
//                super.wait(1000);
            }
            Message consumedMessage = messagesToBeConsumed.poll();
            System.out.printf(MESSAGE_IS_CONSUMED, consumedMessage, consumingTask.getName(), this.messagesToBeConsumed.size() + 1);
            super.notifyAll();
//            super.notify();
            return Optional.ofNullable(consumedMessage);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
