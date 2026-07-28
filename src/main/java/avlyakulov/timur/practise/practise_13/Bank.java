package avlyakulov.timur.practise.practise_13;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class Bank {

    //todo make in future good refactoring!

    private List<BankOperation> bankOperations = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private int counterOperationId = 0;

    public void putTransfer(BankOperation bankOperation) {
        bankOperations.add(bankOperation);
    }

    public void printTransfers() {
        System.out.printf("Информация от работинка банка %s. Выполнено такое количество переводов %d\n", Thread.currentThread().getName(), counterOperationId);
    }

    public int getAndIncrementCounter() {
        return ++counterOperationId;
    }
}
