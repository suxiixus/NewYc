package com.app.youcheng.utils;

import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.app.youcheng.entity.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 用户工具类
 */

public class UserUtils {
    /**
     * 保存
     */
    public static void saveCurrentUser(User currentUser) {
        try {
            File file = FileUtils.getLongSaveFile(MyApplication.getApplication(), GlobalConstant.UserSaveFileDirName, GlobalConstant.UserSaveFileName);
            if (file.exists()) {
                file.delete();
            }
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(currentUser);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除本地存储的文件
     */
    public static void deleteCurrentUserFile() {
        File file = FileUtils.getLongSaveFile(MyApplication.getApplication(), GlobalConstant.UserSaveFileDirName, GlobalConstant.UserSaveFileName);
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取
     */
    public static User getCurrentUserFromFile() {
        User user = null;
        try {
            File file = new File(FileUtils.getLongSaveDir(MyApplication.getApplication(), GlobalConstant.UserSaveFileDirName), GlobalConstant.UserSaveFileName);
            if (file != null && file.exists()) {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                user = (User) ois.readObject();
                if (user == null) {
                    user = new User();
                }
                ois.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

}
