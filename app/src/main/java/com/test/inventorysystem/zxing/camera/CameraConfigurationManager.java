package com.test.inventorysystem.zxing.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.test.inventorysystem.zxing.Config;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Created by youmengli on 6/15/16.
 */

public class CameraConfigurationManager {
    private static Pattern COMMA_PATTERN = Pattern.compile(",");

    private static String TAG = "CameraConfiguration";

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


    /**
     * Reads, one time, values from the camera that are needed by the app.
     */
    public void initFromCameraParameters(Camera paramCamera) {

        Camera.Parameters localParameters = paramCamera.getParameters();

        Display localDisplay = ((WindowManager) this.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.screenResolution = new Point(localDisplay.getWidth(), localDisplay.getHeight());
        Log.d(TAG, "Screen resolution: " + this.screenResolution);
        this.cameraResolution = getCameraResolution(localParameters, this.screenResolution);
        Log.d(TAG, "Camera resolution: " + this.screenResolution);
    }


    public Point getScreenResolution() {
        return screenResolution;
    }

    private static Point getCameraResolution(Camera.Parameters paramParameters, Point paramPoint) {
        String str = paramParameters.get("preview-size-values");
        if (str == null)
            str = paramParameters.get("preview-size-value");
        Point localPoint = null;
        if (str != null) {
            Log.d(TAG, "preview-size-values parameter: " + str);
            localPoint = findBestPreviewSizeValue(str, paramPoint);
        }
        if (localPoint == null)
            localPoint = new Point(paramPoint.x >> 3 << 3, paramPoint.y >> 3 << 3);
        return localPoint;
    }

    public Point getCameraResolution() {
        return cameraResolution;
    }

    private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
        int bestX = 0;
        int bestY = 0;
        int diff = Integer.MAX_VALUE;

        int maybeOKX = 0;
        int maybeOKY = 0;
        float maybeOKScale = -1;
        for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
            previewSize = previewSize.trim();
            int dimPosition = previewSize.indexOf('x');
            if (dimPosition < 0) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newX;
            int newY;

            try {
                newX = Integer.parseInt(previewSize.substring(0, dimPosition));
                newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
            } catch (NumberFormatException nfe) {
                Log.w(TAG, "Bad preview-size: " + previewSize);
                continue;
            }

            int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
            boolean isScaleRight = ((double)newX / (double)screenResolution.y == (double)newY / (double)screenResolution.x);
            Log.d(TAG, isScaleRight + "##" + newX + " / " + screenResolution.y + " == " + newY + " / " + screenResolution.x);


            if (newDiff == 0) {
                bestX = newX;
                bestY = newY;
                break;
            } else if (isScaleRight && newDiff < diff) {
                bestX = newX;
                bestY = newY;
                diff = newDiff;
            } else {
                float scale = Math.abs((float)((float)newX / (float)newY) - ((float)screenResolution.x - (float)screenResolution.y));
                if (maybeOKScale == -1 || scale < maybeOKScale) {
                    maybeOKX = newX;
                    maybeOKY = newY;
                    maybeOKScale = scale;
                }
            }
        }

        if (bestX > 0 && bestY > 0) {
            return new Point(bestX, bestY);
        }

        //640x480,1024x768
        return new Point(maybeOKX, maybeOKY);
        //    return null;
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

    public void setDesiredCameraParameters(Camera camera, boolean safeMode) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters == null) {
            Log.w(TAG,
                    "Device error: no camera parameters are available. Proceeding without configuration.");
            return;
        }

        Log.i(TAG, "Initial camera parameters: " + parameters.flatten());

        if (safeMode) {
            Log.w(TAG,
                    "In camera config safe mode -- most settings will not be honored");
        }

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        // 初始化闪光灯
        initializeTorch(parameters, prefs, safeMode);

        // 默认使用自动对焦
        String focusMode = findSettableValue(
                parameters.getSupportedFocusModes(),
                Camera.Parameters.FOCUS_MODE_AUTO);

        // Maybe selected auto-focus but not available, so fall through here:
        if (!safeMode && focusMode == null) {
            focusMode = findSettableValue(parameters.getSupportedFocusModes(),
                    Camera.Parameters.FOCUS_MODE_MACRO,
                    Camera.Parameters.FOCUS_MODE_EDOF);
        }
        if (focusMode != null) {
            parameters.setFocusMode(focusMode);
        }

        if (prefs.getBoolean(Config.KEY_INVERT_SCAN, false)) {
            String colorMode = findSettableValue(
                    parameters.getSupportedColorEffects(),
                    Camera.Parameters.EFFECT_NEGATIVE);
            if (colorMode != null) {
                parameters.setColorEffect(colorMode);
            }
        }

        parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
        camera.setParameters(parameters);

        Camera.Parameters afterParameters = camera.getParameters();
        Camera.Size afterSize = afterParameters.getPreviewSize();
        if (afterSize != null
                && (cameraResolution.x != afterSize.width || cameraResolution.y != afterSize.height)) {
            Log.w(TAG, "Camera said it supported preview size "
                    + cameraResolution.x + 'x' + cameraResolution.y
                    + ", but after setting it, preview size is "
                    + afterSize.width + 'x' + afterSize.height);
            cameraResolution.x = afterSize.width;
            cameraResolution.y = afterSize.height;
        }

        camera.setDisplayOrientation(90);
    }

    private void initializeTorch(Camera.Parameters parameters,
                                 SharedPreferences prefs, boolean safeMode) {
        boolean currentSetting = FrontLightMode.readPref(prefs) == FrontLightMode.ON;
        doSetTorch(parameters, currentSetting, safeMode);
    }
}
