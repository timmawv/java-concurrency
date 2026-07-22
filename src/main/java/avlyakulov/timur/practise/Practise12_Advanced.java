package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Practise12_Advanced {

    private static final int balanceAccount = 200;
    private static final int numberAccounts = 6;
    private static final Random random = new Random();

    //Задача 19. Переводы между банковскими счетами
    public static void main(String[] args) {
        List<Account> accounts = createAccounts(numberAccounts);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        accounts.forEach(a -> executorService.execute(createBankWorker(accounts)));
    }

    private static Runnable createBankWorker(List<Account> accounts) {
        return () -> {
            while (!Thread.currentThread().isInterrupted()) {
                makeTransfer(accounts);
            }
        };
    }

    private static void makeTransfer(List<Account> accounts) {
        int fromId = random.nextInt(numberAccounts);
        int toId = random.nextInt(numberAccounts);
        Account from = accounts.get(fromId);
        Account to = accounts.get(toId);
        int amount = random.nextInt(1, 51); //от 1 до 51
        if (fromId != toId) {
            lockAccountsAndMakeTransfer(from, to, amount);
        }
    }

    @SneakyThrows
    private static void lockAccountsAndMakeTransfer(Account from, Account to, int amount) {
        ReentrantLock fromLock = from.getReentrantLock();
        ReentrantLock toLock = to.getReentrantLock();
        System.out.printf("Информация до блокировки! Работник Банка - %s. Перевод между (%s, %s)\n", Thread.currentThread().getName(), from.getId(), to.getId());
        boolean isFromLocked = fromLock.tryLock();
        boolean isToLocked = toLock.tryLock();
        if (isFromLocked && isToLocked) {
            try {
                //System.out.printf("Работник Банка - %s. Перевод между (%s, %s). Баланс отправителя (id = %s) %s, Баланс получателя (id = %s) %s, Сумма перевода %s. Время перевода %s\n", Thread.currentThread().getName(), from.getId(), to.getId(), from.getId(), from.getBalance(), to.getId(), to.getBalance(), amount, LocalDateTime.now().toLocalTime());
                if (canAccountSendMoney(amount, from.getBalance())) {
                    removeMoneyFromAccount(from, amount);
                    addMoneyToAccount(to, amount);
                    //System.out.printf("Работник Банка - %s. Перевод между (%s, %s). После перевода. Баланс отправителя (id = %s) %s, Баланс получателя (id = %s) %s\n", Thread.currentThread().getName(), from.getId(), to.getId(), from.getId(), from.getBalance(), to.getId(), to.getBalance());
                } else {
                    LoggerColor.printMessageWithColor("У отправителя недостаточно денег " + from.getId(), LoggerColor.Color.RED);
                }
            } finally {
                LoggerColor.printMessageWithColor("Поток " + Thread.currentThread().getName() + " перевод выполнил", LoggerColor.Color.GREEN);
                fromLock.unlock();
                toLock.unlock();
            }
        } else {
            try {
                LoggerColor.printMessageWithColor("Потоку " + Thread.currentThread().getName() + " не удалось взять блокировку обоих объектов, перевод не выполнен", LoggerColor.Color.RED);
            } finally {
                if (isFromLocked)
                    fromLock.unlock();
                if (isToLocked)
                    toLock.unlock();
            }
        }
        TimeUnit.MILLISECONDS.sleep(200);
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
