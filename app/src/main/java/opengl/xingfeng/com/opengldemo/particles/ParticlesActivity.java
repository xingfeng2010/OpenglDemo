package opengl.xingfeng.com.opengldemo.particles;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.render.TextureRender;

public class ParticlesActivity extends AppCompatActivity implements View.OnTouchListener{

    private GLSurfaceView mGLSurfaceView;
    float previousX, previousY;
    private ParticlesRender mParticleRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mParticleRender = new ParticlesRender(this);
        mGLSurfaceView.setRenderer(mParticleRender);
        mGLSurfaceView.setOnTouchListener(this);
        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent != null) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                previousX = motionEvent.getX();
                previousY = motionEvent.getY();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                final float deltX = motionEvent.getX() - previousX;
                final float deltY = motionEvent.getY() - previousY;

                previousX = motionEvent.getX();
                previousY = motionEvent.getY();

                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mParticleRender.handleTouchDrag(deltX, deltY);
                    }
                });
            }

            return true;
        }
        return false;
    }
}
