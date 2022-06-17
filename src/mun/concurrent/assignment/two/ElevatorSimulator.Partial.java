package mun.concurrent.assignment.two;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.locks.*;

class ElevatorSimulator implements Runnable {

	private static Clock SimulationClock;
	private static ElevatorArray elevators;	
	
	private final int numElevators;
	private final int elevatorCapacity;
	
	private final int simulationTime;
	
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
		ArrayList<Thread> myThread = new ArrayList<Thread>();
		ElevatorArray elevatorArray = new ElevatorArray(numElevators, elevatorCapacity);

//		//create threads
//		for (int i = 0; i<numElevators; i++){
//			myThread.set(i, new Thread(this, String.valueOf(i)));
//			myThread.get(i).start();
//			System.out.println("Starting");
//		}
//		//each thread runs .run

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
				System.out.println("Thread going to sleep");
				Thread.sleep(50);
				System.out.println("Thread wake up");
				elevatorClockLock.lockInterruptibly(); // Use lockInterruptibly so that thread doesn't get stuck waiting for lock
				SimulationClock.tick();		
				elevatorClockTicked.signalAll();
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
