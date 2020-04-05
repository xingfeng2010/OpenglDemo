package opengl.xingfeng.com.opengldemo.camera;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.util.Arrays;

import opengl.xingfeng.com.opengldemo.R;

import static android.hardware.camera2.CameraDevice.TEMPLATE_PREVIEW;

public class CameraPreviewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private CameraDevice mDevice;
    private CameraManager mCameraManager;
    private Size mPreviewSize;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Handler mHandler;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);

        mCameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
        mTextureView = this.findViewById(R.id.texture_preview);
        mTextureView.setSurfaceTextureListener(this);

        mHandler = new Handler();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void openCamera2(final SurfaceTexture surfaceTexture) {
        try {
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
            StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            //自定义规则，选个大小
            mPreviewSize = sizes[0];
            // mController.setDataSize(mPreviewSize.getHeight(),mPreviewSize.getWidth());
            //render.setPreViewSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());
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
                                        try {
                                            session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                                @Override
                                                public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
                                                    super.onCaptureProgressed(session, request, partialResult);
                                                }

                                                @Override
                                                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                                    super.onCaptureCompleted(session, request, result);
                                                    //customSurfaceView.requestRender();
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
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera2(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }
}
