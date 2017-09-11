package com.example.ocr.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.example.ocr.R;
import com.example.ocr.unit.FileUtil;
import com.example.ocr.unit.HttpUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Camera extends AppCompatActivity {

    private static final int CAMERA_INTENT_REQUEST=0;
    private static final int IMAGE_CROP_CODE=1;
    public static final String RETURN="return";

    private Uri imageUri;
    private Uri tempUri;
    private File picFile;
    private File tempPicfile;
    private File finishPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        String path=getIntent().getExtras().getString("path");
        Log.d("Camera",path);
        picFile=new File(path,"Pic.jpg");
        tempPicfile=new File(path,"tempPic.jpg");
        finishPic=new File(path,"finishPic.jpg");
        openCamera();
    }

    private void openCamera(){
        try{
            if (picFile.exists()){
                picFile.delete();
            }
            picFile.createNewFile();
            if(Build.VERSION.SDK_INT>=24){
                imageUri= FileProvider.getUriForFile(Camera.this,"com.example.cameraalbumtest.fileprovider",picFile);
            }else{
                imageUri= Uri.fromFile(picFile);
            }
            Intent cameraActivity=new Intent("android.media.action.IMAGE_CAPTURE");
            cameraActivity.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(cameraActivity,CAMERA_INTENT_REQUEST);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CAMERA_INTENT_REQUEST:
                try {
                    if (tempPicfile.exists()){
                        tempPicfile.delete();
                    }
                    tempPicfile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    tempUri= FileProvider.getUriForFile(Camera.this,"com.example.cameraalbumtest.fileprovider",tempPicfile);
                }else{
                    tempUri= Uri.fromFile(tempPicfile);
                }
                Intent intent = new Intent("com.android.camera.action.CROP");
                if (imageUri==null){
                    Log.d("MainActivity","imageUri为空");
                }
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                startActivityForResult(intent, Camera.IMAGE_CROP_CODE);
                break;
            case Camera.IMAGE_CROP_CODE:

                try {
                    Bitmap bitmap=BitmapFactory.decodeFile(tempPicfile.getAbsolutePath());
                    FileOutputStream out = null;
                    out = new FileOutputStream(finishPic);
                    compressImage(bitmap).compress(Bitmap.CompressFormat.JPEG, 100, out);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public  Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;

        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
        }catch(NullPointerException npe){
            npe.printStackTrace();
        }

    }
}
