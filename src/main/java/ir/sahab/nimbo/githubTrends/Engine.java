package ir.sahab.nimbo.githubTrends;

import ir.sahab.nimbo.utils.BackupHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class Engine {
    private Statistics tenMinStatistics = new Statistics();
    private Statistics oneHourStatistics = new Statistics();
    private Statistics oneDayStatistics = new Statistics();
    private int tenMinTimerDelay;
    private int oneHourTimerDelay;
    private int oneDayTimerDelay;
    private CountDownLatch oneHourTimerLatch = new CountDownLatch(6);
    private CountDownLatch oneDayTimerLatch = new CountDownLatch(24);
    private File tenMinResults = new File("tenMinTrends.txt");
    private File oneHourResults = new File("oneHourTrends.txt");
    private File oneDayResults = new File("oneDayTrends.txt");

    public void start() throws IOException, ClassNotFoundException {
        findBackupFiles();
        findTimerDelays();

        try {
            tenMinResults.createNewFile();
            oneHourResults.createNewFile();
            oneDayResults.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Receiver receiver = new Receiver(tenMinStatistics);
        receiver.start();
        startTimers();
    }

    private void findTimerDelays() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        String[] timeParts = time.split(":");

        findTenMinTimerDelay(timeParts);
        findOneHourTimerDelay(timeParts);
        findOneDayTimerDelay(timeParts);
    }

    private void findOneDayTimerDelay(String[] timeParts) {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]);
        int hourNum = Integer.parseInt(timeParts[0]);
        oneDayTimerDelay = ((60 - secNum) + 60 * (10 - minNum - 1) + (24 - hourNum - 1) * 3600) * 1000;
    }

    private void findOneHourTimerDelay(String[] timeParts) {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]) % 10;
        oneHourTimerDelay = ((60 - secNum) + 60 * (60 - minNum - 1)) * 1000;
    }

    private void findTenMinTimerDelay(String[] timeParts) {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]) % 10;
        tenMinTimerDelay = ((60 - secNum) + 60 * (10 - minNum - 1)) * 1000;
    }

    private void findBackupFiles() throws IOException, ClassNotFoundException {
        File tenMinBackupFile = new File("tenMinBackup.data");
        File oneHourBackupFile = new File("oneHourBackup.data");
        File oneDayBackupFile = new File("oneDayBackup.data");

        tenMinStatistics = BackupHandler.getBackupIfExists(tenMinBackupFile);
        oneHourStatistics = BackupHandler.getBackupIfExists(oneHourBackupFile);
        oneDayStatistics = BackupHandler.getBackupIfExists(oneDayBackupFile);
    }

    private void startTimers() {
        Timer tenMinTimer = new Timer();
        Timer oneHourTimer = new Timer();
        Timer oneDayTimer = new Timer();

        tenMinTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Object[] trends = tenMinStatistics.findAndGetTrends();

                oneHourStatistics.mergeStatistics(tenMinStatistics);
                tenMinStatistics.clearStatistics();
                updateOutputFile(tenMinResults, trends);
                try {
                    BackupHandler.updateBackupFile("oneHourBackup.data", oneHourStatistics);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                oneHourTimerLatch.countDown();
            }
        }, tenMinTimerDelay, 1000 * 60 * 10);

        oneHourTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    oneHourTimerLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Object[] trends = oneHourStatistics.findAndGetTrends();

                oneDayStatistics.mergeStatistics(oneHourStatistics);
                oneHourStatistics.clearStatistics();
                updateOutputFile(oneHourResults, trends);
                try {
                    BackupHandler.updateBackupFile("oneDayBackup.data", oneDayStatistics);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                oneDayTimerLatch.countDown();
                oneHourTimerLatch = new CountDownLatch(6);
            }
        }, oneHourTimerDelay, 1000 * 60 * 60);

        oneDayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    oneDayTimerLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Object[] trends = oneDayStatistics.findAndGetTrends();

                oneDayStatistics.clearStatistics();
                updateOutputFile(oneDayResults, trends);

                oneHourTimerLatch = new CountDownLatch(24);
            }
        }, oneDayTimerDelay, 1000 * 60 * 60 * 24);
    }

    private void updateOutputFile(File file, Object[] trends) {
        Date today = new Date();
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("\n\n" + timeFormat.format(today) + "\nuserName : " + trends[0].toString()
                    + "\nrepoName : " + trends[1].toString() + "\nlanguage : " + trends[2].toString() + "\norganization : "
                    + trends[3].toString());
            bufferedWriter.close();
            System.out.println(file.getName() + " UPDATED!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
