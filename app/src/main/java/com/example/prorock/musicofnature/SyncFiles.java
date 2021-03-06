package com.example.prorock.musicofnature;

import android.app.Activity;
import android.app.ProgressDialog;
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
    String localFolderPath = null;
    private ProgressDialog progress = null;


    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(mainContext, null,
                mainContext.getString(R.string.toast_waitforsync));
        super.onPreExecute();
    }

    SyncFiles(Context appContext, String localFolder) {
        mainContext = appContext;
        localFolderPath = localFolder;
    }

    private void connectToFTP() throws IOException {
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

    private void SyncLocalFolderToFTP(String localFolderPath) throws IOException {
        File coreDir = new File(localFolderPath);
        if (!coreDir.exists()) {
            boolean state = coreDir.mkdirs();
            if (state == false)
                throw new IOException();
        }

        FTPFile[] ftpFileList = connectionFTP.listFiles();
        File[] localFileList = coreDir.listFiles();
        // Check and create dirs
        compareLocalWithRemoteFolders(localFolderPath, localFileList, ftpFileList);

        // Check files in concrete dir
        for (FTPFile remoteDir : ftpFileList) {
            compareLocalWithRemoteFiles(connectionFTP.listFiles(remoteDir.getName()),
                    localFolderPath + File.separator + remoteDir.getName(), remoteDir.getName());
        }
        isSyncedCorrectly = true;
    }

    private void compareLocalWithRemoteFiles(FTPFile[] ftpFileList, String localFolderPath,
                                             String remoteFolderName) throws IOException {
        if (ftpFileList != null && ftpFileList.length > 0) {
            for (FTPFile ftpfile : ftpFileList) {
                if (ftpfile.isFile()) {
                    File targetFile = new File(localFolderPath + File.separator + ftpfile.getName());
                    if (targetFile.exists()) {
                        //Compare remote and local file
                        if (targetFile.length() != ftpfile.getSize()) {
                            targetFile.delete();
                        }
                        else
                            continue;
                    }
                    FileOutputStream output;
                    output = new FileOutputStream(targetFile);
                    //get the file from the remote system
                    connectionFTP.retrieveFile(File.separator + remoteFolderName + File.separator + ftpfile.getName(), output);
                    //close output stream
                    output.close();
                }
            }
        }
        File[] localFileList = new File(localFolderPath).listFiles();
        compareFilesCount(localFileList, ftpFileList);
    }

    private void compareLocalWithRemoteFolders(String localFolderPath, File[] localFoldersList, FTPFile[] ftpFoldersList) throws IOException {
        if (ftpFoldersList != null && ftpFoldersList.length > 0) {
            for (FTPFile ftpDir : ftpFoldersList) {
                if (ftpDir.isDirectory()) {
                    File localDir = new File(localFolderPath + File.separator + ftpDir.getName());
                    if (!localDir.exists())
                        localDir.mkdir();
                }
            }
        }
        compareFilesCount(localFoldersList, ftpFoldersList);
    }

    // Checking folders count on local and remote dirs
    // Needs for deleting extent files
    private void compareFilesCount(File[] localFileList, FTPFile[] ftpFileList) {
        if (ftpFileList.length != localFileList.length) {
            for (File localFile : localFileList) {
                boolean remoteFileExists = false;
                for (FTPFile ftpFile: ftpFileList) {
                    String localFileName = localFile.getName();
                    String ftpFileName = ftpFile.getName();
                    if (localFileName.equals(ftpFileName))
                        remoteFileExists = true;
                }
                if (remoteFileExists == false)
                    localFile.delete();
            }
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        connectionFTP = new FTPClient();
        try {
            connectToFTP();
            SyncLocalFolderToFTP(localFolderPath);
        } catch (IOException e) {
            isSyncedCorrectly = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progress.dismiss();
        if (!isSyncedCorrectly)
            Toast.makeText(mainContext, R.string.toast_syncerror, Toast.LENGTH_SHORT).show();
        else if (!isConnectedToFTP)
            Toast.makeText(mainContext, R.string.toast_servererror, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(mainContext, R.string.toast_synccomplete, Toast.LENGTH_SHORT).show();
    }
}
