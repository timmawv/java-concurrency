package avlyakulov.timur.lesson_read_write_lock;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public abstract class AbstractCounter {

    private long value;

    public final OptionalLong getValue() {
        Lock lock = this.getReadLock();
        lock.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
            return OptionalLong.of(value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return OptionalLong.empty();
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
