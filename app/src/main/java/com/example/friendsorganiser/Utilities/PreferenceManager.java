package com.example.friendsorganiser.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static PreferenceManager instance;

    public static PreferenceManager getInstance(){
        if (instance == null){
            instance = new PreferenceManager();
        }
        return instance;
    }

    public void init(Context context){
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putBoolean(String key, boolean value){
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value){
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key){
        return sharedPreferences.getString(key, null);
    }

    public void clear(){
        editor.clear();
        editor.apply();
    }
}
