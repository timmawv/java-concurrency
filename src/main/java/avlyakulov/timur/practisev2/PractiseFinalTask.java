package avlyakulov.timur.practisev2;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PractiseFinalTask {

    //Задача 29. Игровой сервер
    //Логика: Всего 6 игроков. Им сначало надо подключиться на сервер, для этого использовать CountDownLatch
    //После того как они подключились, им нужно распределиться по командам, на 3 матча. Команда 2 человека, играют 1 на 1 в гонку.
    //Активных матчей может быть только 2, тоесть 2 матча идут, 1 ждет. Тут использовать Semaphore.
    //Идет подготовка к матчу, тут использовать CyclicBarrier ждем, пока все игроки загрузяться на карту и только потом она стартует.
    public static void main(String[] args) {

    }

    static class GameServer {

    }

    @Getter
    @AllArgsConstructor
    static class Player {

        private int id;

    }
}
