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
        status = Status.STATIONARY;
    }

    public Elevator(){

    }

    public Status getStatus(){
        return status;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void addRiderToElevQueue(Rider rider){
        elevator_queue.add(rider.start_floor);
        elevator_queue.add(rider.dest_floor);
        dropOff_queue.add(rider.dest_floor);


//        sortQueue(elevator_queue);
        System.out.println("elevator_queue: " + elevator_queue);
        sortQueue(dropOff_queue);
        System.out.println("dropOff_queue: " + dropOff_queue);

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
        Integer currentFloorI = (Integer) currentFloor;
        int occurences = Collections.frequency(dropOff_queue, currentFloorI);

        if (occurences == 2){
            System.out.println("Elevator " + getElevatorIndex() + " Dropping off riders");
            currentCount -= 2;
            int toRemove = dropOff_queue.indexOf(currentFloor);
            if (toRemove != -1){
                dropOff_queue.remove(toRemove);
                dropOff_queue.remove(toRemove);
            }

            toRemove = elevator_queue.indexOf(currentFloor);
            if (toRemove != -1){
                elevator_queue.remove(toRemove); //TODO: should fix this
                elevator_queue.remove(toRemove); //TODO: should fix this

            }
        }
        else if (occurences == 1) {
            System.out.println("Elevator " + getElevatorIndex() + " Dropping off riders");
            currentCount--;

            int toRemove = dropOff_queue.indexOf(currentFloor);
            if (toRemove != -1){
                dropOff_queue.remove(toRemove);
            }

            toRemove = elevator_queue.indexOf(currentFloor);
            if (toRemove != -1){
                elevator_queue.remove(toRemove); //TODO: should fix this
            }

        }


    }

    // return false if reject
    public boolean pickUpRider() {
        boolean b = true;
        // Pickup if current floor is not in drop off queue
        // Dropoffs happen BEFORE pickups, so this check should be good
        if (elevator_queue.contains(currentFloor) && !dropOff_queue.contains(currentFloor)){

            if (currentCount== capacity){
                //TODO: Reject rider, and remove their floors from the queues
                elevator_queue.remove(elevator_queue.indexOf(currentFloor));
                //TODO: how to remove the rider's destination
                System.out.println("Rejected");
                b =false;
            }

            else {
                currentCount++;
            }

            // Remove current floor from elevator queue once rider enters/is rejected
            System.out.println("ElevQ: " + elevator_queue);
            elevator_queue.remove(0);
            System.out.println("elevator_queue after pickup: " + elevator_queue + ". Size: " + elevator_queue.size());
        }

        return b;
    }

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
        else if(elevator_queue.get(0) < currentFloor) {
            System.out.println("elevator current floor: " + currentFloor + " Destination floor: " + elevator_queue.get(0) + " Moving DOWN");
            status = Status.DOWN;
        }
    }

    public List<Integer> getElevatorQueue() throws InterruptedException {
        List<Integer> list;
        elevatorLock.lockInterruptibly();
        try {
            list = elevator_queue;
        } finally {
            elevatorLock.unlock();
        }
        return  list;

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
