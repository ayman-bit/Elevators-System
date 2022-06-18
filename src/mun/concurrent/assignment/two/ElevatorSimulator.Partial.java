package mun.concurrent.assignment.two;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.*;

class ElevatorSimulator implements Runnable {

	static Clock SimulationClock;
	private static Clock SimulationRider;
	private static ElevatorArray elevators;	
	
	private int numElevators;
	private int elevatorCapacity;
	
	private int simulationTime;
	
	private ElevatorStats elevatorStats;
	private Elevator elevator;
	private Rider rider;
	
	private ElevatorRiderFactory elevatorRiderFactory;
	
	// Allocate synchronization variables
	ReentrantLock elevatorClockLock = new ReentrantLock();

	ReentrantLock elevatorLock = new ReentrantLock();


	Condition elevatorClockTicked = elevatorClockLock.newCondition();	

	//<MORE VARIABLES MAY BE NEEDED HERE>
	
	// Constructor
	public ElevatorSimulator(int numElevators, int elevatorCapacity, int simulationTime)
	{
		this.numElevators = numElevators;
		this.elevatorCapacity = elevatorCapacity;
		this.simulationTime = simulationTime;
		elevatorRiderFactory = new ElevatorRiderFactory();
		ArrayList<Thread> myThread = new ArrayList<Thread>();
		ElevatorArray elevatorArray = new ElevatorArray(numElevators, elevatorCapacity);
		Runnable runnable = new ElevatorSimulator();
		//create threads
		for (int i = 0; i<numElevators; i++){
			Thread thread = new Thread(runnable);
			thread.start();
//			myThread.set(i, new Thread(this, String.valueOf(i)));
//			myThread.get(i).start();
			System.out.println("Starting");
		}
//		//each thread runs .run

	}

	public ElevatorSimulator() {

	}

	@Override
	public void run() {		

		//<INITIALIZATION HERE>
		SimulationClock = new Clock();
		// Simulate Small Elevators
		while (SimulationClock.getTick() < simulationTime)
		{
			try
			{
//				System.out.println("Thread going to sleep");
				Thread.sleep(50);
				elevatorClockLock.lockInterruptibly(); // Use lockInterruptibly so that thread doesn't get stuck waiting for lock
				SimulationClock.tick();		
				elevatorClockTicked.signalAll();
				elevatorRiderFactory.setNextRiderRider();

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
		
		// Output elevator stats

		//<PRINT OUT STATS GATHERED DURING SIMULATION>

		SimulationClock.reset();			
	}	
}
