package com.app.youcheng.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.app.youcheng.GlobalConstant;

import java.io.File;
import java.io.IOException;


/**
 * Created by wonderful on 2017/7/17.
 */

public class FileUtils {

    public static File getMyFile(Context context, String filename) {
        File file = new File(GlobalConstant.myDir, filename);

        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static void deleteMyFile(String filename) {
        File file = new File(GlobalConstant.myDir, filename);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 判断是否有SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable();
    }

    public static File getLongSaveFile(Context context, String dirname, String filename) {
        File file;
        if (hasSDCard()) {
            file = new File(context.getExternalFilesDir(dirname), filename);
        } else {
            file = new File(context.getFilesDir(), filename);
        }
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getLongSaveDir(Context context, String dirname) throws IOException {
        File file;
        if (hasSDCard()) {
            file = context.getExternalFilesDir(dirname);
        } else {
            file = context.getFilesDir();
        }
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (!file.exists()) file.createNewFile();
        return file;
    }

//    public static File getCacheSaveFile(Context context, String filename) {
//        File file;
//        if (hasSDCard()) {
//            file = new File(context.getExternalCacheDir(), filename);
//        } else {
//            file = new File(context.getCacheDir(), filename);
//        }
//        if (!file.getParentFile().exists()) {
//            file.mkdirs();
//        }
//        if (file.exists()) {
//            file.delete();
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return file;
//    }

//    public static File getCacheSaveDir(Context context) throws IOException {
//        File file;
//        if (hasSDCard()) {
//            file = context.getExternalCacheDir();
//        } else {
//            file = context.getCacheDir();
//        }
//        if (!file.getParentFile().exists()) {
//            file.mkdirs();
//        }
//        if (!file.exists()) file.createNewFile();
//        return file;
//    }

//    public static File getCommonPathFile(Context context, String filename) {
//        File file = new File(Environment.getExternalStorageDirectory() + filename);
//        if (file.isDirectory())
//            return null;
//        if (file.exists())
//            file.delete();
//        if (!file.getParentFile().exists())
//            file.mkdirs();
//        try {
//            file.createNewFile();
//            return file;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static Uri getUriForFile(Context context, File file) {
        return Build.VERSION.SDK_INT >= 24 ? getUriForFile24(context, file) : Uri.fromFile(file);
    }

    public static Uri getUriForFile24(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    /**
     * 得到json文件中的内容
     *
     * @param context
     * @param fileName
     * @return
     */
//    public static String getJson(Context context, String fileName) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        AssetManager assetManager = context.getAssets();
//        BufferedReader bufferedReader = null;
//        try {
//            bufferedReader = new BufferedReader(new InputStreamReader(
//                    assetManager.open(fileName), "utf-8"));
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuilder.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (bufferedReader != null)
//                bufferedReader.close();
//        }
//        return stringBuilder.toString();
//    }
}
