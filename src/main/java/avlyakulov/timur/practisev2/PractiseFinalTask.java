package avlyakulov.timur.practisev2;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

public class PractiseFinalTask {

    //Задача 29. Игровой сервер
    //Логика: Всего 6 игроков. Им сначало надо подключиться на сервер, для этого использовать CountDownLatch
    //После того как они подключились, им нужно распределиться по командам, на 3 матча. Команда 2 человека, играют 1 на 1 в гонку.
    //Активных матчей может быть только 2, тоесть 2 матча идут, 1 ждет. Тут использовать Semaphore.
    //Идет подготовка к матчу, тут использовать CyclicBarrier ждем, пока все игроки загрузяться на карту и только потом она стартует.
    @SneakyThrows
    public static void main(String[] args) {
        int numberPlayers = 6;
        int numberTeams = 3;
        int maxPlayerInTeam = 2;
        CountDownLatch countDownLatch = new CountDownLatch(numberPlayers);
        GameServer gameServer = new GameServer(countDownLatch, numberTeams, maxPlayerInTeam);
        ExecutorService executorService = Executors.newFixedThreadPool(numberPlayers);
        IntStream.rangeClosed(1, 6).forEach(i -> executorService.execute(new Player(i, gameServer)));
        executorService.shutdown();
    }

    @Getter
    static class GameServer {

        private CountDownLatch countDownLatch;
        private List<Team> teams;
        private AtomicInteger availableTeam = new AtomicInteger(1);

        public GameServer(CountDownLatch countDownLatch, int numberTeams, int maxPlayerInTeam) {
            this.countDownLatch = countDownLatch;
            this.teams = createTeams(numberTeams, maxPlayerInTeam, new ReentrantReadWriteLock());
        }

        public void addPlayerToTeam(Player player) {
            Team team = teams.get(availableTeam.get() - 1);
            boolean playerAdded = team.addPlayerToTeam(player);
            if (!playerAdded) {
                availableTeam.getAndIncrement();
                addPlayerToTeam(player);
            }
        }

        private List<Team> createTeams(int numberTeams, int maxPlayerInTeam, ReadWriteLock readWriteLock) {
            return IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> new Team(i, maxPlayerInTeam, readWriteLock.readLock(), readWriteLock.writeLock()))
                    .toList();
        }
    }

    @Getter
    static class Team {

        private int idTeam;
        private List<Player> players;
        private int maxPlayerInTeam;
        private Lock readLock;
        private Lock writeLock;

        public Team(int idTeam, int maxPlayerInTeam, Lock readLock, Lock writeLock) {
            this.idTeam = idTeam;
            players = new ArrayList<>();
            this.maxPlayerInTeam = maxPlayerInTeam;
            this.readLock = readLock;
            this.writeLock = writeLock;
        }

        public boolean addPlayerToTeam(Player player) {
            writeLock.lock();
            try {
                if (canAddToTeam()) {
                    players.add(player);
                    LoggerColor.printMessageWithColor("The player %s, was added to Team %s".formatted(player.getName(), this.getTeamName()), LoggerColor.Color.GREEN);
                    return true;
                } else {
                    LoggerColor.printMessageWithColor("The player %s, can't be added to Team %s".formatted(player.getName(), this.getTeamName()), LoggerColor.Color.RED);
                    return false;
                }
            } finally {
                writeLock.unlock();
            }
        }

        public boolean canAddToTeam() {
            return players.size() < maxPlayerInTeam;
        }

        public String getTeamName() {
            return "Team " + idTeam;
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
            gameServer.addPlayerToTeam(this);
        }

        public void connectToServer() {
            try {
                int timeToSleep = getRandomNumber();
                TimeUnit.SECONDS.sleep(timeToSleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                LoggerColor.printMessageWithColor(getName() + " was connected", LoggerColor.Color.GREEN);
                CountDownLatch countDownLatch = getCountDownLatch();
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
