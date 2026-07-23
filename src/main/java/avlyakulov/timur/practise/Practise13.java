package avlyakulov.timur.practise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Practise13 {

    private static final Lock queueLock = new ReentrantLock();
    private static final Random random = new Random();
    private static final int numberOfClient = 6;

    // Финальная задача. Мини-Банк
    @SneakyThrows
    public static void main(String[] args) {
        Queue<OperationType> queue = new ArrayDeque<>();
        createThreadDemon(queue);
    }

    public static void createThreadDemon(Queue<OperationType> queue) {
        Thread thread = new Thread(() -> createRunnable(queue));
        thread.setDaemon(true);
        thread.start();
    }

    public static void createRunnable(Queue<OperationType> queue) {
        while (!Thread.currentThread().isInterrupted()) {
            putTask(queue);
        }
    }

    public static void putTask(Queue<OperationType> queue) {
        queueLock.lock();
        try {
            putTypeTask(queue);
        } finally {
            queueLock.unlock();
        }
    }

    public static void putTypeTask(Queue<OperationType> queue) {
        int taskNumber = random.nextInt(1, 3);
        switch (taskNumber) {
            case 1 -> putTransferInQueue(queue);
            case 2 -> putPrintStatisticInQueue(queue);
            default -> throw new UnsupportedOperationException();
        }
    }

    @SneakyThrows
    public static void putTransferInQueue(Queue<OperationType> queue) {
        queue.add(OperationType.TRANSFER);
        TimeUnit.MILLISECONDS.sleep(400);
    }

    @SneakyThrows
    public static void putPrintStatisticInQueue(Queue<OperationType> queue) {
        queue.add(OperationType.STATISTIC);
        TimeUnit.SECONDS.sleep(5);
    }


    @Getter
    @AllArgsConstructor
    @ToString
    static class Account {

        private int id;
        private int balance;
        private Lock lock;

        public void deposit(int amount) {

        }

        public void withdraw(int amount) {

        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    static class BankOperation {
        private int operationId;
        private int fromAccountId;
        private int toAccountId;
        private int amount;
        private LocalDateTime createdAt;
    }

    enum OperationType {
        TRANSFER, STATISTIC
    }

    @Getter
    static class Bank {

        private List<BankOperation> bankOperations = new ArrayList<>();

        public void transfer(Account from, Account to) {

        }

        public void printTransfers() {
            System.out.println(bankOperations);
        }
    }
}
