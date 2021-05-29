package com.siddharth.sonicopy;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    private static final int READ_EXTERNAl_STORAGE = 100;
    EditText txtSource,txtBackup;
    Context context;
    SharedPreferences sharedPref;
    Switch swService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtSource=(EditText)findViewById(R.id.edSource);
        txtBackup=(EditText)findViewById(R.id.edBackup);
        context = getBaseContext();
        sharedPref = context.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        swService=(Switch)findViewById(R.id.switch1);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, READ_EXTERNAl_STORAGE);
        }
            CreateDirectoryIfNotExist();

            String src = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "WhatsApp";
            String backup = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SoniCopy";

            txtSource.setText(sharedPref.getString("source",src));
            txtBackup.setText(sharedPref.getString("backup",backup));


        boolean isServiceRunning = isMyServiceRunning(FileObserverService.class);
        swService.setChecked(isServiceRunning);

        swService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Intent intent = new Intent(context, FileObserverService.class);
                    intent.putExtra("Source", txtSource.getText().toString());
                    intent.putExtra("Backup", txtBackup.getText().toString());
                    DataVariables.Source= txtSource.getText().toString();
                    DataVariables.Backup= txtBackup.getText().toString();
                    context.startService(intent);
                }
                boolean isServiceRunning = isMyServiceRunning(FileObserverService.class);
                swService.setChecked(isServiceRunning);
            }
        });
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void Manual_Click(View v){
        v.setBackgroundColor(Color.rgb(255,140,0));//Dark Orange
        //Save Path
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("Source",txtSource.getText().toString());
        editor.putString("Backup",txtBackup.getText().toString());
        editor.commit();

        Replicate replicate=new Replicate();
        //Images
        File yoursrc = new File(txtSource.getText().toString()+ File.separator + "Media/Whatsapp Images");
        File yourbackup = new File(txtBackup.getText().toString() + File.separator + "WhatsApp Images");
        replicate.ProcessFiles(yoursrc,yourbackup);

        //Videos
        yoursrc = new File(txtSource.getText().toString()+ File.separator + "Media/WhatsApp Video");
        yourbackup = new File(txtBackup.getText().toString() + File.separator + "WhatsApp Video");
        replicate.ProcessFiles(yoursrc,yourbackup);

        v.setBackgroundColor(Color.rgb(0,128,128));//Teal
    }
    public void Service_Click(View v){
        Intent intent = new Intent(this, FileObserverService.class);
        intent.putExtra("Source", txtSource.getText().toString());
        intent.putExtra("Backup", txtBackup.getText().toString());
        DataVariables.Source= txtSource.getText().toString();
        DataVariables.Backup= txtBackup.getText().toString();
        this.startService(intent);

        boolean isServiceRunning = isMyServiceRunning(FileObserverService.class);
        swService.setChecked(isServiceRunning);
    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        boolean isServiceRunning = isMyServiceRunning(FileObserverService.class);
        swService.setChecked(isServiceRunning);
    }
    private void CreateDirectoryIfNotExist(){
        File SoniCopy = new File(txtBackup.getText().toString());
        if (!SoniCopy.exists())
            SoniCopy.mkdirs();

        File WhatsappImages = new File(txtBackup.getText().toString()+ File.separator + "WhatsApp Images");
        if (!WhatsappImages.exists())
            WhatsappImages.mkdirs();

        File WhatsappVideo= new File(txtBackup.getText().toString()+ File.separator + "WhatsApp Video");
        if (!WhatsappVideo.exists())
         WhatsappVideo.mkdirs();
    }
}
