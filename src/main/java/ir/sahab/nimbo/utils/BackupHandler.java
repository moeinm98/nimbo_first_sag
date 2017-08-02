package ir.sahab.nimbo.utils;

import ir.sahab.nimbo.githubTrends.Statistics;

import java.io.*;

public class BackupHandler
{
    public static void updateBackupFile(String backupFileName, Statistics statistics) throws IOException
    {
        FileOutputStream backupFileOutputStream = new FileOutputStream(backupFileName);
        ObjectOutputStream backupObjectOutputStream = new ObjectOutputStream(backupFileOutputStream);
        backupObjectOutputStream.writeObject(statistics);
    }

    public static Statistics getBackupIfExists(File backupFile) throws IOException, ClassNotFoundException
    {
        if (backupFile.exists()) {
            FileInputStream receivingFileInputStream = new FileInputStream(backupFile);
            ObjectInputStream receivingObjectInputStream = new ObjectInputStream(receivingFileInputStream);
            return (Statistics) receivingObjectInputStream.readObject();
        }

        return new Statistics();
    }
}
