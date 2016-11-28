package com.pinlogin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SessionManager {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context context;

    int PRIVATE_MODE = 0;
    private static final String IS_LOGIN = "Is_login";
    private static final String PREF_FILE = "PinLoginPref";
    private static final String KEY_PASSWORD = "password";

    public SessionManager(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_FILE, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void createLoginSession(String password) {
        editor.putBoolean(IS_LOGIN, true);
        String encryptPass = encrypt(password);
        editor.putString(KEY_PASSWORD, encryptPass);
        editor.commit();
    }

    public String getUserPassword() {
        String encryptPass = preferences.getString(KEY_PASSWORD, null);
        return decrypt(encryptPass);

    }

    public boolean isLogin() {
        return preferences.getBoolean(IS_LOGIN, false);
    }

    public void checkLogin() {
        if (this.isLogin()) {
            Intent intent = new Intent(context, NextActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static String encrypt(String input) {

        return Base64.encodeToString(input.getBytes(), Base64.DEFAULT);
    }

    public static String decrypt(String input) {
        return new String(Base64.decode(input, Base64.DEFAULT));
    }

}
