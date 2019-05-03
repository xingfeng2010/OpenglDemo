package opengl.xingfeng.com.opengldemo;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import opengl.xingfeng.com.opengldemo.render.CoordinateRender;
import opengl.xingfeng.com.opengldemo.render.TextureRender;

public class Coordinate extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
//        mGLSurfaceView.setRenderer(new CoordinateRender());
        mGLSurfaceView.setRenderer(new TextureRender(this));
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
}
