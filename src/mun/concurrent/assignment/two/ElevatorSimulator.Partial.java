package mun.concurrent.assignment.two;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
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
	ArrayList<Integer> nextRidersTimes;
	
	// Allocate synchronization variables
	ReentrantLock elevatorClockLock = new ReentrantLock();

	ReentrantLock elevatorLock = new ReentrantLock();
	Condition riderAdded = elevatorLock.newCondition();


	Condition elevatorClockTicked = elevatorClockLock.newCondition();	

	//<MORE VARIABLES MAY BE NEEDED HERE>
	
	// Constructor
	public ElevatorSimulator(int numElevators, int elevatorCapacity, int simulationTime)
	{
		this.numElevators = numElevators;
		this.elevatorCapacity = elevatorCapacity;
		this.simulationTime = simulationTime;
//		elevators = new ElevatorArray(numElevators, elevatorCapacity);
		elevators = new ElevatorArray();
		elevatorRiderFactory = new ElevatorRiderFactory(elevators);
		List<Thread> myThread = new ArrayList<Thread>(numElevators);

		nextRidersTimes = new ArrayList<Integer>();
		for (int i = 0; i < 5; i++){
			nextRidersTimes.add(ThreadLocalRandom.current().nextInt(20, 120 + 1));
		}

		//create threads
		for (int i = 0; i<numElevators; i++){
			Object object = new Elevator(elevatorCapacity,0, 1);
			Runnable runnable = (Runnable) object;
			Thread thread = new Thread(runnable);
			Elevator elevator = (Elevator) object;
			elevators.add(elevator);
			// myThread.add(i,new Thread(new ElevatorSimulator(), String.valueOf(i)));
			// myThread.get(i).start();
			thread.start();
			System.out.println("Starting: " +  Thread.currentThread().getName());
		}
		elevatorRiderFactory = new ElevatorRiderFactory(elevators);
		//each thread runs .run

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
						elevators.scheduleRider(rider); // assigns rider to elevator
						nextRidersTimes.set(i, nextRiderTime);
					}

					System.out.println(i+1);
					System.out.println(nextRidersTimes.get(i));
				}

				// Signal elevator threads a rider has been added
				elevatorLock.lock(); // Is this needed?
				riderAdded.signalAll();

				System.out.println("current tick" + SimulationClock.getTick());
				System.out.println("\n");
				System.out.println("current Thread:" + Thread.currentThread().getName());


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
