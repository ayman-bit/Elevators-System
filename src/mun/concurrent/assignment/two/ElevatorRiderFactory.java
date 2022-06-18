package mun.concurrent.assignment.two;
import java.util.concurrent.ThreadLocalRandom;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;

public class ElevatorRiderFactory {

    private ElevatorArray elevatorArray;
    public ElevatorRiderFactory(ElevatorArray elevatorArray) {
        this.elevatorArray = elevatorArray;

    }

    //int nextRiderTime = ThreadLocalRandom.current().nextInt(20, 120 + 1);

    public Rider generateRiderFloor(int start_floor) {
        int end_floor= generateFloor();

        while (end_floor == start_floor) {
            end_floor = generateFloor();
        }
        Rider rider = new Rider(start_floor, end_floor );
        System.out.println("Starting floor: " + rider.start_floor);
        System.out.println("Dest floor: " + rider.dest_floor);
        return rider;
    }

    public int generateFloor(){
        return ThreadLocalRandom.current().nextInt(1, 5 + 1);
    }

    public int setNextRiderRider(int start_floor, int nextRiderTime){
        if (nextRiderTime == SimulationClock.getTick()) {
            Rider rider = generateRiderFloor(start_floor);
            elevatorArray.scheduleRider(rider);
            nextRiderTime = SimulationClock.getTick() + ThreadLocalRandom.current().nextInt(20, 120 + 1);
        }
        return nextRiderTime;
    }
}
