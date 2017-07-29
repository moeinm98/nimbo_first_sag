import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;

import java.util.concurrent.ArrayBlockingQueue;

public class Receiver {
    final private String endpoint = "wss://open-data.api.satori.com";
    final private String appkey = "86Cac8DF15eCaEF2B3A3846de9D5FF07";
    final private String channel = "github-events";
    private ArrayBlockingQueue<String> receivedData = new ArrayBlockingQueue<>(1000);

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
                            try {
                                receivedData.put(json.toString());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        client.start();
    }

    public ArrayBlockingQueue<String> getReceivedData() {
        return receivedData;
    }
}