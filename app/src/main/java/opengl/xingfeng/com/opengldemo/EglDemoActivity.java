package opengl.xingfeng.com.opengldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.util.VideoSupportUtil;

public class EglDemoActivity extends AppCompatActivity implements SurfaceHolder.Callback, Runnable, View.OnTouchListener{

    private SurfaceView mSurfaceView;
    private boolean mIsRendering = false;

    private final Object mRenderLock = new Object();
    private GL10 mGLES;
    private float red = 0.2f, green = 0.3f, blue = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl_demo);
        mSurfaceView = (SurfaceView)this.findViewById(R.id.surfaceview);

        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.setOnTouchListener(this);

        VideoSupportUtil.listSupportType();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        synchronized (mRenderLock) {
            mIsRendering = true;
            new Thread(this).start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        synchronized (mRenderLock) {
            mIsRendering = false;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        red = motionEvent.getX() / view.getWidth();
        green = motionEvent.getY() / view.getHeight();
        blue = 1.0f;
        return true;
    }

    @Override
    public void run() {
        //Init
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay disp = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        egl.eglInitialize(disp, new int[2]);

        //Config
        EGLConfig[] configs = new EGLConfig[1];
        egl.eglChooseConfig(disp,new int[] {EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE}, configs, 1, new int[1]);
        EGLConfig config = configs[0];
        //Create surface and bind with native windowing
        EGLSurface surf = egl.eglCreateWindowSurface(disp, config, mSurfaceView.getHolder(), null);
        //Bind with OpenGL context
        EGLContext context = egl.eglCreateContext(disp, config, EGL10.EGL_NO_CONTEXT, null);
        egl.eglMakeCurrent(disp, surf, surf, context);
        mGLES = (GL10) context.getGL();
        while (true) {
            synchronized (mRenderLock) {
                if (!mIsRendering) {
                    break;
                }
            }
            render(mGLES);
            egl.eglSwapBuffers(disp, surf);
        }
        egl.eglMakeCurrent(disp, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        egl.eglDestroyContext(disp, context);
        egl.eglDestroySurface(disp, surf);
        mGLES = null;
    }

    private void render(GL10 gl) {
        gl.glClearColor(red, green, blue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
}
