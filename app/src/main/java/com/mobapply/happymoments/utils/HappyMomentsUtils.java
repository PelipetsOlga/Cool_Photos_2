package com.mobapply.happymoments.utils;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.media.ExifInterface;
        import android.net.Uri;
        import android.os.Environment;
        import android.util.DisplayMetrics;
        import android.view.Display;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;

/**
 * Created by Olga on 11.08.2015.
 */
public class HappyMomentsUtils {


    public static String getNewDirectoryPath(long date) {
        File root = Environment.getExternalStorageDirectory();
        String path = "HappyMoments/directory" + date;
        File file = new File(root, path);
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static File generateCaptureFile(String albumPath) {
        File f = new File(getAlbumDirectory(albumPath), "photo_"
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

    public static void rotateAndSaveCapture(String filePath, Activity ctx){
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
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
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
        } catch (IOException e1) { }

        if (rotate != 0) {
            // Display display = getWindowManager().getDefaultDisplay();
            int w = b.getWidth();
            int h = b.getHeight();
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);
            try {
                b = Bitmap.createBitmap(b, 0, 0, w, h, mtx, false);
                b = b.copy(Bitmap.Config.ARGB_8888, true);
            } catch (IllegalArgumentException e) { }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            if (out != null)
                out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


}


