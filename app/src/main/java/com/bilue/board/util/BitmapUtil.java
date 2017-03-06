package com.bilue.board.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

public class BitmapUtil {

    public BitmapUtil() {
        // TODO Auto-generated constructor stub
    }

    public static ByteArrayOutputStream bitmap2output(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, quality, baos);
        // InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return baos;
    }

    public static InputStream bitmap2input(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    public static Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static byte[] Bit2Byte(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            //Log.i("test", "进入方法了"+b.toString());
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        //Log.i("test", "没进入方法");
        return null;
    }


    public static boolean saveBitmap2file(Bitmap bmp, String filename) {

        String path = Environment.getExternalStorageDirectory().toString();

        File file ;
        String myDir = path+"/board/img/";
        file = new File(myDir);

        if(!file.exists()){
            file.mkdirs();
        }

        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(myDir + filename + ".jpg");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return bmp.compress(format, quality, stream);

    }


    public static boolean saveBitmaps2file(ArrayList<Bitmap> bitmaps,String mypath) {

        String path = Environment.getExternalStorageDirectory().toString();

        File file ;
        String myDir = path+"/board/miss/"+mypath+"/";
        file = new File(myDir);

        if(!file.exists()){
            file.mkdirs();
        }

        for(int i=0; i<bitmaps.size();i++){
            int b = i+1;
            Bitmap bmp = bitmaps.get(i);
            CompressFormat format = CompressFormat.JPEG;
            int quality = 100;
            OutputStream stream = null;
            try {

                stream = new FileOutputStream(myDir + b + ".jpg");
                 bmp.compress(format, quality, stream);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }



        }


        return true;



    }





}
