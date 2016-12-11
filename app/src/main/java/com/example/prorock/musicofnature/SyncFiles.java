package com.example.prorock.musicofnature;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.os.Environment.MEDIA_UNKNOWN;

class SyncFiles extends AsyncTask<Void, Void, Void> {
    private FTPClient connectionFTP;
    private String serverFTPaddress = "soundofnature.ucoz.net";
    private String loginFTPServer = "esoundofnature";
    private String passwordFTPServer = "LCYE2sllrPoh";
    private boolean isConnectedToFTP = false;
    private boolean isSyncedCorrectly = false;
    private Context mainContext;


    SyncFiles(Context appContext) {
        mainContext = appContext;
    }

    private void connectToFTP() throws IOException {
        //TODO: Exception on login
        connectionFTP.connect(serverFTPaddress, 21);
        if (connectionFTP.login(loginFTPServer, passwordFTPServer))
            {
            connectionFTP.enterLocalPassiveMode();
            connectionFTP.setFileType(FTP.BINARY_FILE_TYPE);
            isConnectedToFTP = true;
        }
        else
            throw new IOException();
    }

    private String getLocalFolderPath() {
        String sdState = android.os.Environment.getExternalStorageState(); //Получаем состояние SD карты (подключена она или нет) - возвращается true и false соответственно
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        else {
            return mainContext.getFilesDir().toString();
        }
    }

    private void SyncLocalFolderToFTP(String localFolderPath) throws IOException {
        File coreDir = new File(localFolderPath);
        if (!coreDir.exists()) {
            boolean state = coreDir.mkdirs();
            if (state == false)
                throw new IOException();
        }

        FTPFile[] ftpFileList = connectionFTP.listFiles();
        // Проверяем и создаем корневые папки
        compareLocalWithRemoteFolders(localFolderPath, ftpFileList);

        // Проходим по каждой папке
        //connectionFTP.setFileType(FTPClient.BINARY_FILE_TYPE);
        for (FTPFile localDir : ftpFileList) {
            compareLocalWithRemoteFiles(connectionFTP.listFiles(localDir.getName()),
                    localFolderPath + File.separator + localDir.getName(), localDir.getName());
        }

        isSyncedCorrectly = true;
    }

    private void compareLocalWithRemoteFiles(FTPFile[] ftpFileList, String localFolderPath,
                                             String remoteFolderName) throws IOException {
        if (ftpFileList != null && ftpFileList.length > 0) {
            for (FTPFile ftpfile : ftpFileList) {
                if (ftpfile.isFile()) { /* TODO: Проверять еще нужно на наличие файла и
                                           на его совпадение с версией на сервере,
                                           и на то, если файл удален на сервере но есть на карте */
                    FileOutputStream output;
                    File targetFile = new File(localFolderPath + File.separator + ftpfile.getName());
                    output = new FileOutputStream(targetFile);
                    //get the file from the remote system
                    connectionFTP.retrieveFile(File.separator + remoteFolderName + File.separator + ftpfile.getName(), output);
                    //close output stream
                    output.close();
                }
            }
        }
    }

    private void compareLocalWithRemoteFolders(String localFolderPath, FTPFile[] ftpFileList) throws IOException {
        if (ftpFileList != null && ftpFileList.length > 0) {
            for (FTPFile ftpDir : ftpFileList) {
                if (ftpDir.isDirectory()) {
                    File localDir = new File(localFolderPath + File.separator + ftpDir.getName());
                    if (!localDir.exists())
                        localDir.mkdir();
                }
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        connectionFTP = new FTPClient();
        try {
            connectToFTP();
            String localFolderPath = getLocalFolderPath() + File.separator + "NatureMusic";
            SyncLocalFolderToFTP(localFolderPath);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!isSyncedCorrectly)
            Toast.makeText(mainContext, "Error with syncing!", Toast.LENGTH_SHORT).show();
        else if (!isConnectedToFTP)
            Toast.makeText(mainContext, "Error with connecting to server!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mainContext, "Sync complete!", Toast.LENGTH_SHORT).show();
    }
}
