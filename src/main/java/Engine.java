import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

public class Engine
{
    private DataParser tenMinDataParser = new DataParser();
    private DataParser oneHourDataParser = new DataParser();
    private DataParser oneDayDataParser = new DataParser();
    private Semaphore oneHourTimerSemaphore = new Semaphore(0);
    private Semaphore oneDayTimerSemaphore = new Semaphore(0);
    private File file;

    public void start()
    {
        // todo: analyze the backup file first, remember to add date to backup file

        file = new File("backup.txt");
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            fileWriter = new FileWriter(file, true);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Receiver receiver = new Receiver(tenMinDataParser, bufferedWriter);
        receiver.start();
        startTimers();
    }

    private void startTimers()
    {
        Timer tenMinTimer = new Timer();
        Timer oneHourTimer = new Timer();
        Timer oneDayTimer = new Timer();

        tenMinTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                //todo update data
                oneHourDataParser.mergeData(tenMinDataParser);
                tenMinDataParser.clearData();

                oneHourTimerSemaphore.release();

                FileWriter fileWriter;

                try
                {
                    fileWriter = new FileWriter(file);
                    fileWriter.write("");
                    fileWriter.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }, 1000 * 60 * 10, 1000 * 60 * 10);

        oneHourTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 6; i++)
                {
                    try
                    {
                        oneHourTimerSemaphore.acquire();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                //todo update data
                oneDayDataParser.mergeData(oneHourDataParser);
                oneHourDataParser.clearData();

                oneDayTimerSemaphore.release();
            }
        }, 1000 * 60 * 60, 1000 * 60 * 60);

        oneDayTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 24; i++)
                {
                    try
                    {
                        oneDayTimerSemaphore.acquire();
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                //todo update data
                oneDayDataParser.clearData();
            }
        }, 1000 * 60 * 60 * 24, 1000 * 60 * 60 * 24);


    }
}
