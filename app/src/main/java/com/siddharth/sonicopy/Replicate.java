package com.siddharth.sonicopy;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Replicate {
    public void ProcessFiles(File yoursrc, File yourbackup){

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();

        for (File f : yoursrc.listFiles()){
            if (f.isFile()){
                String name = f.getName();
                Date lastModDate = new Date(f.lastModified());
                if(isSameDay(lastModDate,today)){
                    try {
                        File chk=new File(yourbackup+ File.separator + f.getName());
                        if(!chk.exists())
                            copyOrMoveFile(f, yourbackup,true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("myfiles",name+" "+lastModDate);
                }
            }
        }
    }
    private static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
    private void copyOrMoveFile(File src, File dest,boolean isCopy) throws IOException {
        File newFile = new File(dest, src.getName());
        FileChannel outChannel = null;
        FileChannel inputChannel = null;
        try {
            outChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(src).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outChannel);
            inputChannel.close();
            if(!isCopy)src.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
    private void DeleteOldFiles(File file,int days){
        days=3;
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        Date lastModDate = new Date(file.lastModified());
        long time_difference = today.getTime() - lastModDate.getTime();
        // Calucalte time difference in days
        long days_difference = (time_difference / (1000*60*60*24)) % 365;
        if(days_difference>days)
            file.delete();
    }
}
