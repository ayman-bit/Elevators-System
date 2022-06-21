package mun.concurrent.assignment.two;

public class CSFElevatorSimulator {

	static ElevatorSimulator esimulator;
	public static void main(String[] args)
	{
		ElevatorSimulator smallElevatorSimulator = new ElevatorSimulator(4, 1, 7200);
		ElevatorSimulator largeElevatorSimulator = new ElevatorSimulator(2, 2, 7200);
		smallElevatorSimulator.run();
		largeElevatorSimulator.run();
	}
}
