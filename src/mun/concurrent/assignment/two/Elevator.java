package mun.concurrent.assignment.two;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static mun.concurrent.assignment.two.ElevatorSimulator.SimulationClock;


public class Elevator {
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
    private static ReentrantLock elevatorClockLock;
    private static Condition elevatorClockTicked;
    ReentrantLock elevatorLock = new ReentrantLock(); // TODO Why this being used??
    private int elevatorIndex;

    public Elevator(int capacity, int currentCount, int currentFloor, int index){

        this.capacity = capacity;
        this.currentCount = currentCount;
        this.currentFloor = currentFloor;
        elevatorIndex = index;
    }

    public Elevator(){

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

    public void addRiderToElevQueue(Rider rider){
        // add rider.start to queue
        elevator_queue.add(rider.start_floor);

//        elevator_queue.add(rider.dest_floor);
        dropOff_queue.add(rider.dest_floor);
//        pickup_queue.add(rider.start_floor);


        // elevator_queue = [5, 1, 4,2]

        // pickup_queue = [5,4] DOWN

        // dropoff_queue = [1,2] DOWN
        // dropoff_queue = [2,1] DOWN





        // elevator at level one UP



        sortQueue(elevator_queue);
        // TODO: should we sort the dropoff queue?
    }

    private void sortQueue(List<Integer> queue) {
        if (status == Status.UP){
            Collections.sort(queue);
        }
        else if (status == Status.DOWN){
            Collections.sort(queue, Collections.reverseOrder());
        }
    }

    public void dropOffRider(){
        // elevator_queue.get(0) is current floor since array always gets sorted
        // what if 2 people want to get off here?
        Integer currentFloorI = (Integer) currentFloor;
        int occurences = Collections.frequency(dropOff_queue, currentFloorI);

        if (occurences == 2){
            System.out.println("Elevator " + getElevatorIndex() + " Dropping off riders");
            currentCount -= 2;
            dropOff_queue.remove(dropOff_queue.indexOf(currentFloor));
            dropOff_queue.remove(dropOff_queue.indexOf(currentFloor));
//            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
//            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
        }
        else if (occurences == 1) {
            System.out.println("Elevator " + getElevatorIndex() + " Dropping off riders");
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
//    public void start() {
//        if ( thread == null ) {
//            thread = new Thread(this);
//        }
//        elevatorRunning = true;
//        thread.start();
//    }

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

    public void updateFloor() {
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
        //TODO: this needs to account for 2 cases: going to pick up rider
        if (elevator_queue.get(0) > currentFloor){
            System.out.println("elevator current floor: " + currentFloor + " Destination floor: " + elevator_queue.get(0) + " Moving UP");
            status = Status.UP;
        }
        else {
            System.out.println("elevator current floor: " + currentFloor + " Destination floor: " + elevator_queue.get(0) + " Moving DOWN");
            status = Status.DOWN;
        }
    }

    public List<Integer> getElevatorQueue() {
        return elevator_queue;
    }

    public void pickUpRider() {
        if (elevator_queue.contains(currentFloor)){

            if (currentCount== capacity){
                //TODO: Reject rider
                System.out.println("Rejected");
            }

            else {
                currentCount++;
            }

            // Remove current floor from elevator queue once rider enters/is rejected
            elevator_queue.remove(elevator_queue.indexOf(currentFloor));
        }

    }

    public int getElevatorIndex() {
        return elevatorIndex;
    }

    public List<Integer> getDropoffQueue() {
        return dropOff_queue;
    }

    public void setStatusDoorsOpen() {
        status = Status.DOORS_OPEN;
        System.out.println("Elevator "+ elevatorIndex + ": Doors open");
    }
}
