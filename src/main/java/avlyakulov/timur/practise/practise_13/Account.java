package avlyakulov.timur.practise.practise_13;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.locks.Lock;

@Getter
@AllArgsConstructor
@ToString
public class Account {

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
