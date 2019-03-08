package com.example.bokuang.get_rotation;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileSave {
    private static final String Orientation_Angles="/Orientation_Angles";//文件夹名
    public static void saveFile(String str){
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
        long l=System.currentTimeMillis();
        String filePath;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString()
                    + Orientation_Angles
                    + File.separator
                    + "方向角数据 " +  sdf.format(new Date(l))
                    + "_test.txt";
        } else  {
            filePath = Environment.getDownloadCacheDirectory().toString()
                    + Orientation_Angles
                    + File.separator
                    + "方向角数据 " + sdf.format(new Date(l))
                    + "_test.txt";
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
