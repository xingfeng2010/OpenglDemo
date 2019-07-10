package opengl.xingfeng.com.opengldemo.beautycamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;

import opengl.xingfeng.com.opengldemo.R;

public class BeautyCamera extends AppCompatActivity implements SurfaceCreateCallback{
    private Camera mCamera;
    private CustomSurfaceView customSurfaceView;
    private AppCompatSeekBar appCompatSeekBar;
    private Camera1Renderer camera1Renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_camera);
        customSurfaceView = (CustomSurfaceView) findViewById(R.id.mSurface);
        appCompatSeekBar = (AppCompatSeekBar) findViewById(R.id.mSeek);

        camera1Renderer = new Camera1Renderer(this);
        customSurfaceView.setRender(camera1Renderer);

        camera1Renderer.setSurfaceCreateCallback(this);
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
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mCamera = Camera.open();
       // mController.setImageDirection(cameraId);
        Camera.Size size = mCamera.getParameters().getPreviewSize();
       // camera1Renderer.setDataSize(size.height, size.width);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    customSurfaceView.requestRender();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    public void onClick(View view) {
    }
}
