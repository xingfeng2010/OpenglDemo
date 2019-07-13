package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.record.CameraHelper;

import static opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView.RENDERMODE_CONTINUOUSLY;

public class BeautyCamera extends AppCompatActivity implements SurfaceCreateCallback{
    private Camera mCamera;
    private CustomSurfaceView customSurfaceView;
    private AppCompatSeekBar appCompatSeekBar;
    private CameralRenderer render;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private CameraHelper cameraHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_camera);
        customSurfaceView = (CustomSurfaceView) findViewById(R.id.mSurface);
        customSurfaceView.setRenderMode(RENDERMODE_CONTINUOUSLY);
        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.mSeek);

        render = new CameralRenderer(this);
        customSurfaceView.setRender(render);
        cameraHelper = new CameraHelper(this);

        previewAngle(this);
        render.setSurfaceCreateCallback(this);
        appCompatSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                render.setIntensity(progress/100f);
                render.setFlag(progress/20+1);
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
    }

    public void openCamera(SurfaceTexture surfaceTexture) {
        cameraHelper.startCamera(surfaceTexture,cameraId);
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    customSurfaceView.requestRender();
                }
            });
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        previewAngle(this);
    }
}
