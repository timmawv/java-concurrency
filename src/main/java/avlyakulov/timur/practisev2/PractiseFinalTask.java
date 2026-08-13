package avlyakulov.timur.practisev2;

import avlyakulov.timur.utils.LoggerColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class PractiseFinalTask {

    //Задача 29. Игровой сервер
    //Логика: Всего 6 игроков. Им сначало надо подключиться на сервер, для этого использовать CountDownLatch
    //После того как они подключились, им нужно распределиться по командам, на 3 команды. Команда 2 человека, играют 1 на 1 в гонку.
    //Активных матчей может быть только 2, тоесть 2 матча идут, 1 ждет. Тут использовать Semaphore.
    //Идет подготовка к матчу, тут использовать CyclicBarrier ждем, пока все игроки загрузяться на карту и только потом она стартует.
    @SneakyThrows
    public static void main(String[] args) {
        int numberPlayers = 6;
        int numberTeams = 3;
        int maxPlayerInTeam = 2;
        CountDownLatch countDownLatch = new CountDownLatch(numberPlayers);
        GameServer gameServer = new GameServer(countDownLatch, numberTeams, maxPlayerInTeam);
        ExecutorService executorService = Executors.newFixedThreadPool(numberPlayers + 1);
        IntStream.rangeClosed(1, numberPlayers).forEach(i -> executorService.execute(new Player(i, gameServer)));
        executorService.execute(gameServer);
        executorService.shutdown();
    }

    @Getter
    static class GameServer implements Runnable {

        private CountDownLatch countDownLatch;
        private List<Team> teams;
        private Lock lock;
        private int numberPlayers;
        private int maxPlayerInTeam;

        public GameServer(CountDownLatch countDownLatch, int numberTeams, int maxPlayerInTeam) {
            this.countDownLatch = countDownLatch;
            this.teams = createTeams(numberTeams, maxPlayerInTeam);
            this.lock = new ReentrantLock();
            this.numberPlayers = 0;
            this.maxPlayerInTeam = maxPlayerInTeam;
        }

        @Override
        public void run() {
            waitAllPlayers();
        }

        public void addPlayerToTeam(Player player) {
            lock.lock();
            try {
                int index = numberPlayers / maxPlayerInTeam;
                Team team = teams.get(index);
                team.addPlayerToTeam(player);
                ++numberPlayers;
            } finally {
                lock.unlock();
            }
        }

        private List<Team> createTeams(int numberTeams, int maxPlayerInTeam) {
            return IntStream.rangeClosed(1, numberTeams)
                    .mapToObj(i -> new Team(i, maxPlayerInTeam))
                    .toList();
        }

        @SneakyThrows
        private void waitAllPlayers() {
            CountDownLatch countDownLatch = getCountDownLatch();
            countDownLatch.await();
        }
    }

    @Getter
    static class Team {

        private int idTeam;
        private List<Player> players;
        private int maxPlayerInTeam;

        public Team(int idTeam, int maxPlayerInTeam) {
            this.idTeam = idTeam;
            this.players = new ArrayList<>();
            this.maxPlayerInTeam = maxPlayerInTeam;
        }

        public boolean addPlayerToTeam(Player player) {
            if (!isTeamFull()) {
                players.add(player);
                LoggerColor.printMessageWithColor("The player %s, was added to Team %s".formatted(player.getName(), this.getTeamName()), LoggerColor.Color.GREEN);
                return true;
            } else {
                LoggerColor.printMessageWithColor("The player %s, can't be added to Team %s".formatted(player.getName(), this.getTeamName()), LoggerColor.Color.RED);
                return false;
            }
        }

        public boolean isTeamFull() {
            return players.size() == maxPlayerInTeam;
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
        public void run() {
            connectToServer();
            gameServer.waitAllPlayers();
            gameServer.addPlayerToTeam(this);
        }

        public void connectToServer() {
            try {
                int timeToSleep = getRandomNumber();
                TimeUnit.SECONDS.sleep(timeToSleep);
                LoggerColor.printMessageWithColor(getName() + " was connected", LoggerColor.Color.GREEN);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
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
