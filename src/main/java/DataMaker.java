import classes.Events;

import java.util.concurrent.ArrayBlockingQueue;

public class DataMaker extends Thread {

    private ArrayBlockingQueue<String> data;
    private ArrayBlockingQueue<Events> classes;

    public DataMaker(Receiver receiver) {
        data = receiver.getReceivedData();
    }

    public ArrayBlockingQueue getClasses() {
        return classes;
    }

    @Override
    public void run() {
        while (true){
            try {
                System.out.println(data.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
