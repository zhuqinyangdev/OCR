package com.example.ocr.unit;

import android.content.Context;

import java.io.File;

/**
 * Created by ZQY on 2017/9/6.
 */

public class FileUtil {
    public static File getSaveFile(Context context,String name) {
        File file = new File(context.getFilesDir().getAbsolutePath(), name);
        return file;
    }
}

