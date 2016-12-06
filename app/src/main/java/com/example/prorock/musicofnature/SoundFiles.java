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

class SoundFiles extends AsyncTask<Void, Void, Void> {
    private FTPClient connectionFTP;
    private String serverFTPaddress = "soundofnature.ucoz.net";
    private String loginFTPServer = "esoundofnature";
    private String passwordFTPServer = "LCYE2sllrPoh";
    private boolean isConnectedToFTP = false;
    private Context mainContext;


    SoundFiles(Context appContext) {
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
            coreDir.mkdir();
        }

        File[] localFileList = coreDir.listFiles();
        FTPFile[] ftpFileList = connectionFTP.listFiles();
        // Проверяем и создаем корневые папки
        compareLocalWithRemoteFolder(localFolderPath, ftpFileList, null);

        // Проходим по каждой папке
        // TODO: Сделать разные функции для папок и для файлов
        //connectionFTP.setFileType(FTPClient.BINARY_FILE_TYPE);
        for (FTPFile localDir : ftpFileList) {
            compareLocalWithRemoteFolder(localFolderPath + File.separator + localDir.getName(),
                    connectionFTP.listFiles(localDir.getName()), localDir.getName());
        }
    }

    private void compareLocalWithRemoteFolder(String localFolderPath, FTPFile[] ftpFileList, String remoteFolderName) throws IOException {
        if (ftpFileList != null && ftpFileList.length > 0) {
            for (FTPFile ftpfile : ftpFileList) {
                if (ftpfile.isDirectory()) {
                    File localDir = new File(localFolderPath + File.separator + ftpfile.getName());
                    if (!localDir.exists())
                        localDir.mkdir();
                }
                else if (ftpfile.isFile()) { // TODO: Проверять еще нужно на наличие файла И на его совпадение с версией на сервере
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
        if (isConnectedToFTP)
            Toast.makeText(mainContext, "Sync complete!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mainContext, "Error with syncing!", Toast.LENGTH_SHORT).show();
    }
}
