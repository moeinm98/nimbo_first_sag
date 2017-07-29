import classes.Events;

import java.util.concurrent.ArrayBlockingQueue;

public class DataPros extends Thread{

    private ArrayBlockingQueue<Events> classes;


    public DataPros(DataMaker dataMaker){
        classes=dataMaker.getClasses();
    }
    @Override
    public void run() {

    }
}
