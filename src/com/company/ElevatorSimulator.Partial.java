package mun.concurrent.assignment.two;

import java.util.concurrent.locks.*;

public class ElevatorSimulator implements Runnable {

	private static Clock SimulationClock;
	private static ElevatorArray elevators;	
	
	private final int numElevators;
	private final int elevatorCapacity;
	
	private final int simulationTime;
	
	private ElevatorStats elevatorStats;
	
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
	}
			
	public void run() {		

		//<INITIALIZATION HERE>

		SimulationClock = new Clock();
		// Simulate Small Elevators		
		while (SimulationClock.getTick() < simulationTime)
		{
			try
			{
				Thread.sleep(50);
				elevatorClockLock.lockInterruptibly(); // Use lockInterruptibly so that thread doesn't get stuck waiting for lock
				SimulationClock.tick();		
				elevatorClockTicked.signalAll();										
			}	
			catch (InterruptedException e)
			{				
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
