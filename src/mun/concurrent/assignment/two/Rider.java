package mun.concurrent.assignment.two;

public class Rider {
    //TODO: start floor, destination floor
    public int start_floor;
    public int dest_floor;


    public Rider(int start_floor, int end_floor){
        this.start_floor=start_floor;
        this.dest_floor=end_floor;
        System.out.println("Rider generated");
    }


}
