import java.io.*;
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


    private int time; //number of 10 mins passed

    public void start() {
        // todo: analyze the backup tenMinBackupFile first, remember to add date to tenMinBackupFile
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

                String[] trends = tenMinStatistics.findAndGetTrends();

                oneHourStatistics.mergeStatistics(tenMinStatistics);
                updateBackupFile("oneHourBackupFile.data", oneHourStatistics);
                tenMinStatistics.clearStatistics();
                updateOutputFile(tenMinResults, trends, time * 10, "Minutes");

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

                String[] trends = oneHourStatistics.findAndGetTrends();

                oneDayStatistics.mergeStatistics(oneHourStatistics);
                oneHourStatistics.clearStatistics();
                updateOutputFile(oneHourResults, trends, time / 6, "Hour");

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

                String[] trends = oneDayStatistics.findAndGetTrends();

                oneDayStatistics.clearStatistics();
                updateOutputFile(oneDayResults, trends, time / (6 * 24), "Day");

                oneHourTimerLatch = new CountDownLatch(24);
            }
        }, oneDayTimerDelay, 1000 * 60 * 60 * 24);
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
