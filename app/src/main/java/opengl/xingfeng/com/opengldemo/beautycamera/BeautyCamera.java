package opengl.xingfeng.com.opengldemo.beautycamera;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
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
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cgfay.facedetect.engine.FaceTracker;
import com.cgfay.facedetect.listener.FaceTrackerCallback;
import com.cgfay.landmark.LandmarkEngine;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.beautycamera.setting.CameraSettingParam;
import opengl.xingfeng.com.opengldemo.record.CameraHelper;
import opengl.xingfeng.com.opengldemo.recorder.AudioParams;
import opengl.xingfeng.com.opengldemo.recorder.HWMediaRecorder;
import opengl.xingfeng.com.opengldemo.recorder.MediaMuxerWrapper;
import opengl.xingfeng.com.opengldemo.recorder.OnRecordStateListener;
import opengl.xingfeng.com.opengldemo.recorder.RecordInfo;
import opengl.xingfeng.com.opengldemo.recorder.SpeedMode;
import opengl.xingfeng.com.opengldemo.recorder.VideoParams;
import opengl.xingfeng.com.opengldemo.util.BitmapUtils;
import opengl.xingfeng.com.opengldemo.util.PathConstraints;
import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar;
import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar.RecordSpeed;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class BeautyCamera extends AppCompatActivity implements SurfaceCreateCallback, FpsUpdateCallback,
        RecordSpeedLevelBar.OnSpeedChangedListener, TakePictureCallback, OnRecordStateListener, PreviewCallback, FaceTrackerCallback {
    private Camera mCamera;
    private CustomSurfaceView customSurfaceView;
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

    // 音视频参数
    private VideoParams mVideoParams;
    private AudioParams mAudioParams;
    private HWMediaRecorder mHWMediaRecorder;

    private ImageReader mImageReader;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_camera);
        customSurfaceView = (CustomSurfaceView) findViewById(R.id.mSurface);
        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.mSeek);
        mFpsTextView = (TextView) findViewById(R.id.fps_tv);
        mRecordSpeedLevelBar = findViewById(R.id.record_speed_bar);
        customSurfaceView.getContext();

        mRecordSpeedLevelBar.setOnSpeedChangedListener(this);


        mCameraSettingParam = new CameraSettingParam();
        render = new CameralRenderer(this, mCameraSettingParam);
        //customSurfaceView.setEGLContextClientVersion(2);
        customSurfaceView.setRender(render);
        customSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        //customSurfaceView.setPreserveEGLContextOnPause(true);

        // 视频录制器
        mVideoParams = new VideoParams();
        mAudioParams = new AudioParams();
        mHWMediaRecorder = new HWMediaRecorder(this);
        mHWMediaRecorder.setEnableAudio(true);
        mVideoParams.setVideoPath(PathConstraints.getVideoTempPath(this));
        mAudioParams.setAudioPath(PathConstraints.getVideoTempPath(this));

        Log.i("DEBUG_TEST", "video path:" + PathConstraints.getVideoTempPath(this));

        mVideoParams.setMaxDuration(5 * 1000 * 1000);
        mAudioParams.setMaxDuration(5 * 1000 * 1000);

        cameraHelper = new CameraHelper(this);
        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        mThread = new HandlerThread("camera2 ");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());

        render.setSurfaceCreateCallback(this);
        render.setFpsUpdateCallback(this);
        render.setTakePictureCallback(this);
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

        // 初始化检测器
        FaceTracker.getInstance()
                .setFaceCallback(this)
                .previewTrack(true)
                .initTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //customSurfaceView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁人脸检测器
        FaceTracker.getInstance().destroyTracker();
        // 清理关键点
        LandmarkEngine.getInstance().clearAll();

        if (mHWMediaRecorder != null) {
            mHWMediaRecorder.release();
            mHWMediaRecorder = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
//        openCamera(surfaceTexture);
        openCamera2(surfaceTexture);
//
//        previewAngle(this);
    }

    public void openCamera(SurfaceTexture surfaceTexture) {
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
            mCamera.setDisplayOrientation(90);

            surfaceTexture.setOnFrameAvailableListener((SurfaceTexture texture) -> {
                customSurfaceView.requestRender();
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
                mRecordSpeedLevelBar.setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
                break;
            case R.id.mShutter:
                //mCameraSettingParam.setTakePicture(true);
                if (!mHWMediaRecorder.isRecording()) {
                    MediaMuxerWrapper mediaMuxer = null;
                    mediaMuxer = new MediaMuxerWrapper(mVideoParams.getVideoPath());
                    mVideoParams.setMediaMuxer(mediaMuxer);
                    mAudioParams.setMediaMuxer(mediaMuxer);
                    mediaMuxer.addEncoder();
                    mediaMuxer.addEncoder();
                    mHWMediaRecorder.startRecord(mVideoParams, mAudioParams);
                } else {
                    mHWMediaRecorder.stopRecord();
                }
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

    CaptureRequest.Builder mPreviewBuilder;

    ImageReader.OnImageAvailableListener mOnImageAvailableListener = (ImageReader reader) -> {
        Log.i("DEBUG_TEST", "因为Camera2并没有Camera1的Priview回调");
        Image img = reader.acquireNextImage();
        /**
         *  因为Camera2并没有Camera1的Priview回调！！！
         *  所以该怎么能到预览图像的byte[]呢？
         *  就是在这里了！！！我找了好久的办法！！！
         **/
        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        img.close();

        FaceTracker.getInstance()
                .trackFace(data, mPreviewSize.getWidth(), mPreviewSize.getHeight());
    };

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
            mVideoParams.setVideoSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            render.setPreViewSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
            mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mDevice = camera;
                    try {
                        Surface surface = new Surface(surfaceTexture);
                        // 设置捕获请求为预览，这里还有拍照啊，录像等
                        mPreviewBuilder = mDevice.createCaptureRequest(TEMPLATE_PREVIEW);
                        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());


                        //      就是在这里，通过这个set(key,value)方法，设置曝光啊，自动聚焦等参数！！ 如下举例：
                        //      mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        mImageReader = ImageReader.newInstance(customSurfaceView.getWidth(), customSurfaceView.getHeight(), ImageFormat.JPEG, 2);
                        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);

                        // 这里一定分别add两个surface，一个Textureview的，一个ImageReader的，如果没add，会造成没摄像头预览，或者没有ImageReader的那个回调！！
                        mPreviewBuilder.addTarget(surface);
                        mPreviewBuilder.addTarget(mImageReader.getSurface());

                        mDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new
                                CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(CameraCaptureSession session) {
                                        mCameraCaptureSession = session;
                                        try {
                                            updatePreview(session);
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

    /**
     * 没有这个回调，预揽图像出不来
     */
    CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            customSurfaceView.requestRender();
        }
    };

    private void updatePreview(CameraCaptureSession session) throws CameraAccessException {
        session.setRepeatingRequest(mPreviewBuilder.build(), mCaptureCallback, mHandler);
    }

    @Override
    public void fpsUpdate(float fps) {
        runOnUiThread(() -> {
            mFpsTextView.setText("FPS: " + fps);
        });
    }

    @Override
    public void onSpeedChanged(RecordSpeed speed) {
        mCameraSettingParam.setSpeed(speed);
        SpeedMode speedMode = SpeedMode.MODE_NORMAL;
        if (speed == RecordSpeed.SPEED_L0) {
            speedMode = SpeedMode.MODE_EXTRA_SLOW;
        } else if (speed == RecordSpeed.SPEED_L1) {
            speedMode = SpeedMode.MODE_SLOW;
        } else if (speed == RecordSpeed.SPEED_L2) {
            speedMode = SpeedMode.MODE_NORMAL;
        } else if (speed == RecordSpeed.SPEED_L3) {
            speedMode = SpeedMode.MODE_FAST;
        } else if (speed == RecordSpeed.SPEED_L4) {
            speedMode = SpeedMode.MODE_EXTRA_FAST;
        }

        mAudioParams.setSpeedMode(speedMode);
        mVideoParams.setSpeedMode(speedMode);
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

    @Override
    public void onTakePicture(final ByteBuffer buffer, final int width, final int height) {
        new Thread(() -> {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = BitmapUtils.rotateBitmap(bitmap, 180, true);
            bitmap = BitmapUtils.flipBitmap(bitmap, true);
            saveBitmap(bitmap);
            bitmap.recycle();
        }).start();
    }


    protected String getSD() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //图片保存
    public void saveBitmap(Bitmap b) {
        String path = getSD() + "/OpenGLDemo/photo/";
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            runOnUiThread(() -> {
                Toast.makeText(BeautyCamera.this, "无法保存照片", Toast.LENGTH_SHORT).show();
            });
            return;
        }
        long dataTake = System.currentTimeMillis();
        final String jpegName = path + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            Toast.makeText(BeautyCamera.this, "保存成功->" + jpegName, Toast.LENGTH_SHORT).show();
        });

    }

    public void onBindSharedContext() {
        mVideoParams.setEglContext(customSurfaceView.getEglContext());
    }


    public void onRecordFrameAvailable(int texture, long timestamp) {
        mHWMediaRecorder.frameAvailable(texture, timestamp);
    }

    @Override
    public void onRecordStart() {

    }

    @Override
    public void onRecording(long duration) {

    }

    @Override
    public void onRecordFinish(RecordInfo info) {

    }

    @Override
    public void onTrackingFinish() {
        customSurfaceView.requestRender();
    }

    @Override
    public void onPreviewFrame(byte[] data) {

    }
}