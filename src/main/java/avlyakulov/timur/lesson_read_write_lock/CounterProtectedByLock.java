package avlyakulov.timur.lesson_read_write_lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CounterProtectedByLock  extends AbstractCounter{

    private Lock lock = new ReentrantLock();

    @Override
    protected Lock getReadLock() {
        return lock;
    }

    @Override
    protected Lock getWriteLock() {
        return lock;
    }
}
