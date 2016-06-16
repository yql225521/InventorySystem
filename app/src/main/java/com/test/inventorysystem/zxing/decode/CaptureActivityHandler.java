package com.test.inventorysystem.zxing.decode;

import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.test.inventorysystem.R;
import com.test.inventorysystem.activities.CaptureActivity;
import com.test.inventorysystem.zxing.CameraManager;
import com.test.inventorysystem.zxing.view.ViewfinderResultPointCallback;

import java.util.Collection;
import java.util.Map;

/**
 * Created by youmengli on 6/15/16.
 */

public class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class
            .getSimpleName();

    private final CaptureActivity activity;

    /**
     * 真正负责扫描任务的核心线程
     */
    private final DecodeThread decodeThread;

    private State state;

    private final CameraManager cameraManager;

    /**
     * 当前扫描的状态
     */
    private enum State {
        /**
         * 预览
         */
        PREVIEW,
        /**
         * 扫描成功
         */
        SUCCESS,
        /**
         * 结束扫描
         */
        DONE
    }

    public CaptureActivityHandler(CaptureActivity activity,
                                  Collection<BarcodeFormat> decodeFormats,
                                  Map<DecodeHintType, ?> baseHints, String characterSet,
                                  CameraManager cameraManager) {
        this.activity = activity;

        // 启动扫描线程
        decodeThread = new DecodeThread(activity, decodeFormats, baseHints,
                characterSet, new ViewfinderResultPointCallback(
                activity.getViewfinderView()));
        decodeThread.start();

        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;

        // 开启相机预览界面
        cameraManager.startPreview();

        restartPreviewAndDecode();
    }

    /**
     * 完成一次扫描后，只需要再调用此方法即可
     */
    private void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;

            // 向decodeThread绑定的handler（DecodeHandler)发送解码消息
            cameraManager.requestPreviewFrame(decodeThread.getHandler(),
                    R.id.decode);
            activity.drawViewfinder();
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();

        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        }
        catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

}
