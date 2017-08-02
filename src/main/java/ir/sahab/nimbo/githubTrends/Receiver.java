package ir.sahab.nimbo.githubTrends;

import com.satori.rtm.*;
import com.satori.rtm.model.AnyJson;
import com.satori.rtm.model.SubscriptionData;
import ir.sahab.nimbo.utils.BackupHandler;

import java.io.IOException;

public class Receiver {
    final private String endpoint = "wss://open-data.api.satori.com";
    final private String appkey = "86Cac8DF15eCaEF2B3A3846de9D5FF07";
    final private String channel = "github-events";
    private Statistics statistics;

    public Receiver(Statistics statistics) {
        this.statistics = statistics;
    }

    public void start() {
        final RtmClient client = new RtmClientBuilder(endpoint, appkey)
                .setListener(new RtmClientAdapter() {
                    @Override
                    public void onEnterConnected(RtmClient client) {
                        System.out.println("Connected!");
                    }
                })
                .build();

        client.createSubscription(channel, SubscriptionMode.SIMPLE,
                new SubscriptionAdapter() {
                    @Override
                    public void onSubscriptionData(SubscriptionData data) {
                        for (AnyJson json : data.getMessages()) {
                            String message = json.toString();
                            statistics.updateStatistics(message);
                        }
                        try
                        {
                            BackupHandler.updateBackupFile("tenMinBackup.data", statistics);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        client.start();
    }
}