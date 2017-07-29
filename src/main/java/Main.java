public class Main {

    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        receiver.start();
        DataMaker dataMaker=new DataMaker(receiver);
        dataMaker.start();
        DataPros dataPros=new DataPros(dataMaker);
        dataPros.start();


    }
}