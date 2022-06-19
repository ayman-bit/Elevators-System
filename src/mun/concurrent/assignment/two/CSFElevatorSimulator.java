package mun.concurrent.assignment.two;

public class CSFElevatorSimulator {

	static ElevatorSimulator esimulator;
	public static void main(String[] args)
	{
		ElevatorSimulator smallElevatorSimulator = new ElevatorSimulator(4, 1, 120);
		ElevatorSimulator largeElevatorSimulator = new ElevatorSimulator(2, 2, 30);
		smallElevatorSimulator.run();
		largeElevatorSimulator.run();
	}
}
