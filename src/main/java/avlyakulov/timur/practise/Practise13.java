package avlyakulov.timur.practise;

import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final int amountToTransfer = 30;

    // Финальная задача. Мини-Банк
    @SneakyThrows
    public static void main(String[] args) {
        Bank bank = new Bank();
        List<Account> accounts = createAccounts(numberOfClient);
        Queue<OperationType> queue = new ArrayDeque<>();
        createThreadDemon(queue);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        IntStream.rangeClosed(1, 3).forEach(i -> executorService.execute(createBankWorker(queue, bank, accounts)));
    }

    public static List<Account> createAccounts(int numberOfAccount) {
        return IntStream.rangeClosed(1, numberOfAccount).mapToObj(id -> new Account(id, balanceClient, new ReentrantLock())).toList();
    }

    public static Runnable createBankWorker(Queue<OperationType> queue, Bank bank, List<Account> accounts) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                makeBankWork(queue, bank, accounts);
            }
        };
    }

    @SneakyThrows
    public static void makeBankWork(Queue<OperationType> queue, Bank bank, List<Account> accounts) {
        OperationType operationFromQueue = getOperationFromQueue(queue);
        doBankWork(operationFromQueue, bank, accounts);
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

    public static void doBankWork(OperationType operationFromQueue, Bank bank, List<Account> accounts) {
        switch (operationFromQueue) {
            case TRANSFER -> makeTransfer(bank, accounts);
            case STATISTIC -> printOperations(bank);
        }
    }

    private static void printOperations(Bank bank) {
        Lock lock = bank.getLock();
        lock.lock();
        try {
            bank.printTransfers();
        } finally {
            lock.unlock();
        }
    }

    private static void makeTransfer(Bank bank, List<Account> accounts) {
        int fromId = random.nextInt(1, numberOfClient);
        int toId = random.nextInt(1, numberOfClient);
        if (fromId != toId) {
            Account from = accounts.get(fromId);
            Account to = accounts.get(toId);
            lockForTransfer(from, to);
            try {
                completeTransfer(from, to);
                putToBankOperation(bank, from, to);
            } finally {
                from.getLock().unlock();
                to.getLock().unlock();
            }
        }
    }

    private static void lockForTransfer(Account from, Account to) {
        int fromId = from.getId();
        int toId = to.getId();
        if (fromId < toId) {
            from.getLock().lock();
            to.getLock().lock();
        } else {
            to.getLock().lock();
            from.getLock().lock();
        }
    }

    private static void completeTransfer(Account from, Account to) {
        from.withdraw(amountToTransfer);
        to.deposit(amountToTransfer);
    }

    private static void putToBankOperation(Bank bank, Account from, Account to) {
        bank.getLock().lock();
        try {
            BankOperation bankOperation = BankOperation.builder()
                    .operationId(bank.getAndIncrementCounter())
                    .fromAccountId(from.getId())
                    .toAccountId(to.getId())
                    .amount(amountToTransfer)
                    .createdAt(LocalDateTime.now())
                    .build();
            bank.putTransfer(bankOperation);
        } finally {
            bank.getLock().unlock();
        }
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
            balance += amount;
        }

        public void withdraw(int amount) {
            balance -= amount;
        }

        //todo add this method also
        public boolean canDeposit(int amount) {
            return balance - amount >= 0;
        }
    }

    @Getter
    @Builder
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
        private int counterOperationId = 0;

        public void putTransfer(BankOperation bankOperation) {
            bankOperations.add(bankOperation);
        }

        public void printTransfers() {
            System.out.println(bankOperations);
        }

        public int getAndIncrementCounter() {
            return ++counterOperationId;
        }
    }
}
