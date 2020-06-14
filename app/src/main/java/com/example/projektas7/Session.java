package com.example.projektas7;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

public class Session {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public Session(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setLoggedin(boolean loggedin) {
        editor.putBoolean("loggedInmode",loggedin);
        editor.commit();
    }

    public boolean loggedin() {
        return sharedPreferences.getBoolean("loggedInmode", false);
    }


}
