package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class Practise8_1 {

    private static final int amount = 10;
    private static final int secondsToSleep = 200;

    //Задача 15. Банкомат и ReentrantLock. Вариант с synchronized
    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount();

        Thread client1 = new Thread(createRunnable(bankAccount, amount));
        Thread client2 = new Thread(createRunnable(bankAccount, amount));
        Thread client3 = new Thread(createRunnable(bankAccount, amount));
        Thread client4 = new Thread(createRunnable(bankAccount, amount));

        client1.start();
        client2.start();
        client3.start();
        client4.start();
    }

    private static Runnable createRunnable(BankAccount bankAccount, int amountToWithdraw) {
        return () -> {
            while (!Thread.currentThread().isInterrupted())
                bankAccount.withdraw(amountToWithdraw);
        };
    }

    static class BankAccount {

        private int balance = 100;

        @SneakyThrows
        public synchronized boolean withdraw(int amountToWithdraw) {
            System.out.println(Thread.currentThread().getName() + " пытается снять деньги " + amountToWithdraw);
            if (balance >= amountToWithdraw) {
                balance -= amountToWithdraw;
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
