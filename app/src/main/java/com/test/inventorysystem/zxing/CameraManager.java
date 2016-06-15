package com.test.inventorysystem.zxing;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;

import com.test.inventorysystem.zxing.camera.AutoFocusManager;
import com.test.inventorysystem.zxing.camera.CameraConfigurationManager;
import com.test.inventorysystem.zxing.camera.PreviewCallback;

/**
 * Created by youmengli on 6/15/16.
 */

public class CameraManager {

    private static final int MIN_FRAME_WIDTH = 240;

    private static final int MAX_FRAME_WIDTH = 1200; // = 5/8 * 1920

    private final Context context;

    private Camera camera;

    private Rect framingRect;

    private Rect framingRectInPreview;

    private final CameraConfigurationManager configManager;

    private AutoFocusManager autoFocusManager;

    /**
     * Preview frames are delivered here, which we pass on to the registered
     * handler. Make sure to clear the handler so it will only receive one
     * message.
     */
    private final PreviewCallback previewCallback;

    public CameraManager(Context context) {
        this.context = context;
        this.configManager = new CameraConfigurationManager(context);
        previewCallback = new PreviewCallback(configManager);
    }


    /**
     * Calculates the framing rect which the UI should draw to show the user
     * where to place the barcode. This target helps with alignment as well as
     * forces the user to hold the device far enough away to ensure the image
     * will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    public synchronized Rect getFramingRect() {
        if (framingRect == null) {
            if (camera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            if (screenResolution == null) {
                // Called early, before init even finished
                return null;
            }

            int width = findDesiredDimensionInRange(screenResolution.x,
                    MIN_FRAME_WIDTH, MAX_FRAME_WIDTH);
            // 将扫描框设置成一个正方形
            int height = width;

            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            framingRect = new Rect(leftOffset, topOffset, leftOffset + width,
                    topOffset + height);

            System.out.println("Calculated framing rect: " + framingRect);
        }

        return framingRect;
    }

    /**
     * Target 5/8 of each dimension<br/>
     * 计算结果在hardMin~hardMax之间
     *
     * @param resolution
     * @param hardMin
     * @param hardMax
     * @return
     */
    private static int findDesiredDimensionInRange(int resolution, int hardMin,
                                                   int hardMax) {
        int dim = 5 * resolution / 8; // Target 5/8 of each dimension
        if (dim < hardMin) {
            return hardMin;
        }
        if (dim > hardMax) {
            return hardMax;
        }
        return dim;
    }

    /**
     * Convenience method for
     * @link org.madmatrix.zxing.android.CaptureActivity
     */
    public synchronized void setTorch(boolean newSetting) {
        if (newSetting != configManager.getTorchState(camera)) {
            if (camera != null) {
                if (autoFocusManager != null) {
                    autoFocusManager.stop();
                }
                configManager.setTorch(camera, newSetting);
                if (autoFocusManager != null) {
                    autoFocusManager.start();
                }
            }
        }
    }

}
