package avlyakulov.timur.practise;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
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
