package avlyakulov.timur.practise;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Practise9 {

    //Задача 16. tryLock()
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

    private static Runnable createRunnable(BankAccount bankAccount) {
        return () -> {
            while (!Thread.currentThread().isInterrupted())
                bankAccount.withdraw();
        };
    }

    static class BankAccount {

        private final ReentrantLock lock = new ReentrantLock();

        @SneakyThrows
        public void withdraw() {
            boolean isLocked = lock.tryLock(2, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    System.out.println(Thread.currentThread().getName() + " получил доступ к счету и теперь выполняет операции");
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(Thread.currentThread().getName() + " выполнил операции и освободил банкомат");
                } finally {
                    lock.unlock();
                }
                TimeUnit.SECONDS.sleep(2);
            } else {
                System.out.println(Thread.currentThread().getName() + " устал стоять в очереди и ждать и он ушел после того как не получил блокировку ");
            }
        }
    }
}
