package com.mobapply.happymoments.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import com.mobapply.happymoments.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class HappyMomentsUtils {


    public static String getNewAlbumPath(long date) {
        File root = Environment.getExternalStorageDirectory();
        String path = Constants.APP_FOLDER + "/album" + date;
        File file = new File(root, path);
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static File generateCaptureFile(String albumPath) {
        File f = new File(getAlbumDirectory(albumPath), "picture_"
                + System.currentTimeMillis() + ".jpg");
        return f;
    }

    public static File generatePreviewFile(String albumPath) {
        File f = new File(getAlbumDirectory(albumPath), "preview_"
                + System.currentTimeMillis() + ".jpg");
        return f;
    }

    public static File getAlbumDirectory(String albumPath) {
        File dir = new File(albumPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static void rotateAndSaveCapture(String filePath, Activity ctx, String newFilepath) {
        Display display = ctx.getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        // Get the dimensions of the bitmap
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeFile(filePath, opt);

        int targetW = metricsB.widthPixels;
        int targetH = metricsB.heightPixels;
        int photoW = opt.outWidth;
        int photoH = opt.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = photoW / targetW;
        if (scaleFactor<1){
            scaleFactor=1;
        }
        // Decode the image file into a Bitmap sized to fill the View
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = scaleFactor;
        opt.inPurgeable = true;
        b = BitmapFactory.decodeFile(filePath, opt);

        ExifInterface exif = null;
        int rotate = 0;
        try {
            exif = new ExifInterface(filePath);
            int exifOrientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
        } catch (IOException e1) {
        }

        if (rotate != 0) {
            // Display display = getWindowManager().getDefaultDisplay();
            int w = b.getWidth();
            int h = b.getHeight();
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);
            try {
                b = Bitmap.createBitmap(b, 0, 0, w, h, mtx, false);
                b = b.copy(Bitmap.Config.ARGB_8888, true);
            } catch (IllegalArgumentException e) {
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(newFilepath);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            if (out != null)
                out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void generatePreview(String bifFilePath, String previewPath) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap b = BitmapFactory.decodeFile(bifFilePath, opt);

        // Determine how much to scale down the image
        int scaleFactor = 2;

        // Decode the image file into a Bitmap sized to fill the View
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = scaleFactor;
        opt.inPurgeable = true;
        b = BitmapFactory.decodeFile(bifFilePath, opt);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(previewPath);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            if (out != null)
                out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String getImagePath(Uri uri, Activity ctx) {
        if (uri == null)
            return null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = ctx.managedQuery(uri, projection, null, null,
                null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public static boolean deleteDirectory(String path) {
        File folder=new File(path);
        if( folder.exists() ) {
            File[] files = folder.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i].getAbsolutePath());
                }
                else {
                    files[i].delete();
                }
            }
        }
        return(folder.delete() );
    }




}


