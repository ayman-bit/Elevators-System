package mun.concurrent.assignment.two;
import java.util.concurrent.ThreadLocalRandom;

public class ElevatorRiderFactory {

    public ElevatorRiderFactory() {}

    public void nextRider(){
        //TODO: create a Rider object
        int randomNum = ThreadLocalRandom.current().nextInt(20, 120 + 1);
        System.out.println(randomNum);
    }
}
