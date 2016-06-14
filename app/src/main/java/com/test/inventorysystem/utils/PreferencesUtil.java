package com.test.inventorysystem.utils;

/**
 * Created by youmengli on 2016-05-31.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtil {
    private Context _Context = null;
    private android.content.ContextWrapper _ContextWrapper = null;

    public PreferencesUtil(Context c) {
        _Context = c;
        _ContextWrapper = new android.content.ContextWrapper(_Context);
    }

    public boolean Write(String Group, String Name, String Value) {
        SharedPreferences sp = _ContextWrapper.getSharedPreferences(Group,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(Name, Value);
        return editor.commit();
    }

    public String Read(String Group, String Name) {
        SharedPreferences sp = _ContextWrapper.getSharedPreferences(Group,
                Context.MODE_PRIVATE);
        return sp.getString(Name, "");
    }
}