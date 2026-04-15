package avlyakulov.timur.lesson_read_write_lock;

import java.util.concurrent.locks.Lock;

public abstract class AbstractCounter {

    private long value;

    public final long getValue() {
        Lock lock = this.getReadLock();
        lock.lock();
        try {
            return this.value;
        } finally {
            lock.unlock();
        }
    }

    public final void increment() {
        Lock lock = this.getWriteLock();
        lock.lock();
        try {
            this.value++;
        } finally {
            lock.unlock();
        }
    }

    protected abstract Lock getReadLock();

    protected abstract Lock getWriteLock();
}
