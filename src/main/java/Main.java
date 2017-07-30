public class Main {

    public static void main(String[] args) {
        JsonMaker jsonMaker = new JsonMaker();
        Receiver receiver = new Receiver(jsonMaker);
        receiver.start();
        DataPros dataPros=new DataPros(receiver);
        dataPros.start();


    }
}