package com.example.ocr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.example.ocr.camera.Camera;

import java.io.File;

public class CommonWord extends AppCompatActivity {

    private OCR ocr;

    private static final int COMMON_WORD=1;

    private File finishPic;

    private EditText editText;

    private boolean hasGotToken=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_word);
        Toast.makeText(CommonWord.this,"正在初始化请稍等",Toast.LENGTH_SHORT).show();
        init();
        Toast.makeText(CommonWord.this,"初始化完成",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(CommonWord.this,Camera.class);
        intent.putExtra("path",this.getExternalCacheDir().getAbsolutePath());
        startActivityForResult(intent,COMMON_WORD);
     }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case COMMON_WORD:
                startConnect();
                break;
        }
    }
    private void initAccessTokenWithAkSk() {
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(CommonWord.this);
        String token=sharedPreferences.getString("AccessToken",null);
        ocr=OCR.getInstance();
        if (token!=null){
            ocr.initWithToken(getApplicationContext(), token);
            hasGotToken=true;
        }else{
            Toast.makeText(CommonWord.this,"程序出错!\n请尝试重新启动",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private void startConnect() {
        try {
            if (!finishPic.exists()) {
            }
            GeneralBasicParams param = new GeneralBasicParams();
            param.setDetectDirection(true);
            param.setImageFile(finishPic);

// 调用通用文字识别服务
           ocr.recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
                @Override
                public void onResult(GeneralResult result) {
                    Log.d("CommonWord", "得到结果");
                    StringBuffer sb = new StringBuffer();
                    for (WordSimple wordSimple : result.getWordList()) {
                        WordSimple word = wordSimple;
                        sb.append(word.getWords());
                        sb.append("\n");
                    }
                    editText.setText(sb.toString());
                    Log.d("CommonWord", sb.toString());
                }

                @Override
                public void onError(OCRError error) {
                    error.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {
        try {
            initAccessTokenWithAkSk();
            finishPic=new File(getExternalCacheDir().getAbsolutePath(),"finishPic.jpg");
            editText=(EditText)findViewById(R.id.common_word);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}