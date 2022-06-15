package mun.concurrent.assignment.two;

public class Clock {

	private int curtick;
	
	public Clock()
	{
		curtick = 0;
	}
	
	public void tick()
	{
		curtick++;
	}
	
	public int getTick()
	{
		return curtick;
	}
	
	public void reset() {
		curtick = 0;
	}
}
