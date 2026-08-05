package avlyakulov.timur.practisev2;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.stream.IntStream;

public class PractiseFinalTask {

    //Задача 29. Игровой сервер
    //Логика: Всего 6 игроков. Им сначало надо подключиться на сервер, для этого использовать CountDownLatch
    //После того как они подключились, им нужно распределиться по командам, на 3 матча. Команда 2 человека, играют 1 на 1 в гонку.
    //Активных матчей может быть только 2, тоесть 2 матча идут, 1 ждет. Тут использовать Semaphore.
    //Идет подготовка к матчу, тут использовать CyclicBarrier ждем, пока все игроки загрузяться на карту и только потом она стартует.
    public static void main(String[] args) {
        int numberPlayers = 6;
        GameServer gameServer = new GameServer(new CountDownLatch(numberPlayers));
        ExecutorService executorService = Executors.newFixedThreadPool(numberPlayers);
        IntStream.rangeClosed(1, 6).forEach(i -> executorService.execute(new Player(i, gameServer)));
        executorService.shutdown();
    }

    @Getter
    static class GameServer {

        private CountDownLatch countDownLatch;
        private Lock lock;

        public GameServer(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

    }

    @Getter
    @AllArgsConstructor
    static class Player implements Runnable {

        private int id;
        private GameServer gameServer;

        @Override
        @SneakyThrows
        public void run() {
            connectToServer();
            CountDownLatch countDownLatch = getCountDownLatch();
            countDownLatch.await();
        }

        public void connectToServer() {
            CountDownLatch countDownLatch = getCountDownLatch();
            try {
                int timeToSleep = getRandomNumber();
                TimeUnit.SECONDS.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                LoggerColor.printMessageWithColor(getName() + " was connected", LoggerColor.Color.GREEN);
                countDownLatch.countDown();
            }
        }

        public String getName() {
            return "Player " + id;
        }

        public CountDownLatch getCountDownLatch() {
            return gameServer.getCountDownLatch();
        }
    }

    private static int getRandomNumber() {
        return ThreadLocalRandom.current().nextInt(1, 5);
    }
}
