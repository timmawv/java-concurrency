package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class Practise12 {

    private static final int balanceAccount = 200;
    private static final int numberAccounts = 6;

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
    //Пример с DeadLock
    //казалось бы пример полностью готов, но нет есть ошибка дед лока. Она не видна, если ставить sleep(2 seconds). Она видна только при безперерывной работе.
    //Я думал если поставить 2 потока, то это пофикситься, но нет! Такое не фикситься 2 потоками, а все потому что мы пытаемся брать блокировку
    //Ситуация такая, что 1 поток берет блокировку 4 клиента, 2 поток берет блокировку 6 клиента, потом 2 пытаемся взять 4 клиента, а она занята, 1 поток пытается взять 6 клиента, они становяться в бесконечную очередь.
    private static void makeTransfer(List<Account> accounts) {
        Random random = new Random();
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
    //todo here we have deadlock!
    private static void lockAccountsAndMakeTransfer(Account from, Account to, int amount) {
        ReentrantLock fromLock = from.getReentrantLock();
        ReentrantLock toLock = to.getReentrantLock();
        System.out.printf("Информация до блокировки! Работник Банка - %s. Перевод между (%s, %s)\n", Thread.currentThread().getName(), from.getId(), to.getId());
        fromLock.lock();
        System.out.println("Поток " + Thread.currentThread().getName() + " взял успешно 1 лок " + from.getId());
        toLock.lock();
        System.out.println("Поток " + Thread.currentThread().getName() + " взял успешно 2 лок " + to.getId());
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

/*
                                Ситуация №1
    Коротко что тут происходит, на примере этого лога и почему возникает DeadLock
    Поток 3 работает с клиентами 2 и 4, получается он берет лок 2 клиента успешно его захватывает и пытается взять лок 4 клиента
    Но в этоже время 2 поток по логам берет лок 4 клиента успешно и потом пытается взять лок 2 клиента, но лок уже залочен
    В то же время 1 поток пытается взять все же 4 клиента и становиться в очередь на то чтоб его взять, так само 2 поток в очереди за локом 2 клиента
    Потом 1 поток пытается взять лок 4 клиента и становиться в бесконечную очередь так само

    Поток pool-1-thread-3 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-3. Перевод между (2, 4)
    Поток pool-1-thread-2 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-2. Перевод между (1, 4)
    Поток pool-1-thread-2 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-2. Перевод между (4, 2)
    Поток pool-1-thread-1 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-1. Перевод между (4, 3)
*/



/*
                               Ситуация №2
    Тут уже отчетливо видно проблему, и что происходит на самом деле, все доходит до момента, когда потоки берут все себе в работу 1 клиента
    Но потом оказывается что 2 клиент это 1 клиент, которого уже взяли то есть 2 1 3 это тоже самое что 1 3 2 только в другом порядке
    Так и возникает дед лок и эта проблема. Потоки получается процесорное время и они могут  неограничено ждать и простаивать.

    Информация до блокировки! Работник Банка - pool-1-thread-2. Перевод между (2, 1)
    Поток pool-1-thread-3 взял успешно 2 лок
    Поток pool-1-thread-3 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-3. Перевод между (1, 3)
    Поток pool-1-thread-1 взял успешно 2 лок
    Поток pool-1-thread-1 перевод выполнил
    Информация до блокировки! Работник Банка - pool-1-thread-1. Перевод между (3, 2)
    Поток pool-1-thread-1 взял успешно 1 лок
    Поток pool-1-thread-3 взял успешно 1 лок
    Поток pool-1-thread-2 взял успешно 1 лок
*/



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
