package com.axel.datatracking;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataService extends IntentService {

    public static String packageName = "packagename";
    public static int uid = 0;
    public int toggle = 2;
    static File testFile;
    static long startingTime, startingDown, startingUp;
    static Intent mediaScannerConnection;
    static Uri fileContent;

    public DataService() {
        super("DataService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        packageName = intent.getStringExtra("PCKG");
        uid = getUid(this, packageName);
        // toggle = 0 : start
        // toggle = 1 : end
        toggle = intent.getIntExtra("TOGGLE", toggle);
        Log.d("service", "PKG name: " + packageName
                + " UID:" + uid + " Toggle:" + toggle);
        SimplifiedAppInfo selectedApp = new SimplifiedAppInfo("temp", packageName, uid);
        selectedApp.setDownbytes();
        selectedApp.setUpbyts();
        if(toggle == 0) {
            startLog(selectedApp);
        } else if(toggle == 1) {
            endLog(selectedApp);
        }
    }

    private int getUid(Context context, String name) {
        int uid = 0;
        try {
            uid = context.getPackageManager().getApplicationInfo(name, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return uid;
    }


    public void startLog(SimplifiedAppInfo selectedApp) {
        int i = 0;
        String name = "dataFile.csv";
        Log.d("update", "sort of works maybe");
        // make the file if it already exists increment the name by 1
        try {
            testFile = new File(this.getExternalFilesDir(null), name);
            while(testFile.exists()) {
                i++;
                name = this.makeFileName(i);
                testFile = new File(this.getExternalFilesDir(null), name);
            }
            Log.d("filename", name);
            testFile.createNewFile();
        } catch (IOException e) {
            Log.d("broke", "Unable to write my dood");
        }
        // try to write to the file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true));
            startingDown = selectedApp.getDownbytes();
            startingUp = selectedApp.getUpbyts();
            startingTime = System.currentTimeMillis();
            writer.write("data,up,down\n");
            writer.write("Initial,"+selectedApp.getUpbyts()+","+selectedApp.getDownbytes()+"\n");
            writer.close();
            // refresh the data file
            mediaScannerConnection = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            fileContent = Uri.fromFile(testFile);
            mediaScannerConnection.setData(fileContent);
            this.sendBroadcast(mediaScannerConnection);
        } catch (IOException e) {
            Log.d("broke", "cant write to the file");
        }
    }


    public void endLog(SimplifiedAppInfo selectedApp) {
        // write end results to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(testFile, true));
            writer.write("End,"+selectedApp.getUpbyts()+","+selectedApp.getDownbytes()+"\n");
            float effectiveDown = selectedApp.getDownbytes() - startingDown;
            float effectiveUp = selectedApp.getUpbyts() - startingUp;
            writer.write("Effective,"+effectiveUp+","+effectiveDown+"\n");
            float timePassed = ((float) ((System.currentTimeMillis() - startingTime)/1000));
            float avgUp = effectiveUp/timePassed;
            float avgDown = effectiveDown/timePassed;
            writer.write("Average bytes/sec,"+avgUp+","+avgDown+"\n");
            writer.write("Time passed,"+timePassed+","+timePassed+"\n");
            writer.close();
            mediaScannerConnection.setData(fileContent);
            this.sendBroadcast(mediaScannerConnection);
        } catch (IOException e) {
            Log.d("broke", "the write dont work");
        }
    }

    public String makeFileName(int i) {
        return "dataFile" + i + ".csv";
    }


}
