package avlyakulov.timur.practise;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Practise12 {

    private static final int balanceAccount = 200;
    private static final int numberAccounts = 6;

    //Задача 19. Переводы между банковскими счетами
    public static void main(String[] args) {
        List<Account> accounts = createAccounts(numberAccounts);
    }

    private static Runnable createBankWorker(List<Account> accounts) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                makeTransfer(accounts);
            }
        };
    }

    private static void makeTransfer(List<Account> accounts) {
        Random random = new Random();
        int firstAccount = random.nextInt(numberAccounts + 1);
        int secondAccount = random.nextInt(numberAccounts + 1);
        int amount = random.nextInt(1, 51); //от 1 до 51
        if (firstAccount != secondAccount) {
            lockAccountsAndMakeTransfer(firstAccount, secondAccount, accounts, amount);
        }
    }

    //todo refactor it, we can in a previous step get number of account
    @SneakyThrows
    private static void lockAccountsAndMakeTransfer(int firstAccount, int secondAccount, List<Account> accounts, int amount) {
        Account from = accounts.get(firstAccount);
        Account to = accounts.get(secondAccount);
        ReentrantLock fromLock = from.getReentrantLock();
        ReentrantLock toLock = to.getReentrantLock();
        fromLock.lock();
        toLock.lock();
        try {
            if (canAccountSendMoney(amount, from.getBalance())) {
                removeMoneyFromAccount(from, amount);
                addMoneyToAccount(to, amount);
                TimeUnit.SECONDS.sleep(2);
            }
        } finally {
            fromLock.unlock();
            toLock.unlock();
        }
    }

    private static boolean canAccountSendMoney(int amount, int balanceAccount) {
        return balanceAccount - amount > 0;
    }

    private static void removeMoneyFromAccount(Account from, int amount) {
        int balance = from.getBalance();
        int newBalance = balance - amount;
        from.setBalance(newBalance);
    }

    private static void addMoneyToAccount(Account to, int amount) {
        int balance = to.getBalance();
        int newBalance = balance + amount;
        to.setBalance(newBalance);
    }

    private static List<Account> createAccounts(int numberOfAccount) {
        return IntStream.rangeClosed(1, numberOfAccount).mapToObj(id -> new Account(id, balanceAccount)).toList();
    }

    @Getter
    @Setter
    static class Account {

        private final int id;
        private int balance;

        private final ReentrantLock reentrantLock = new ReentrantLock();

        public Account(int id, int balance) {
            this.id = id;
            this.balance = balance;
        }
    }
}
