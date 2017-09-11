package com.example.ocr;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.example.ocr.camera.Camera;
import com.example.ocr.service.OCRService;
import com.example.ocr.unit.HttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String API_KEY="ge4pt1lqsGDz5865aG9ohTvX";
    public static final String SECRET_KEY="dOSL3knxp7m67zgfuyLer7ZmQIljtqaG";

    private String Access_Token;
    private boolean hasGotToken=false;

    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private File finishPic;

    private Button commonWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getJurisdiction();
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
        initAccessTokenWithAkSk();
        editor=PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        finishPic=new File(getExternalCacheDir().getAbsolutePath(),"finishPic.jpg");
        commonWord=(Button)findViewById(R.id.common_word);
        initButton();
    }
    private void initButton(){
        commonWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasGotToken==true){
                    Intent common=new Intent(MainActivity.this,CommonWord.class);
                    startActivity(common);
                }else{
                    Toast.makeText(MainActivity.this,"初始化未完成",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void initAccessTokenWithAkSk() {
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("AccessToken",token);
                editor.apply();
                Log.d("MainActivity","获取成功");
                hasGotToken=true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.d("MainActivity","获取失败");
            }
        }, getApplicationContext(), API_KEY, SECRET_KEY);
    }


}
