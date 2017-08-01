import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class Engine {
    private Statistics tenMinStatistics = new Statistics();
    private Statistics oneHourStatistics = new Statistics();
    private Statistics oneDayStatistics = new Statistics();
    private CountDownLatch oneHourTimerLatch = new CountDownLatch(6);
    private CountDownLatch oneDayTimerLatch = new CountDownLatch(24);
    private File backupFile;
    private File tenMinResults = new File("tenMinTrends.txt");
    private File oneHourResults = new File("oneHourTrends.txt");
    private File oneDayResults = new File("oneDayTrends.txt");

    private int time; //number of 10 mins passed

    public void start() {
        // todo: analyze the backup backupFile first, remember to add date to backupFile

        backupFile = new File("backup.txt");
        FileWriter fileWriterBackup;
        BufferedWriter bufferedWriterBackup = null;

        try {
            backupFile.createNewFile();
            tenMinResults.createNewFile();
            tenMinResults.createNewFile();
            tenMinResults.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileWriterBackup = new FileWriter(backupFile, true);
            bufferedWriterBackup = new BufferedWriter(fileWriterBackup);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Receiver receiver = new Receiver(tenMinStatistics, bufferedWriterBackup);
        receiver.start();
        startTimers();
    }

    private void startTimers() {
        Timer tenMinTimer = new Timer();
        Timer oneHourTimer = new Timer();
        Timer oneDayTimer = new Timer();

        tenMinTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                String[] trends = tenMinStatistics.findAndGetTrends();
                oneHourStatistics.mergeStatistics(tenMinStatistics);
                tenMinStatistics.clearStatistics();
                try {
                    FileWriter fileWriter = new FileWriter(backupFile);
                    fileWriter.write("");
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updateOutputFile(tenMinResults, trends, time * 10, "Minutes");
                oneHourTimerLatch.countDown();
            }
        }, 1000 * 60 * 10, 1000 * 60 * 10);

        oneHourTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    oneHourTimerLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String[] trends = oneHourStatistics.findAndGetTrends();
                oneDayStatistics.mergeStatistics(oneHourStatistics);
                oneHourStatistics.clearStatistics();
                updateOutputFile(oneHourResults, trends, time / 6, "Hour");
                oneDayTimerLatch.countDown();
                oneHourTimerLatch = new CountDownLatch(6);
            }
        }, 1000 * 60 * 60, 1000 * 60 * 60);

        oneDayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    oneDayTimerLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String[] trends = oneDayStatistics.findAndGetTrends();
                oneDayStatistics.clearStatistics();
                updateOutputFile(oneDayResults, trends, time / (6 * 24), "Day");
                oneHourTimerLatch = new CountDownLatch(24);
            }
        }, 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24);
    }

    private void updateOutputFile(File file, String[] trends, int time, String timeUnit) {
        try {
            FileWriter fileWriter = new FileWriter(oneDayResults, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("\n\n" + time + " " + timeUnit + "\nuserName : " + trends[0] + "\nrepoName : " + trends[1] + "\nlanguage : " + trends[2]);
            bufferedWriter.close();
            System.out.println(file.getName() + " UPDATED!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
