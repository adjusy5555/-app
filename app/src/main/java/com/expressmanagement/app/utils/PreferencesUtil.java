package com.expressmanagement.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

    private static final String PREF_NAME = "express_management_prefs";

    // 键名常量
    private static final String KEY_REMEMBER_PASSWORD = "remember_password";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存记住密码状态和账号密码
     */
    public static void saveLoginInfo(Context context, String username, String password, boolean remember) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(KEY_REMEMBER_PASSWORD, remember);
        if (remember) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
        } else {
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
        }
        editor.apply();
    }

    /**
     * 获取是否记住密码
     */
    public static boolean isRememberPassword(Context context) {
        return getPreferences(context).getBoolean(KEY_REMEMBER_PASSWORD, false);
    }

    /**
     * 获取保存的用户名
     */
    public static String getSavedUsername(Context context) {
        return getPreferences(context).getString(KEY_USERNAME, "");
    }

    /**
     * 获取保存的密码
     */
    public static String getSavedPassword(Context context) {
        return getPreferences(context).getString(KEY_PASSWORD, "");
    }

    /**
     * 保存当前登录用户信息
     */
    public static void saveCurrentUser(Context context, int userId, int role) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putInt(KEY_USER_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * 获取当前登录用户ID
     */
    public static int getCurrentUserId(Context context) {
        return getPreferences(context).getInt(KEY_USER_ID, -1);
    }

    /**
     * 获取当前登录用户角色
     */
    public static int getCurrentUserRole(Context context) {
        return getPreferences(context).getInt(KEY_USER_ROLE, -1);
    }

    /**
     * 检查是否已登录
     */
    public static boolean isLoggedIn(Context context) {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 清除登录状态（退出登录）
     */
    public static void clearLoginStatus(Context context) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_ROLE);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    /**
     * 清除所有保存的数据
     */
    public static void clearAll(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}