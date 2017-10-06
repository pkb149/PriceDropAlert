package com.pkb149.pricedropalert.Utility;

/**
 * Created by CoderGuru on 01-10-2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "loginInfo";

    private static final String isloggedIn = "IsLoggedIn";
    private static final String isFirstTimeLaunch="IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLoggedIn(String users_tableId) {
        editor.putString(isloggedIn, users_tableId);
        editor.commit();
    }

    public String getUserssId() {
        return pref.getString(isloggedIn, null);
    }
    public void clearLoggedIn(){
        editor.putString(isloggedIn, null);
        editor.commit();
    }

    public boolean isLoggedIn() {

        Log.e("login preference value",": "+pref.getString(isloggedIn,null));
        return !(pref.getString(isloggedIn, null)==null);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(isFirstTimeLaunch, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(isFirstTimeLaunch, true);
    }

}