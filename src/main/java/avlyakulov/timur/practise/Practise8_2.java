package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Practise8_2 {

    private static final int amount = 10;
    private static final int secondsToSleep = 200;

    //Задача 15. Банкомат и ReentrantLock. Вариант с ReentrantLock
    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount();

        Thread client1 = new Thread(createRunnable(bankAccount));
        Thread client2 = new Thread(createRunnable(bankAccount));
        Thread client3 = new Thread(createRunnable(bankAccount));
        Thread client4 = new Thread(createRunnable(bankAccount));

        client1.start();
        client2.start();
        client3.start();
        client4.start();
    }

    private static Runnable createRunnable(Practise8_2.BankAccount bankAccount) {
        return () -> {
            while (!Thread.currentThread().isInterrupted())
                bankAccount.withdraw(amount);
        };
    }

    static class BankAccount {

        private int balance = 100;
        private final ReentrantLock lock = new ReentrantLock();

        @SneakyThrows
        public boolean withdraw(int amountToWithdraw) {
            System.out.println(Thread.currentThread().getName() + " пытается снять деньги " + amountToWithdraw);
            if (balance >= amountToWithdraw) {
                lock.lock();
                try {
                    balance -= amountToWithdraw; // проблема этого действия, что это не одно действия а 3, поэтому без блокировок не обойтись
                } finally {
                    lock.unlock();
                }
                System.out.println(Thread.currentThread().getName() + " снял успешно деньги остаток " + balance);
                TimeUnit.MILLISECONDS.sleep(secondsToSleep);
                return true;
            } else {
                System.out.println("Недостаточно средств");
                TimeUnit.MILLISECONDS.sleep(secondsToSleep);
                return false;
            }
        }
    }
}
