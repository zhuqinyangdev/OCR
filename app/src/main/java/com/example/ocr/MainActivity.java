package com.example.ocr;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.ocr.service.OCRService;
import com.example.ocr.unit.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY="9D2v6lq4mI4ygXGvYzK36Kbu";
    public static final String SECRET_KEY="ladX15ihU2qnF3V5IlNRXK4ri67MFpT6";

    private String Access_Token;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getJurisdiction();
        try {
            new OCRService(handler).start();
            //Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getJurisdiction(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }

    private void init(){
        editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(MainActivity.this,"未获得Access Token",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"请求获得Access Token失败",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(MainActivity.this,"获得Access Token成功",Toast.LENGTH_SHORT).show();
                    Access_Token=(String)msg.obj;
                    editor.putString("AccessToken",Access_Token);
                    Toast.makeText(MainActivity.this,Access_Token,Toast.LENGTH_SHORT).show();
                    Log.d("MainActivity",Access_Token);
                    break;
                default:
                    break;
            }
        }
    };

}
