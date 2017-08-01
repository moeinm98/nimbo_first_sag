import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Engine {
    private Statistics tenMinStatistics = new Statistics();
    private Statistics oneHourStatistics = new Statistics();
    private Statistics oneDayStatistics = new Statistics();
    private Semaphore oneHourTimerSemaphore = new Semaphore(0);
    private Semaphore oneDayTimerSemaphore = new Semaphore(0);
    private File backupFile;
    private int time; //number of 10 mins passed

    public void start() {
        // todo: analyze the backup backupFile first, remember to add date to backupFile

        backupFile = new File("backup.txt");
        FileWriter fileWriterBackup;
        BufferedWriter bufferedWriterBackup = null;

        if (!backupFile.exists()) {
            try {
                backupFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                File tenMinResults = new File("tenMinTrends.txt");
                FileWriter fileWriterTenMin;
                BufferedWriter bufferedWriterTenMin = null;
                if (!tenMinResults.exists()) {
                    try {
                        tenMinResults.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fileWriterTenMin = new FileWriter(tenMinResults, true);
                    bufferedWriterTenMin = new BufferedWriter(fileWriterTenMin);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] trends = tenMinStatistics.findAndGetTrends();
                oneHourStatistics.mergeStatistics(tenMinStatistics);
                tenMinStatistics.clearStatistics();
                //<<<<<<<<<<<
                FileWriter fileWriter;

                try { //todo ehtemalan kar nakhahad kard
                    fileWriter = new FileWriter(backupFile);
                    fileWriter.write("");
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //>>>>>>>>>>>

                try {
                    bufferedWriterTenMin.write("\n\n" + time * 10 + " minutes\nuserName : " + trends[0] + "\nrepoName : " + trends[1] + "\nlanguage : " + trends[2]);
                    bufferedWriterTenMin.close();
                    System.out.println("tenMinTrends.txt updated!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                oneHourTimerSemaphore.release();

            }
        }, 1000 * 60 * 10, 1000 * 60 * 10);

        oneHourTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 6; i++) {
                    try {
                        oneHourTimerSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                File oneHourResults = new File("oneHourTrends.txt");
                FileWriter fileWriterOneHour;
                BufferedWriter bufferedWriterOneHour = null;
                if (!oneHourResults.exists()) {
                    try {
                        oneHourResults.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fileWriterOneHour = new FileWriter(oneHourResults, true);
                    bufferedWriterOneHour = new BufferedWriter(fileWriterOneHour);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] trends = oneHourStatistics.findAndGetTrends();
                oneDayStatistics.mergeStatistics(oneHourStatistics);
                oneHourStatistics.clearStatistics();

                try {
                    bufferedWriterOneHour.write("\n\n" + time / 6 + " hours\nuserName : " + trends[0] + "\nrepoName : " + trends[1] + "\nlanguage : " + trends[2]);
                    bufferedWriterOneHour.close();
                    System.out.println("oneHourTrends.txt updated!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                oneDayTimerSemaphore.release();
            }
        }, 1000 * 60 * 60, 1000 * 60 * 60);

        oneDayTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 24; i++) {
                    try {
                        oneDayTimerSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                File oneDayResults = new File("oneDayTrends.txt");
                FileWriter fileWriterOneDay;
                BufferedWriter bufferedWriterOneDay = null;
                if (!oneDayResults.exists()) {
                    try {
                        oneDayResults.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fileWriterOneDay = new FileWriter(oneDayResults, true);
                    bufferedWriterOneDay = new BufferedWriter(fileWriterOneDay);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String[] trends = oneDayStatistics.findAndGetTrends();
                oneDayStatistics.clearStatistics();
                try {
                    bufferedWriterOneDay.write("\n\n" + time / (6 * 24) + " days\nuserName : " + trends[0] + "\nrepoName : " + trends[1] + "\nlanguage : " + trends[2]);
                    bufferedWriterOneDay.close();
                    System.out.println("oneHourTrends.txt updated!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24);


    }
}
