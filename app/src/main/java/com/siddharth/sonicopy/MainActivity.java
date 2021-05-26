package com.siddharth.sonicopy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final int READ_EXTERNAl_STORAGE = 100;
    private static final int WRITE_EXTERNAl_STORAGE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, READ_EXTERNAl_STORAGE);
        }else{
            String src = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "WhatsApp/Media/Whatsapp Images";
            File yoursrc = new File(src);

            String srcmedia = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "Soni/Media/Images";
            File src_dest = new File(srcmedia);



            Calendar c = Calendar.getInstance();
            Date today = c.getTime();


            for (File f : yoursrc.listFiles()) {
                if (f.isFile()){
                    String name = f.getName()+System.currentTimeMillis();
                    Date lastModDate = new Date(f.lastModified());
                    if(isSameDay(lastModDate,today)){
                        try {
                            copyOrMoveFile(f, yoursrc,true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d("myfiles",name+" "+lastModDate);
                    }
                }
            }
        }
    }

/*public void btn_Click(View v){

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sourceDir",txtSource.getText().toString());
        editor.putString("destDir",txtDestination.getText().toString());
        editor.commit();
        startService(new Intent(this, FileSystemObserverService.class));
    }*/
    private void copyOrMoveFile(File file, File dir,boolean isCopy) throws IOException {
        File newFile = new File(dir, file.getName());
        FileChannel outChannel = null;
        FileChannel inputChannel = null;
        try {
            outChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(file).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outChannel);
            inputChannel.close();
            if(!isCopy)
                file.delete();
        } finally {
            if (inputChannel != null) inputChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAl_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
                String src = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "WhatsApp/Media/Whatsapp Images";
                File yoursrc = new File(src);

                String srcmedia = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "Soni/Media/Images";
                File src_dest = new File(srcmedia);



                Calendar c = Calendar.getInstance();
                Date today = c.getTime();


                for (File f : yoursrc.listFiles()) {
                    if (f.isFile()){
                        String name = f.getName()+System.currentTimeMillis();
                        Date lastModDate = new Date(f.lastModified());
                        if(isSameDay(lastModDate,today)){
                            try {
                                copyOrMoveFile(f, yoursrc,true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Log.d("myfiles",name+" "+lastModDate);
                        }




                    }
                }
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
    // Function to check and request permission
    public void checkPermissions(String permission, int requestCode,String perm) throws IOException {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission,perm}, requestCode);
        }
        else {

            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
}
