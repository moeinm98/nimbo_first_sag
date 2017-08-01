import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Engine {
    private Statistics tenMinStatistics = new Statistics();
    private Statistics oneHourStatistics = new Statistics();
    private Statistics oneDayStatistics = new Statistics();
    private Semaphore oneHourTimerSemaphore = new Semaphore(0);
    private Semaphore oneDayTimerSemaphore = new Semaphore(0);
    private int tenMinTimerDelay;
    private int oneHourTimerDelay;
    private int oneDayTimerDelay;
    private int time; //number of 10 mins passed

    public void start() {
        // todo: analyze the backup tenMinBackupFile first, remember to add date to tenMinBackupFile
        findBackupFiles();
        findTimerDelays();


        Receiver receiver = new Receiver(tenMinStatistics);
        receiver.start();
        startTimers();
    }

    private void findTimerDelays()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String time = dateFormat.format(date);
        String[] timeParts = time.split(":");

        findTenMinTimerDelay(timeParts);
        findOneHourTimerDelay(timeParts);
        findOneDayTimerDelay(timeParts);
    }

    private void findOneDayTimerDelay(String[] timeParts)
    {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]);
        oneDayTimerDelay = ((60 - secNum) + 60*(60 - minNum)) * 1000;
    }

    private void findOneHourTimerDelay(String[] timeParts)
    {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]);
        int hourNum = Integer.parseInt(timeParts[0]);
        oneHourTimerDelay = ((60 - secNum) + 60*(10 - minNum) + (24 - hourNum)*3600) * 1000;
    }

    private void findTenMinTimerDelay(String[] timeParts)
    {
        int secNum = Integer.parseInt(timeParts[2]);
        int minNum = Integer.parseInt(timeParts[1]) % 10;
        tenMinTimerDelay = ((60 - secNum) + 60*(10 - minNum)) * 1000;
    }

    private void findBackupFiles()
    {
        File oneHourBackupFile = new File("oneHourBackup.data");
        File oneDayBackupFile = new File("oneDatBackup.data");

        if (oneHourBackupFile.exists())
        {
            try
            {
                FileInputStream receivingFileInputStream = new FileInputStream(oneHourBackupFile);
                ObjectInputStream receivingObjectInputStream = new ObjectInputStream(receivingFileInputStream);
                oneHourStatistics = (Statistics) receivingObjectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }

        if (oneDayBackupFile.exists())
        {
            try
            {
                FileInputStream receivingFileInputStream = new FileInputStream(oneDayBackupFile);
                ObjectInputStream receivingObjectInputStream = new ObjectInputStream(receivingFileInputStream);
                oneDayStatistics = (Statistics) receivingObjectInputStream.readObject();
            } catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
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
                updateBackupFile("oneHourBackupFile.data", oneHourStatistics);
                tenMinStatistics.clearStatistics();
                //<<<<<<<<<<<
//                FileWriter fileWriter;
//
//                try { //todo ehtemalan kar nakhahad kard
//                    fileWriter = new FileWriter(tenMinBackupFile);
//                    fileWriter.write("");
//                    fileWriter.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
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
        }, tenMinTimerDelay, 1000 * 60 * 10);

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
                updateBackupFile("oneDayBackupFile.data", oneDayStatistics);
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
        }, oneHourTimerDelay, 1000 * 60 * 60);

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
        }, oneDayTimerDelay, 1000 * 60 * 60 * 24);


    }

    private void updateBackupFile(String backupFileName, Statistics statistics)
    {
        try
        {
            FileOutputStream backupFileOutputStream = new FileOutputStream(backupFileName);
            ObjectOutputStream backupObjectOutputStream = new ObjectOutputStream(backupFileOutputStream);
            backupObjectOutputStream.writeObject(statistics);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
