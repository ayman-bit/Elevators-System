package mun.concurrent.assignment.two;

import java.util.ArrayList;
import java.util.Iterator;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;


public class Elevator implements Runnable {
    private int capacity; // max capacity of elevator
    private int currentCount; // how many members in the elevator
    private int currentFloor;
    private int destinationFloor;
    private Thread thread; // Elevator's thread to handle asynchronous movement
    private ArrayList<Integer> elevator_queue = new ArrayList<Integer>();
    private ArrayList<Integer> dropOff_queue = new ArrayList<Integer>();
    private String status; //Make enum
    private boolean elevatorRunning = false;
    private boolean moving = false;
    private static final int PASSENDER_LEAVING_TIME = 15;
    private static final int TRAVEL_TIME = 5;


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
        dropOff_queue.add(rider.dest_floor);
    }

    public void removeRider(Rider rider){
        // drops off the rider
        if (dropOff_queue.contains(elevator_queue.get(0))) {
            // what if 2 people want to get off here?
            if (dropOff_queue.contains(elevator_queue.get(0))==dropOff_queue.contains(elevator_queue.get(1))){
                currentCount--;
                currentCount--;
                dropOff_queue.remove(dropOff_queue.indexOf(elevator_queue.get(0)));
                dropOff_queue.remove(dropOff_queue.indexOf(elevator_queue.get(1)));
            }
            else {
            currentCount--;
            dropOff_queue.remove(dropOff_queue.indexOf(elevator_queue.get(0)));
            }

        }
        elevator_queue.remove(0);

        // TODO: sort the queue
        ;
    }

    // is Elevator thread running?
    private boolean isElevatorRunning()
    {
        return elevatorRunning;
    }
    // start Elevator thread
    public void start() {
        if ( thread == null ) {
            thread = new Thread(this);
        }
        elevatorRunning = true;
        thread.start();
    }

    // stop Elevator thread; method run terminates
    public void stopElevator() {
        elevatorRunning = false;
    }

    // is Elevator moving?
    public boolean isMoving()
    {
        return moving;
    }

    // set if Elevator should move
    private void setMoving( boolean elevatorMoving )
    {
        moving = elevatorMoving;
    }

    // pause concurrent thread for number of milliseconds
    private void pauseThread( int time )
    {
        for (int i=SimulationClock.getTick(); i < (i+time);i++ ){}
    } // end method pauseThread

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



        while ( isElevatorRunning() ) {

            // remain idle until awoken
            while ( !isMoving() ) {
                pauseThread(10);
            }
            // pause while passenger exits (if one exists)
            pauseThread( PASSENDER_LEAVING_TIME );

            // Elevator needs 5 seconds to travel
            pauseThread( TRAVEL_TIME);
            // stop Elevator
            setMoving( false );
        } // end while loop

    } // end method run
}
