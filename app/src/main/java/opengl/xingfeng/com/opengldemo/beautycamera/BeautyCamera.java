package opengl.xingfeng.com.opengldemo.beautycamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.beautycamera.setting.CameraSettingParam;
import opengl.xingfeng.com.opengldemo.record.CameraHelper;
import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar;
import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar.RecordSpeed;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView.RENDERMODE_CONTINUOUSLY;

public class BeautyCamera extends AppCompatActivity implements SurfaceCreateCallback,FpsUpdateCallback, RecordSpeedLevelBar.OnSpeedChangedListener {
    private Camera mCamera;
    private GLSurfaceView customSurfaceView;
    private AppCompatSeekBar appCompatSeekBar;
    private CameralRenderer render;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private CameraHelper cameraHelper;


    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private Size mPreviewSize;
    private HandlerThread mThread;
    private Handler mHandler;
    private TextView mFpsTextView;
    private RecordSpeedLevelBar mRecordSpeedLevelBar;

    private CameraSettingParam mCameraSettingParam;

    private CameraCaptureSession mCameraCaptureSession;

    private SurfaceTexture mSurfaceTexture;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_camera);
        customSurfaceView = (GLSurfaceView) findViewById(R.id.mSurface);
        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.mSeek);
        mFpsTextView = (TextView) findViewById(R.id.fps_tv);
        mRecordSpeedLevelBar = findViewById(R.id.record_speed_bar);

        mRecordSpeedLevelBar.setOnSpeedChangedListener(this);


        mCameraSettingParam = new CameraSettingParam();
        render = new CameralRenderer(this);
        customSurfaceView.setEGLContextClientVersion(2);
        customSurfaceView.setRenderer(render);
        customSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        customSurfaceView.setPreserveEGLContextOnPause(true);

        cameraHelper = new CameraHelper(this);
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        //previewAngle(this);
        render.setSurfaceCreateCallback(this);
        render.setFpsUpdateCallback(this);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                render.setIntensity(progress / 100f);
                render.setFlag(progress / 20 + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        customSurfaceView.onResume();
    }

    @Override
    public void surfaceCreated(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        //openCamera(surfaceTexture);
        openCamera2(surfaceTexture);
    }

    public void openCamera(SurfaceTexture surfaceTexture) {
//        cameraHelper.startCamera(surfaceTexture,cameraId);
//        render.setPreViewSize(cameraHelper.getPreviewWidth(), cameraHelper.getPreviewHeight());
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera = Camera.open(cameraId);
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        render.setPreViewSize(size.height, size.width);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    customSurfaceView.requestRender();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_switch:
                cameraId = cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT ?
                        Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
                openCamera2(mSurfaceTexture);
                break;
            case R.id.speed_switch:
                int visibility = mRecordSpeedLevelBar.getVisibility();
                mRecordSpeedLevelBar.setVisibility(visibility == View.GONE ? View.VISIBLE: View.GONE);
                break;
        }
    }

    public void previewAngle(Context context) {
        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        render.resetMatirx();
        switch (angle) {
            case Surface.ROTATION_0:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    render.setAngle(90, 0, 0, 1);
                    render.setAngle(180, 1, 0, 0);
                } else {
                    render.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_90:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    render.setAngle(180, 0, 0, 1);
                    render.setAngle(180, 0, 1, 0);
                } else {
                    render.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    render.setAngle(90f, 0f, 0f, 1f);
                    render.setAngle(180, 0, 1f, 0f);
                } else {
                    render.setAngle(-90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    render.setAngle(180f, 0, 1f, 0f);
                } else {
                    render.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
       // previewAngle(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openCamera2(final SurfaceTexture surfaceTexture) {
        try {
            closeCamera2();

            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
            StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            //自定义规则，选个大小
            mPreviewSize = sizes[0];
            // mController.setDataSize(mPreviewSize.getHeight(),mPreviewSize.getWidth());
            render.setPreViewSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mDevice = camera;
                    try {
                        Surface surface = new Surface(surfaceTexture);
                        final CaptureRequest.Builder builder = mDevice.createCaptureRequest
                                (TEMPLATE_PREVIEW);
                        builder.addTarget(surface);
                        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                        mDevice.createCaptureSession(Arrays.asList(surface), new
                                CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        mCameraCaptureSession = session;
                                        try {
                                            session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                                    super.onCaptureProgressed(session, request, partialResult);
                                                }

                                                @Override
                                                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                    super.onCaptureCompleted(session, request, result);
                                                    customSurfaceView.requestRender();
                                                }
                                            }, mHandler);
                                        } catch (CameraAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(CameraCaptureSession session) {

                                    }
                                }, mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    mDevice = null;
                }

                @Override
                public void onError(CameraDevice camera, int error) {

                }
            }, mHandler);
        } catch (SecurityException | CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fpsUpdate(float fps) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFpsTextView.setText("FPS: " + fps);
            }
        });
    }

    @Override
    public void onSpeedChanged(RecordSpeed speed) {
        mCameraSettingParam.setSpeed(speed);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCamera2() {
        if (mDevice != null) {
            mDevice.close();
            mDevice = null;
        }

        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
    }
}