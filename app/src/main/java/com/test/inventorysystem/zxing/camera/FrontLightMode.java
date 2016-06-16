package com.test.inventorysystem.zxing.camera;

import android.content.SharedPreferences;

import com.test.inventorysystem.zxing.Config;

/**
 * Created by youmengli on 6/15/16.
 */

public enum FrontLightMode {
    /** Always on. */
    ON,
    /** On only when ambient light is low. */
    AUTO,
    /** Always off. */
    OFF;

    private static FrontLightMode parse(String modeString) {
        return modeString == null ? OFF : valueOf(modeString);
    }

    public static FrontLightMode readPref(SharedPreferences sharedPrefs) {
        return parse(sharedPrefs.getString(
                Config.KEY_FRONT_LIGHT_MODE, null));
    }

}
