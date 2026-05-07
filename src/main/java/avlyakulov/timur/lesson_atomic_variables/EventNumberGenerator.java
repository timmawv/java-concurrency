package avlyakulov.timur.lesson_atomic_variables;

import java.util.concurrent.atomic.AtomicInteger;

public class EventNumberGenerator {
    private static final int GENERATION_DELTA = 2;

    //оказывается AtomicInteger исплоьзует неблокирующий подход, они использует CAS
    //тут также важно понимать как работает
    private final AtomicInteger value = new AtomicInteger();


    public int generate() {
        //здесь важно это неблокирующий метод мы используем 1 getAndAdd
        //в этом методе нельзя 2 раза написать getAndAdd та как может быть то что после 1 инкремента поток заснет и тогда логика будет не та.
        return value.getAndAdd(GENERATION_DELTA);
    }

    public int getValue() {
        return value.intValue();
    }
}
