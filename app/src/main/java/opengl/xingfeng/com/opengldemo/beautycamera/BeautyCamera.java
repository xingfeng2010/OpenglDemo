package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import java.io.IOException;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.record.CameraHelper;

import static opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView.RENDERMODE_CONTINUOUSLY;

public class BeautyCamera extends AppCompatActivity implements SurfaceCreateCallback{
    private Camera mCamera;
    private CustomSurfaceView customSurfaceView;
    private AppCompatSeekBar appCompatSeekBar;
    private Camera1Renderer render;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_camera);
        customSurfaceView = (CustomSurfaceView) findViewById(R.id.mSurface);
        customSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.mSeek);

        render = new Camera1Renderer(this);
        customSurfaceView.setRender(render);

        render.setSurfaceCreateCallback(this);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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
    public void surfaceCreated(SurfaceTexture surfaceTexture) {
        openCamera(surfaceTexture);
//        try {
//            mCamera = getCameraInstance();
//            mCamera.setPreviewTexture(surfaceTexture);
//            mCamera.setDisplayOrientation(90);
//            mCamera.startPreview();
//        } catch (IOException e) {
//
//        }
    }

    public void openCamera(SurfaceTexture surfaceTexture) {
        CameraHelper helper = new CameraHelper(this);
        helper.startCamera(surfaceTexture, 0);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    customSurfaceView.requestRender();
                }
            });

//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
//        mCamera = Camera.open();
//       // mController.setImageDirection(cameraId);
//        Camera.Size size = mCamera.getParameters().getPreviewSize();
//        camera1Renderer.setDataSize(size.width, size.height);
//        try {
//            mCamera.setPreviewTexture(surfaceTexture);
//            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
//                @Override
//                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//                    customSurfaceView.requestRender();
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mCamera.startPreview();
    }

    public void onClick(View view) {
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
}
