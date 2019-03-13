package com.kickstart.utils;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.kickstart.ApplicationClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.kickstart.constants.AppheartKt.CAMERA_FACING;
import static com.kickstart.constants.AppheartKt.stFlashMode;
import static com.kickstart.constants.AppheartKt.yyyyMMdd_HHmmssSSS;

/**
 * Created by sotsys-055 on 3/7/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public Camera camera;
    SurfaceHolder surfaceHolder;
    public Camera.PictureCallback rawCallback;
    public Camera.ShutterCallback shutterCallback;
    public Camera.PictureCallback jpegCallback;
    int currentCameraId = ApplicationClass.app.getPrefs().getIntDetail(CAMERA_FACING);
    private MediaRecorder recorder;
    private CamcorderProfile camcorderProfile;


    public CameraPreview(Context context) {
        super(context);
    }

    public void captureImage() {
        // TODO Auto-generated method stub
        try {
            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Camera.Size start_camera(SurfaceView cameraView) {
        Camera.Size previewSize = null;

        surfaceHolder = cameraView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        rawCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.d("Log", "onPictureTaken - raw");
            }
        };

        try {
            camera = Camera.open(currentCameraId);
        } catch (RuntimeException e) {
            Log.e("CAMERA VIEW", "init_camera: " + e);
            return null;
        }

        try {


            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            parameters.setFlashMode(ApplicationClass.app.getPrefs().getBooleanDetail(stFlashMode)
                    ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
            parameters.setJpegQuality(100);

            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

            float closestRatio = Float.MAX_VALUE;

            int targetPreviewWidth = cameraView.getWidth();
            int targetPreviewHeight = cameraView.getHeight();

            previewSize = getOptimalPreviewSize(previewSizes, targetPreviewWidth, targetPreviewHeight);

            parameters.setPreviewSize(previewSize.width, previewSize.height);
            parameters.getSupportedPictureSizes();
            List<Camera.Size> sizeList = parameters.getSupportedPictureSizes();
            Camera.Size bestDimens = null;

            if (sizeList != null && sizeList.size() > 0) {
                for (Camera.Size dimens : sizeList) {
                    if (dimens.width <= 1024 && dimens.height <= 768) {
                        if (bestDimens == null || (dimens.width > bestDimens.width && dimens.height > bestDimens.height)) {
                            bestDimens = dimens;
                        }
                    }
                }
                if (bestDimens != null)
                    parameters.setPictureSize(bestDimens.width, bestDimens.height);
                else
                    parameters.setPictureSize(sizeList.get(0).width, sizeList.get(0).height);
            }

            camera.setParameters(parameters);
            camera.setDisplayOrientation(0);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
            //camera.takePicture(shutter, raw, jpeg)
        } catch (Exception e) {
            Log.e("CAMERA VIEW", "init_camera: " + e);
            return null;
        }

        return previewSize;
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void stop_camera() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
            }

            if (recorder != null)
                recorder.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (camera != null)
            camera.startPreview();
        // TODO Auto-generated method stub
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }


    public void switchCamera(SurfaceView surfaceView) {
        stop_camera();
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        ApplicationClass.app.getPrefs().setIntDetail(CAMERA_FACING, currentCameraId);

        start_camera(surfaceView);
    }

    String generateFileName() {
        String timeStamp = new SimpleDateFormat(yyyyMMdd_HHmmssSSS).format(new Date());
        return "VID_" + timeStamp;
    }

}

