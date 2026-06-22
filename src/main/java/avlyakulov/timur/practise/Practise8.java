package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public class Practise8 {

    private static final int amount = 10;

    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount();
        Thread thread1 = new Thread(createRunnable(bankAccount, amount));
        Thread thread2 = new Thread(createRunnable(bankAccount, amount));
        Thread thread3 = new Thread(createRunnable(bankAccount, amount));
        Thread thread4 = new Thread(createRunnable(bankAccount, amount));

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    private static Runnable createRunnable(BankAccount bankAccount, int amountToWithdraw) {
        return () -> {
            while (!Thread.currentThread().isInterrupted())
                bankAccount.withdraw(amountToWithdraw);
        };
    }

    static class BankAccount {

        private int balance = 1000;

        @SneakyThrows
        public synchronized boolean withdraw(int amountToWithdraw) {
            System.out.println(Thread.currentThread().getName() + " пытается снять деньги " + amountToWithdraw);
            if (balance >= amountToWithdraw) {
                TimeUnit.MILLISECONDS.sleep(100);
                balance -= amountToWithdraw;
                System.out.println(Thread.currentThread().getName() + " снял успешно деньги остаток " + balance);
                return true;
            } else {
                System.out.println("Недостаточно средств");
                return false;
            }
        }
    }
}
