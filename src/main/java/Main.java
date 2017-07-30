public class Main {

    public static void main(String[] args) {
        Receiver receiver = new Receiver();
        receiver.start();
        DataPros dataPros=new DataPros(receiver);
        dataPros.start();


    }
}