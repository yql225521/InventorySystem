package com.test.inventorysystem.zxing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;

/**
 * Created by youmengli on 6/15/16.
 */

public class AmbientLightManager implements SensorEventListener {
    private static final float TOO_DARK_LUX = 45.0f;
    private static final float BRIGHT_ENOUGH_LUX = 450.0f;

    private final Context context;
    private CameraManager cameraManager;

    /**
     * 光传感器
     */
    private Sensor lightSensor;

    public AmbientLightManager(Context context) {
        this.context = context;
    }

    /**
     * 该方法会在周围环境改变后回调，然后根据设置好的临界值决定是否打开闪光灯
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float ambientLightLux = sensorEvent.values[0];
        if (cameraManager != null) {
            if (ambientLightLux <= TOO_DARK_LUX) {
                cameraManager.setTorch(true);
            }
            else if (ambientLightLux >= BRIGHT_ENOUGH_LUX) {
                cameraManager.setTorch(false);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }
}
