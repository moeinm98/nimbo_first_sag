import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import java.io.BufferedWriter;
import java.io.IOException;

public class Receiver {
    final private String endpoint = "wss://open-data.api.satori.com";
    final private String appkey = "86Cac8DF15eCaEF2B3A3846de9D5FF07";
    final private String channel = "github-events";
    private DataParser dataParser;
    private BufferedWriter bufferedWriter;

    public Receiver(DataParser dataParser, BufferedWriter bufferedWriter) {
        this.dataParser = dataParser;
        this.bufferedWriter = bufferedWriter;
    }

    public void start() {
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected to Satori RTM!");
                    }
                })
                .build();

        client.createSubscription(channel, SubscriptionMode.SIMPLE,
                new SubscriptionAdapter() {
                    @Override
                    public void onSubscriptionData(SubscriptionData data) {
                        for (AnyJson json : data.getMessages()) {
                            String message = json.toString();

                            try
                            {
                                bufferedWriter.write(message);
                                bufferedWriter.flush();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            dataParser.parseData(message);
                        }
                    }
                });
        client.start();
    }
}