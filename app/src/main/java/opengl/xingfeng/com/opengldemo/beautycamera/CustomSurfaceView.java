package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.EGLContext;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;


import opengl.xingfeng.com.opengldemo.util.EglHelper;

public class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Surface mSurface;
    private EGLThread mEGLThread;
    private Render mRender;
    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;
    private int mRenderMode = RENDERMODE_WHEN_DIRTY;
    private EGLContext mEglContext;

    public CustomSurfaceView(Context context) {
        this(context, null);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mSurface == null) {
            mSurface = surfaceHolder.getSurface();
        }

        mEGLThread = new EGLThread(new WeakReference<>(this));
        mEGLThread.isCreate = true;
        mEGLThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        mEGLThread.width = width;
        mEGLThread.height = height;
        mEGLThread.isChange = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mEGLThread.onDestroy();
        mEGLThread = null;
        mSurface = null;
        mEglContext = null;
    }

    public void setRender(Render render) {
        this.mRender = render;
    }

    public void setRenderMode(int mode) {
        this.mRenderMode = mode;
    }

    public void requestRender() {
        if (mEGLThread != null) {
            mEGLThread.requestRender();
        }
    }

    public void setSurface(Surface surface) {
        this.mSurface = surface;
    //    this.mEglContext = eglContext;
    }

    public EGLContext getEglContext() {
        if (mEGLThread != null) {
            return mEGLThread.getEglContext();
        }
        return null;
    }

    private static class EGLThread extends Thread {
        private static final String TAG = "EGLThread";
        private boolean isCreate;

        private int width;
        private int height;
        private boolean isChange;
        private boolean isStart;
        private boolean isExit;
        private WeakReference<CustomSurfaceView> mEGLSurfaceViewWeakRef;
        private EglHelper mEglHelper;

        private Object object;

        public EGLThread(WeakReference<CustomSurfaceView> eglSurfaceViewWeakReference) {
            this.mEGLSurfaceViewWeakRef = eglSurfaceViewWeakReference;
        }

        @Override
        public void run() {
            super.run();
            try {
                guardedRun();
            } catch (Exception e) {
                Log.i(TAG, "guardedRun exception:" + e.toString());
            }
        }

        private void guardedRun() throws InterruptedException {
            isExit = false;
            isStart = false;
            object = new Object();
            mEglHelper = new EglHelper();
            mEglHelper.initEgl(mEGLSurfaceViewWeakRef.get().mSurface, mEGLSurfaceViewWeakRef.get().mEglContext);

            while (true) {
                if (isExit) {
                    release();
                    break;
                }

                if (isStart) {
                    if (mEGLSurfaceViewWeakRef.get().mRenderMode == RENDERMODE_WHEN_DIRTY) {
                        synchronized (object) {
                            object.wait();
                        }
                    } else if (mEGLSurfaceViewWeakRef.get().mRenderMode == RENDERMODE_CONTINUOUSLY) {
                        Thread.sleep(1000 / 60);
                    } else {
                        throw new IllegalArgumentException("rendermode");
                    }
                }

                onCreate();
                onChange(width, height);
                onDraw();
                isStart = true;
            }
        }

        private void onCreate() {
            if (!isCreate || mEGLSurfaceViewWeakRef.get().mRender == null) {
                return;
            }
            isCreate = false;
            mEGLSurfaceViewWeakRef.get().mRender.onSurfaceCreated();
        }

        private void onChange(int width, int height) {
            if (!isChange || mEGLSurfaceViewWeakRef.get().mRender == null) {
                return;
            }

            isChange = false;
            mEGLSurfaceViewWeakRef.get().mRender.onSurfaceChanged(width, height);
        }

        private void onDraw() {
            if(mEGLSurfaceViewWeakRef.get().mRender == null) {
                return;
            }

            mEGLSurfaceViewWeakRef.get().mRender.onDrawFrame();
            //第一次的时候手动调用一次，不然不会显示 UI
            if (!isStart) {
                mEGLSurfaceViewWeakRef.get().mRender.onDrawFrame();
            }

            mEglHelper.swapBuffers();
        }

        void onDestroy() {
            isExit = true;
            requestRender();
        }

        void requestRender() {
            if (object != null) {
                synchronized (object) {
                    object.notifyAll();
                }
            }
        }

        EGLContext getEglContext() {
            if (mEglHelper != null) {
                return mEglHelper.getEglContext();
            }
            return null;
        }

        void release() {
            if (mEglHelper != null) {
                mEglHelper.destoryEgl();
                mEglHelper = null;
                object = null;
                mEGLSurfaceViewWeakRef = null;
            }
        }
    }

    public interface Render {
        void onSurfaceCreated();

        void onSurfaceChanged(int width, int height);

        void onDrawFrame();
    }
}
