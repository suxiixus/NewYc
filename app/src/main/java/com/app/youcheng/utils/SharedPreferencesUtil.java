package com.app.youcheng.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.youcheng.GlobalConstant;
import com.app.youcheng.MyApplication;
import com.google.gson.Gson;


public class SharedPreferencesUtil {
    private static final String FILE_NAME = "setting";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences mPreferences;
    private static SharedPreferencesUtil sharedPreferencesUtil;

    public static final String SP_KEY_LANGUAGE = "SP_KEY_LANGUAGE";
    public static final String SP_KEY_MONEY = "SP_KEY_MONEY";
    private static final String SP_KEY_LOCK_PWD = "SP_KEY_LOCK_PWD";

    private SharedPreferencesUtil(Context context) {
        mPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public static SharedPreferencesUtil getInstance() {
        if (sharedPreferencesUtil == null) {
            sharedPreferencesUtil = new SharedPreferencesUtil(MyApplication.getApplication());
        }
        return sharedPreferencesUtil;
    }

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    public void setParam(String key, Object object) {
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = MyApplication.getApplication().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(object);
            editor.putString(key, json);
        }
        editor.commit();
    }

    /**
     * 获取SharedPreferences中保存的数据
     *
     * @param key          键
     * @param defaultValue 获取失败默认值
     * @return 从SharedPreferences读取的数据
     */
    public static Object getData(String key, Object defaultValue) {
        SharedPreferences sp = MyApplication.getApplication().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        Object result;
        String type = defaultValue.getClass().getSimpleName();
        try {
            switch (type) {
                case "Boolean":
                    result = sp.getBoolean(key, (Boolean) defaultValue);
                    break;
                case "Long":
                    result = sp.getLong(key, (Long) defaultValue);
                    break;
                case "Float":
                    result = sp.getFloat(key, (Float) defaultValue);
                    break;
                case "String":
                    result = sp.getString(key, (String) defaultValue);
                    break;
                case "Integer":
                    result = sp.getInt(key, (Integer) defaultValue);
                    break;
                default:
                    Gson gson = new Gson();
                    String json = sp.getString(key, "");
                    if (!json.equals("") && json.length() > 0) {
                        result = gson.fromJson(json, defaultValue.getClass());
                    } else {
                        result = defaultValue;
                    }
                    break;
            }
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * boolean类型返回值
     */
    public boolean getBooleanParam(String key) {
        if (StringUtils.isNotEmpty(key) && key.equals(GlobalConstant.IS_FIRST_LOGIN))
            return mPreferences.getBoolean(key, true);
        else if (StringUtils.isNotEmpty(key) && key.equals(GlobalConstant.SP_KEY_MONEY_SHOW_TYPE))
            return mPreferences.getBoolean(key, true);
        else if (StringUtils.isNotEmpty(key) && key.equals(GlobalConstant.WAKE_UP))
            return mPreferences.getBoolean(key, false);
        else if (StringUtils.isNotEmpty(key) && key.equals(GlobalConstant.SP_KEY_IS_NEED_SHOW_LOCK))
            return mPreferences.getBoolean(key, false);
        return mPreferences.getBoolean(key, false);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear().commit();
    }

    /**
     * 清除指定数据
     */
    public void clearAll(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 保存货币偏好  // 1 CNY  2 USD  3 HKH
     */
    public void saveMoneyCode(int moneyCode) {
        if (mPreferences == null) return;
        setParam(SP_KEY_MONEY, moneyCode);
    }

    /**
     * 获取货币
     */
    public int getMoneyCode() {
        return mPreferences == null ? 1 : mPreferences.getInt(SP_KEY_MONEY, 1);
    }

    /**
     * 保存语言偏好  // 1 中文  2 英文  3 韩语
     */
    public void saveLanguageCode(int languageCode) {
        if (mPreferences == null) return;
        setParam(SP_KEY_LANGUAGE, languageCode);
    }

    /**
     * 获取语言偏好
     */
    public int getLanguageCode() {
        return mPreferences == null ? 1 : mPreferences.getInt(SP_KEY_LANGUAGE, 1);
    }

    /**
     * 保存手势密码
     */
    public synchronized void saveLockPwd(String encryPas) {
        SharedPreferencesUtil.getInstance().setParam(GlobalConstant.WAKE_UP, false);

        if (mPreferences == null) return;
        mPreferences.edit().putString(SP_KEY_LOCK_PWD, encryPas).apply();
    }

    /**
     * 获取手势密码
     */
    public synchronized String getLockPwd() {
        return mPreferences == null ? null : mPreferences.getString(SP_KEY_LOCK_PWD, null);
    }


}
