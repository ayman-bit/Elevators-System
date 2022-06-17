package mun.concurrent.assignment.two;

import java.util.ArrayList;

public class ElevatorArray {
    ArrayList<Elevator> elevators = new ArrayList<Elevator>();
    int numElevators;
    public ElevatorArray (int numElevators, int capacity){

        this.numElevators= numElevators;

        for(int i = 0; i < numElevators; i++) {
            elevators.add( new Elevator(capacity, 0, 1));
        }

    }


}
