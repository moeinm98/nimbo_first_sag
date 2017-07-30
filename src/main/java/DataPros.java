import classes.Events;

import java.util.concurrent.ArrayBlockingQueue;

public class DataPros extends Thread{

    private ArrayBlockingQueue<Events> events;


    public DataPros(Receiver receiver){
        events =receiver.getEvnets();
    }
    @Override
    public void run() {

    }
}
