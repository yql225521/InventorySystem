package com.test.inventorysystem.zxing.camera;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import java.util.Collection;

/**
 * Created by youmengli on 6/15/16.
 */

public class CameraConfigurationManager {

    private Context context;

    /**
     * 屏幕分辨率
     */
    private Point screenResolution;

    /**
     * 相机分辨率
     */
    private Point cameraResolution;

    public CameraConfigurationManager(Context context) {
        this.context = context;
    }

    public Point getScreenResolution() {
        return screenResolution;
    }

    Point getCameraResolution() {
        return cameraResolution;
    }

    public boolean getTorchState(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters != null) {
                String flashMode = camera.getParameters().getFlashMode();
                return flashMode != null
                        && (Camera.Parameters.FLASH_MODE_ON.equals(flashMode) || Camera.Parameters.FLASH_MODE_TORCH
                        .equals(flashMode));
            }
        }
        return false;
    }

    public void setTorch(Camera camera, boolean newSetting) {
        Camera.Parameters parameters = camera.getParameters();
        doSetTorch(parameters, newSetting, false);
        camera.setParameters(parameters);
    }

    private void doSetTorch(Camera.Parameters parameters, boolean newSetting,
                            boolean safeMode) {
        String flashMode;
        if (newSetting) {
            flashMode = findSettableValue(parameters.getSupportedFlashModes(),
                    Camera.Parameters.FLASH_MODE_TORCH,
                    Camera.Parameters.FLASH_MODE_ON);
        }
        else {
            flashMode = findSettableValue(parameters.getSupportedFlashModes(),
                    Camera.Parameters.FLASH_MODE_OFF);
        }
        if (flashMode != null) {
            parameters.setFlashMode(flashMode);
        }
    }

    /**
     * 在supportedValues中寻找desiredValues，找不到则返回null
     *
     * @param supportedValues
     * @param desiredValues
     * @return
     */
    private static String findSettableValue(Collection<String> supportedValues,
                                            String... desiredValues) {
        String result = null;
        if (supportedValues != null) {
            for (String desiredValue : desiredValues) {
                if (supportedValues.contains(desiredValue)) {
                    result = desiredValue;
                    break;
                }
            }
        }
        return result;
    }

}
