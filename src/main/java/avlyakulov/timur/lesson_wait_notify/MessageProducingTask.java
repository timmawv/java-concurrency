package avlyakulov.timur.lesson_wait_notify;

import java.util.concurrent.TimeUnit;

public class MessageProducingTask implements Runnable {
    private static final String MESSAGE_IS_PRODUCED = "%s is produced\n";

    private final MessageBroker messageBroker;
    private final MessageFactory messageFactory;

    public MessageProducingTask(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
        this.messageFactory = new MessageFactory();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message producedMessage = messageFactory.create();
                TimeUnit.SECONDS.sleep(1);
                messageBroker.produce(producedMessage);
                System.out.printf(MESSAGE_IS_PRODUCED, producedMessage);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private static final class MessageFactory {

        private static final int INITIAL_NEXT_MESSAGE_INDEX = 1;

        private static final String TEMPLATE_CREATED_MESSAGE_DATA = "Message#%d";

        private int nextMessageIndex;

        public MessageFactory() {
            this.nextMessageIndex = INITIAL_NEXT_MESSAGE_INDEX;
        }

        public Message create() {
            return new Message(String.format(TEMPLATE_CREATED_MESSAGE_DATA, this.nextMessageIndex++));
        }
    }
}
