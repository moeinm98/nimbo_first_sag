public class Engine {

    private JsonMaker tenMinJsonMaker = new JsonMaker();
    private JsonMaker OneHourJsonMaker = new JsonMaker();
    private JsonMaker OneDayJsonMaker = new JsonMaker();

    public void start(){

        //todo make merger for jsonmaker!

        Receiver receiver = new Receiver(tenMinJsonMaker);
        receiver.start();
        DataPros dataPros=new DataPros(receiver);
        dataPros.start();
    }


}
