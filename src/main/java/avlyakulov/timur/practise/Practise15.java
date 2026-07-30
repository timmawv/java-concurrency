package avlyakulov.timur.practise;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Practise15 {

    //Semaphore
    public static void main(String[] args) {

    }

    @Getter
    static class Parking {
        private List<Car> carsOnParking = new ArrayList<>();


    }

    static class Car implements Runnable {

        private Parking parking;

        @Override
        public void run() {

        }
    }
}
