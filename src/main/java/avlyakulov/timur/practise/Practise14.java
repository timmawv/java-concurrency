package avlyakulov.timur.practise;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class Practise14 {

    private static List<String> peopleNames = List.of("Alex", "Daren", "Lin", "David", "Harry", "Tymur", "Bohdan", "Dima");

    //Задача 21. Телефонная книга ⭐
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        PhoneBook phoneBook = new PhoneBook();
        PhoneReader phoneReader = new PhoneReader(phoneBook.getBook(), peopleNames, new Random(), phoneBook.getLock().readLock());
        PhoneWriter phoneWriter = new PhoneWriter(phoneBook.getBook(), peopleNames, new Random(), phoneBook.getLock().writeLock(), atomicInteger);
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        IntStream.rangeClosed(1, 5).forEach(i -> executorService.execute(phoneReader));
        IntStream.rangeClosed(1, 2).forEach(i -> executorService.execute(phoneWriter));
    }

    @AllArgsConstructor
    static class PhoneReader implements Runnable {

        private Map<String, String> phoneBook;
        private List<String> names;
        private Random random;
        private Lock readLock;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                readInformationFromBook();
            }
        }

        @SneakyThrows
        private void readInformationFromBook() {
            int indexArr = random.nextInt(0, names.size());
            String namePerson = peopleNames.get(indexArr);
            readLock.lock();
            try {
                String phonePerson = phoneBook.getOrDefault(namePerson, StringUtils.EMPTY);
                if (StringUtils.isNotBlank(phonePerson)) {
                    LoggerColor.printMessageWithColor("Person with this name %s and this phone %s exist in database\n".formatted(namePerson, phonePerson), LoggerColor.Color.GREEN);
                } else {
                    LoggerColor.printMessageWithColor("Person with this name %s doesn't exist\n".formatted(namePerson), LoggerColor.Color.RED);
                }
            } finally {
                readLock.unlock();
            }
            TimeUnit.MILLISECONDS.sleep(400);
        }
    }

    @AllArgsConstructor
    static class PhoneWriter implements Runnable {

        private Map<String, String> phoneBook;
        private List<String> names;
        private Random random;
        private Lock writeLock;
        private AtomicInteger atomicInteger;

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                writeInformationToBook();
            }
        }

        @SneakyThrows
        private void writeInformationToBook() {
            String phonePattern = "+38066895185";
            int indexArr = random.nextInt(0, names.size());
            String namePerson = peopleNames.get(indexArr);
            String phoneNumber = phonePattern.concat(String.valueOf(atomicInteger.getAndDecrement()));
            writeLock.lock();
            try {
                String key = phoneBook.putIfAbsent(namePerson, phoneNumber);
                if (Objects.nonNull(key)) {
                    LoggerColor.printMessageWithColor("Person with this name %s already exist\n".formatted(namePerson), LoggerColor.Color.RED);
                } else {
                    LoggerColor.printMessageWithColor("Person with this name %s and this phone %s not exist in database and was added\n".formatted(namePerson, phoneNumber), LoggerColor.Color.GREEN);
                }
            } finally {
                writeLock.unlock();
            }
            TimeUnit.SECONDS.sleep(2);
        }
    }

    @Getter
    static class PhoneBook {
        private Map<String, String> book = new HashMap<>();
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    }
}
