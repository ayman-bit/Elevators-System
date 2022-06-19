package mun.concurrent.assignment.two;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;


public class Elevator implements Runnable {
    private int capacity; // max capacity of elevator
    private int currentCount; // how many members in the elevator
    private int currentFloor;
    private Thread thread; // Elevator's thread to handle asynchronous movement
    private List<Integer> elevator_queue = new ArrayList<Integer>();
    private List<Integer> dropOff_queue = new ArrayList<Integer>();
    private Status status;
    private boolean elevatorRunning = false;
    private boolean moving = false;
    private static final int PASSENDER_LEAVING_TIME = 15;
    private static final int TRAVEL_TIME = 5;
    ReentrantLock elevatorLock = new ReentrantLock();
    Condition riderAdded = elevatorLock.newCondition();
    //Condition noRider = elevatorLock.newCondition();

    public Elevator(int capacity, int currentCount, int currentFloor){

        this.capacity = capacity;
        this.currentCount = currentCount;
        this.currentFloor = currentFloor;
    }


    public Status getStatus(){
        return status;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getDestinationFloor() {
        return elevator_queue.get(0);
    }

    public void addRider(Rider rider){
        // add rider.start to queue
        elevator_queue.add(rider.start_floor);
        elevator_queue.add(rider.dest_floor);
        // TODO: sort the queue. ISSUE: when elevator going down, should be sorted in reverse order. maybe no need to sort.
        dropOff_queue.add(rider.dest_floor);
//        riderAdded.signalAll();
    }

    public void removeRider(int currentFloor){
        // elevator_queue.get(0) is current floor since array always gets sorted
        // what if 2 people want to get off here?
        Integer currentFloorI = (Integer) currentFloor;
        int occurences = Collections.frequency(dropOff_queue, currentFloorI);

        if (occurences == 2){
            currentCount -= 2;
            dropOff_queue.remove(dropOff_queue.indexOf(currentFloor));
            dropOff_queue.remove(dropOff_queue.indexOf(currentFloor));
            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
        }
        else {
            currentCount--;
            dropOff_queue.remove(dropOff_queue.indexOf(currentFloor));
            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
        }


        // TODO: sort the queue
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

    // Simulate elevator moving
    public void run(){
        System.out.println("current elevator: " +  Thread.currentThread().getName());
        System.out.println("WEEE GOTT SOMEONE INSIDE THE ELEVATOR: " + elevator_queue.size());

//        elevatorLock.lock();
//        try {
//            while(elevator_queue.size() == 0){
//                riderAdded.await();
//            }

            // while there exists destinations on the queue
            while(elevator_queue.size() > 0 ){
                System.out.println("current thread" + Thread.currentThread().getName() + elevator_queue.size());
                // Set status based on direction
                setStatusUpDown();

                // TODO: lock the lock to read tick initially
                // read current tick
                int currentTime = SimulationClock.getTick();

                // Go to that floor.
                while(getCurrentFloor() != elevator_queue.get(0)){
                    // lock clock
                    if (SimulationClock.getTick() == currentTime + 5){
                        updateFloor();
                        currentTime = SimulationClock.getTick();
                    }
                    // unlock here
                }

                // Cases: 1 drop off only, 2 drop off only, 1 drop off and 1 entering, 2 drop off and 1 entering, 1 entering only

                // Drop off rider (or 2 riders) if current floor contained in dropOff_queue
                if (dropOff_queue.contains(getCurrentFloor())){
                    removeRider(getCurrentFloor());
                    System.out.println("Dropped off rider");
                }

                // Increment count if current floor remains on elevator_queue after potentially removing rider above
                if (elevator_queue.contains(getCurrentFloor())){

                    if (currentCount== capacity){
                        //TODO: Reject rider
                        System.out.println("Rejected");
                    }

                    else {
                        currentCount++;
                    }

                    // Remove current floor from elevator queue once rider enters/is rejected
                    elevator_queue.remove(elevator_queue.indexOf(getCurrentFloor()));
                }

                setStatusStationary();

                // Wait 15 seconds
                while (SimulationClock.getTick() < currentTime + 15){ // "Less than" is used instead of "!=" in case clock ticked twice
                    //sleep 10 ms maybe
                }

                // What should happen if no more destinations on the queue? just sleep the thread maybe
                // (Do we do a signalAll from ElevatorArray? If we do signalAll, each sleeping elevator will ask "Did something get added to my queue" )

            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        finally {
//            elevatorLock.unlock();
//        }

//        while ( isElevatorRunning() ) {
//
//            // remain idle until awoken
//            while ( !isMoving() ) {
//                pauseThread(10);
//            }
//            // pause while passenger exits (if one exists)
//            pauseThread( PASSENDER_LEAVING_TIME );
//
//            // Elevator needs 5 seconds to travel
//            pauseThread( TRAVEL_TIME);
//            // stop Elevator
//            setMoving( false );
//        } // end while loop

    } // end method run

    private void updateFloor() {
        if (status == Status.UP){
            currentFloor++;
        }
        else if (status == Status.DOWN){
            currentFloor--;
        }
    }

    public void setStatusStationary() {
        if (elevator_queue.size() == 0){
            status = Status.STATIONARY;
        }
        else if (status == Status.UP){
            status = Status.STATIONARY_UP;
        }
        else if (status == Status.DOWN){
            status = Status.STATIONARY_DOWN;
        }
    }

    // Set the status to UP or DOWN
    public void setStatusUpDown(){
        if (elevator_queue.get(0) > getCurrentFloor()){
            status = Status.DOWN;
        }
        else {
            status = Status.UP;
        }
    }
}
