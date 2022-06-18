package mun.concurrent.assignment.two;

import java.util.ArrayList;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;


public class Elevator {
    private int capacity;
    private int currentCount; // time
    private int currentFloor;
    private int destinationFloor;
    private ArrayList<Integer> elevator_queue = new ArrayList<Integer>();
    private String status; //Make enum


    public Elevator(int capacity, int currentCount, int currentFloor){

        this.capacity = capacity;
        this.currentCount = currentCount;
        this.currentFloor = currentFloor;
    }

    public String getStatus(){
        return status;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void addRider(Rider rider){
        // add rider.start to queue
        elevator_queue.add(rider.start_floor);
        elevator_queue.add(rider.dest_floor);
        // TODO: sort the queue

    }

    public void run(){
        // while there exists destinations on the queue
        // read the destination // READING THE QUEUE AND WRITING TO THE QUEUE SHOULD BE LOCK-PROTECTED
        // go to that floor
        // Going to floor:
//         while(getCurrentFloor() != getDestinationFloor()){
//
//             for (int i = SimulationClock.getTick(), i < ( i + 5); i++ ){
//             SimulationClock.tick();
//         }
        // clock.getTick(), if and constantly update its status and currentFloor:
        // Once 5 seconds pass, increment/decrement floor.
        // When it stops at a floor, check whether currentCount == capacity, then REJECT
        // if no more destinations on the queue, just sleep the thread maybe
        // (Do we do a signalAll from ElevatorArray? If we do signalAll, each sleeping elevator will ask "Did something get added to my queue" )
    }

}
