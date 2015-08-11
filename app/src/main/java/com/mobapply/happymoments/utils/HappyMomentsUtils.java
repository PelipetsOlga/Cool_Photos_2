package com.mobapply.happymoments.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Olga on 11.08.2015.
 */
public class HappyMomentsUtils {

    public static final String EXTRA_ID="extra_id";


    public static String getNewDirectoryPath(long date) {
        File root = Environment.getExternalStorageDirectory();
        String path = "directory" + date;
        File file = new File(root, path);
        file.mkdirs();
        return file.getAbsolutePath();
    }

}


