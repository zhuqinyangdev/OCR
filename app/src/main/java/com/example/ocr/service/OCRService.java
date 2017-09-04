package com.example.ocr.service;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.example.ocr.MainActivity;
import com.example.ocr.unit.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.widget.Toast.makeText;

/**
 * Created by ZQY on 2017/9/3.
 */

public class OCRService extends Thread {

    private Handler handler;
    private Message message;   //what:1没有获得Access Token，2是请求没有成功,3为获得Access Token成功将Access Token写在obj字段中

    public OCRService(Handler handler){
        message=new Message();
        this.handler=handler;
    }

    @Override
    public void run() {
        getAccess_Token();
    }

    private void getAccess_Token(){
        String requestUrl="https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="
                +MainActivity.API_KEY+"&client_secret="+MainActivity.SECRET_KEY;
        HttpUtil.sendOkHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                message.what=2;
                handler.sendMessage(message);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String Access_Token="";
                    String text=response.body().string();
                    Log.d("OCRServer",text);
                    JSONArray jsonArray=new JSONArray(text);
                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                    Access_Token=jsonObject.getString("access_token");
                    if (Access_Token.equals("")){
                        message.what=1;
                        handler.sendMessage(message);
                    }else{
                        message.what=3;
                        message.obj=(Object) Access_Token;
                        handler.sendMessage(message);
                        Log.d("OCRServer",Access_Token);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
