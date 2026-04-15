package avlyakulov.timur.lesson_read_write_lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CounterProtectedByReadWriteLock extends AbstractCounter{

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = this.readWriteLock.readLock();
    private final Lock writeLock = this.readWriteLock.writeLock();

    @Override
    protected Lock getReadLock() {
        return readLock;
    }

    @Override
    protected Lock getWriteLock() {
        return writeLock;
    }
}
