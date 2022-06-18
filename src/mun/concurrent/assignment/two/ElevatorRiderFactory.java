package mun.concurrent.assignment.two;
import java.util.concurrent.ThreadLocalRandom;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;

public class ElevatorRiderFactory {

    public ElevatorRiderFactory() {}

    int nextRider = ThreadLocalRandom.current().nextInt(20, 120 + 1);

    public Rider generateRider() {
        int start_floor = generateFloor();
        int end_floor= generateFloor();

        while (end_floor == start_floor) {
            end_floor = generateFloor();
        }
        Rider rider = new Rider(start_floor, end_floor );
        System.out.println(rider.start_floor);
        System.out.println(rider.dest_floor);
        return rider;
    }

    public int generateFloor(){
        return ThreadLocalRandom.current().nextInt(1, 5 + 1);
    }

    public void setNextRiderRider(){
        if (nextRider == SimulationClock.getTick()) {
            generateRider();
            nextRider = SimulationClock.getTick() + ThreadLocalRandom.current().nextInt(20, 120 + 1);

        }
    }
}
