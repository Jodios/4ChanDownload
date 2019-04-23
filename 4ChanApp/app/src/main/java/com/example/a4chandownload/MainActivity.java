package com.example.a4chandownload;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private Thread downloadThread;
    private Thread downloadThread1;
    private Thread downloadThread2;

    private Button buttonDownload;
    private Button buttonStop;
    private TextView textView;
    private Spinner spinnerBoards;
    private ArrayList<String> boardList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;
    private int pos = 0;
    private int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)){
            requestStoragePermission();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buttonDownload = (Button) findViewById(R.id.download_button);
        //buttonStop = (Button) findViewById(R.id.stop_button);
        textView = (TextView) findViewById(R.id.textView);
        spinnerBoards = (Spinner) findViewById(R.id.boards);
        initBoards();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, boardList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBoards.setAdapter(arrayAdapter);
        spinnerBoards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    download();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Need to be able to save pictures to phone")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initBoards(){
        boardList.add("/a");
        boardList.add("/b");
        boardList.add("/c");
        boardList.add("/d");
        boardList.add("/e");
        boardList.add("/f");
        boardList.add("/g");
        boardList.add("/gif");
        boardList.add("/h");
        boardList.add("/hr");
        boardList.add("/k");
        boardList.add("/m");
        boardList.add("/o");
        boardList.add("/p");
        boardList.add("/r");
        boardList.add("/s");
        boardList.add("/t");
        boardList.add("/u");
        boardList.add("/v");
        boardList.add("/vg");
        boardList.add("/vr");
        boardList.add("/w");
        boardList.add("/wg");
        boardList.add("/i");
        boardList.add("/ic");
        boardList.add("/r9k");
        boardList.add("/s4s");
        boardList.add("/vip");
        boardList.add("/qa");
        boardList.add("/cm");
        boardList.add("/hm");
        boardList.add("/lgbt");
        boardList.add("/y");
        boardList.add("/aco");
        boardList.add("/adv");
        boardList.add("/an");
        boardList.add("/asp");
        boardList.add("/bant");
        boardList.add("/biz");
        boardList.add("/cgl");
        boardList.add("/ck");
        boardList.add("/co");
        boardList.add("/diy");
        boardList.add("/fa");
        boardList.add("/fit");
        boardList.add("/gd");
        boardList.add("/hc");
        boardList.add("/his");
        boardList.add("/int");
        boardList.add("/jp");
        boardList.add("/lit");
        boardList.add("/mlp");
        boardList.add("/mu");
        boardList.add("/n");
        boardList.add("/news");
        boardList.add("/out");
        boardList.add("/po");
        boardList.add("/pol");
        boardList.add("/qst");
        boardList.add("/sci");
        boardList.add("/soc");
        boardList.add("/sp");
        boardList.add("/tg");
        boardList.add("/toy");
        boardList.add("/trv");
        boardList.add("/tv");
        boardList.add("/vp");
        boardList.add("/wsg");
        boardList.add("/wsr");
        boardList.add("/x");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void download() throws InterruptedException {
        String file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getParentFile().getPath();
        String message = "Downloading Everything from " + boardList.get(pos);
        textView.setText(message);
        stop();
        for (int i = 1; i < 10; i += 3) {
            downloadThread = new DownloadThread(i, boardList.get(pos), file);
            downloadThread1 = new DownloadThread(i + 1, boardList.get(pos), file);
            downloadThread2 = new DownloadThread(i + 2, boardList.get(pos), file);
            downloadThread.start();
            downloadThread1.start();
            downloadThread2.start();
        }

        Toast.makeText(this, "Downloading Everything. Click Stop to stop", Toast.LENGTH_SHORT).show();
    }

    private void stop() throws InterruptedException {
        try {
            if (downloadThread != null && downloadThread1 != null && downloadThread2 != null) {
                downloadThread.interrupt();
                downloadThread1.interrupt();
                downloadThread2.interrupt();
                downloadThread.join();
                downloadThread1.join();
                downloadThread2.join();
                downloadThread = null;
                downloadThread1 = null;
                downloadThread2 = null;
            }
        }catch (InterruptedException ie){
            ie.printStackTrace();
            textView.setText("download stopped");
        }
    }


//    private void



}
