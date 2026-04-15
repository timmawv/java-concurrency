package avlyakulov.timur.lesson_lock_condition;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBuffer<T> {
    private T[] elements;
    private Lock lock;
    private int size;
    //по сути используется для выставление wait() но для Lock та как там нет блока synchronized
    private Condition condition;

    @SuppressWarnings("unchecked")
    public BoundedBuffer(int capacity) {
        this.elements = (T[]) new Object[capacity];
        this.lock = new ReentrantLock();
        this.condition = this.lock.newCondition();
    }

    public boolean isFull() {
        this.lock.lock();
        try {
            return this.size == this.elements.length;
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isEmpty() {
        this.lock.lock();
        try {
            return this.size == 0;
        } finally {
            this.lock.unlock();
        }
    }

    public void put(T element) {
        this.lock.lock();
        try {
            while (this.isFull()) {
                this.condition.await();
            }
            this.elements[this.size] = element;
            this.size++;
            System.out.printf("%s was put in buffer. Result buffer: %s\n", element, this);
            this.condition.signalAll();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            this.lock.unlock();
        }
    }

    public T take() {
        this.lock.lock();
        try {
            while (this.isEmpty()) {
                this.condition.await();
            }
            T result = this.elements[this.size - 1];
            this.elements[this.size - 1] = null;
            this.size--;
            System.out.printf("%s was taken from buffer. Result buffer: %s\n", result, this);
            this.condition.signalAll();
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public String toString() {
        this.lock.lock();
        try {
            return "BoundedBuffer{" +
                    "elements = " + Arrays.toString(elements) +
                    ", size = " + size +
                    '}';
        } finally {
            this.lock.unlock();
        }
    }
}