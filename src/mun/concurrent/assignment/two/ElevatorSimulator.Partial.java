package mun.concurrent.assignment.two;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.*;

// average amount of time taken to serve each elevator rider's request

class ElevatorSimulator implements Runnable {

	static Clock SimulationClock = new Clock();
	private static Clock SimulationRider;
	private static ElevatorArray elevators;
	
	private int numElevators;
	private int elevatorCapacity;
	
	private int simulationTime;

	private int totalRequests = 5;
	public static int ridersTime = 0;
	public int rejects = 0;

	
	private ElevatorRiderFactory elevatorRiderFactory;
	ArrayList<Integer> nextRidersTimes;
	
	// Allocate synchronization variables
	ReentrantLock elevatorClockLock = new ReentrantLock();

	ReentrantLock elevatorLock = new ReentrantLock();
	Condition riderAdded = elevatorLock.newCondition();
	boolean mainEnded = false; //TODO: need to find a better way


	Condition elevatorClockTicked = elevatorClockLock.newCondition();	

	//<MORE VARIABLES MAY BE NEEDED HERE>
	
	// Constructor
	public ElevatorSimulator(int numElevators, int elevatorCapacity, int simulationTime)
	{
		this.numElevators = numElevators;
		this.elevatorCapacity = elevatorCapacity;
		this.simulationTime = simulationTime;
		elevators = new ElevatorArray(numElevators, elevatorCapacity, elevatorLock, riderAdded, mainEnded,
				elevatorClockLock, elevatorClockTicked, SimulationClock, this);

		elevatorRiderFactory = new ElevatorRiderFactory();

		nextRidersTimes = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++){
			nextRidersTimes.add(ThreadLocalRandom.current().nextInt(20, 120 + 1));
		}

		//create threads
		for (int i = 0; i<numElevators; i++){
			Runnable runnable = elevators;
			Thread thread = new Thread(runnable);
			thread.start();
		}


	}


	@Override
	public void run() {		

		while (SimulationClock.getTick() < simulationTime)
		{
			try
			{
				Thread.sleep(50);
				elevatorClockLock.lockInterruptibly(); // Use lockInterruptibly so that thread doesn't get stuck waiting for lock
				SimulationClock.tick();		
				elevatorClockTicked.signalAll();

				// Check if a rider is set to be generated now. If so, generate that rider, and time for next one.
				for (int i=0; i<5; i++){
					int currentNextRiderTime = nextRidersTimes.get(i);

					if (currentNextRiderTime == SimulationClock.getTick()) {
						int nextRiderTime = SimulationClock.getTick() + ThreadLocalRandom.current().nextInt(20, 120 + 1);
						Rider rider = elevatorRiderFactory.generateRiderFloor(i+1);
						elevators.addRiderToRiders(rider);
						nextRidersTimes.set(i, nextRiderTime);
						Thread.sleep(10);
						totalRequests++; // TODO: this is a simplification, should be when time for request comes
					}

					System.out.println("Floor: "+ (i+1) + ". Next rider: " + nextRidersTimes.get(i));
				}

				System.out.println("current tick: " + SimulationClock.getTick());
				System.out.println("current Thread:" + Thread.currentThread().getName());
				System.out.println("\n");

			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			finally
			{	
				elevatorClockLock.unlock();			
			}	
		}		

		mainEnded = true;
		// Output elevator stats
		System.out.println("Total Requests: " + totalRequests);
		System.out.println("Total Rejects: " + rejects);
		System.out.println("Total Served: " + (totalRequests - rejects));
		System.out.println("Average Time Per Request: " + (ridersTime/totalRequests));

		SimulationClock.reset();			
	}

}
