package com.siddharth.sonicopy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

//https://stackoverflow.com/questions/48034748/how-to-monitor-folder-for-file-changes-in-background
public class FileObserverService extends Service {
    String mSource,mBackup;
    private FileObserver mImagesObserver,mVideoObserver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSource=intent.getStringExtra("Source");
        mBackup=intent.getStringExtra("Backup");

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSource=DataVariables.Source;
        mBackup=DataVariables.Backup;
        //Images
        mImagesObserver = new FileObserver(mSource+ File.separator + "Media/Whatsapp Images") {
            @Override
            public void onEvent(int event, String path) {
                // If an event happens we can do stuff inside here
                // for example we can send a broadcast message with the event-id
                Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex


                Replicate replicate=new Replicate();
                File yoursrc = new File(mSource+ File.separator + "Media/Whatsapp Images");
                File yourbackup = new File(mBackup + File.separator + "WhatsApp Images");
                replicate.ProcessFiles(yoursrc,yourbackup);
            }
        };
        mImagesObserver.startWatching(); // The FileObserver starts watching

        //Videos
        mVideoObserver = new FileObserver(mSource+ File.separator + "Media/WhatsApp Video") {
            @Override
            public void onEvent(int event, String path) {
                // If an event happens we can do stuff inside here
                // for example we can send a broadcast message with the event-id
                Log.d("FILEOBSERVER_EVENT", "Event with id " + Integer.toHexString(event) + " happened"); // event identifies the occured Event in hex

                Replicate replicate=new Replicate();

                File yoursrc = new File(mSource+ File.separator + "Media/WhatsApp Video");
                File yourbackup = new File(mBackup + File.separator + "WhatsApp Video");
                replicate.ProcessFiles(yoursrc,yourbackup);
            }
        };
        mVideoObserver.startWatching(); // The FileObserver starts watching
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }
}
