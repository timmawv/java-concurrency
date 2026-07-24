package avlyakulov.timur.practise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Practise13 {

    private static final Lock queueLock = new ReentrantLock();
    private static final Condition condition = queueLock.newCondition();

    private static final Random random = new Random();
    private static final int numberOfClient = 6;
    private static final int balanceClient = 300;

    // Финальная задача. Мини-Банк
    @SneakyThrows
    public static void main(String[] args) {
        Bank bank = new Bank();
        List<Account> accounts = createAccounts(numberOfClient);
        Queue<OperationType> queue = new ArrayDeque<>();
        createThreadDemon(queue);
    }

    public static List<Account> createAccounts(int numberOfAccount) {
        return IntStream.rangeClosed(1, numberOfAccount).mapToObj(id -> new Account(id, balanceClient, new ReentrantLock())).toList();
    }

    public static Runnable createBankWorker(Queue<OperationType> queue, Bank bank) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                makeBankWork(queue, bank);
            }
        };
    }

    @SneakyThrows
    public static void makeBankWork(Queue<OperationType> queue, Bank bank) {
        OperationType operationFromQueue = getOperationFromQueue(queue);
    }

    @SneakyThrows
    private static OperationType getOperationFromQueue(Queue<OperationType> queue) {
        queueLock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await();
            }
            return queue.poll();
        } finally {
            queueLock.unlock();
        }
    }

    public static void doBankWork() {

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
        OperationType operationType;
        queueLock.lock();
        try {
            operationType = putTypeTask(queue);
            condition.signal();
        } finally {
            queueLock.unlock();
        }
        takeBreak(operationType);
    }

    @SneakyThrows
    public static void takeBreak(OperationType operationType) {
        switch (operationType) {
            case TRANSFER -> TimeUnit.MILLISECONDS.sleep(400);
            case STATISTIC -> TimeUnit.SECONDS.sleep(3);
        }
    }

    public static OperationType putTypeTask(Queue<OperationType> queue) {
        int taskNumber = random.nextInt(1, 3);
        switch (taskNumber) {
            case 1 -> queue.add(OperationType.TRANSFER);
            case 2 -> queue.add(OperationType.STATISTIC);
            default -> throw new UnsupportedOperationException();
        }
        return taskNumber == 1 ? OperationType.TRANSFER : OperationType.STATISTIC;
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
            balance += amount;
        }

        public boolean canDeposit(int amount) {
            return balance - amount >= 0;
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
        private final Lock lock = new ReentrantLock();

        public void putTransfer(BankOperation bankOperation) {
            bankOperations.add(bankOperation);
        }

        public void printTransfers() {
            System.out.println(bankOperations);
        }
    }
}
