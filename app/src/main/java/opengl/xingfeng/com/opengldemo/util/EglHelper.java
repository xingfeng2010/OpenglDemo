package opengl.xingfeng.com.opengldemo.util;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.view.Surface;



public class EglHelper {
    private static final String TAG = "EglHelper";
    private EGLDisplay mEGLDisplay;
    private EGLContext mEGLContext;
    private EGLSurface mEGLSurface;


    public void initEgl(Surface surface, EGLContext eglContext) {
        //1. 得到Egl实例
        //EGL14 = (EGL14) EGLContext.getEGL();

        //2. 得到默认的显示设备（就是窗口）
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //3. 初始化默认显示设备
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEGLDisplay, version,0, version, 1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //4. 设置显示设备的属性
        int[] attrib_list = new int[]{
                EGL14.EGL_RED_SIZE, mRedSize,
                EGL14.EGL_GREEN_SIZE, mGreenSize,
                EGL14.EGL_BLUE_SIZE, mBlueSize,
                EGL14.EGL_ALPHA_SIZE, mAlphaSize,
                EGL14.EGL_DEPTH_SIZE, mDepthSize,
                EGL14.EGL_STENCIL_SIZE, mStencilSize,
                EGL14.EGL_RENDERABLE_TYPE, mRenderType,//egl版本  2.0
                EGL14.EGL_NONE};


        int[] num_config = new int[1];
        if (!EGL14.eglChooseConfig(mEGLDisplay, attrib_list, 0, null, 0, 1,
                num_config, 0)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = num_config[0];
        if (numConfigs <= 0) {
            throw new IllegalArgumentException(
                    "No configs match configSpec");
        }

        //5. 从系统中获取对应属性的配置
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!EGL14.eglChooseConfig(mEGLDisplay, attrib_list, 0, configs, 0, numConfigs,
                num_config, 0)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }
        //EGLConfig eglConfig = chooseConfig(EGL14, mEGLDisplay, configs);
        EGLConfig eglConfig = configs[0];

        //6. 创建EglContext
        int[] contextAttr = new int[]{
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        if (eglContext == null) {
            mEGLContext =  EGL14.eglCreateContext(mEGLDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttr, 0);
        } else {
            mEGLContext = EGL14.eglCreateContext(mEGLDisplay, eglConfig, eglContext, contextAttr, 0);
        }

        //7. 创建渲染的Surface
        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };
        mEGLSurface = EGL14.eglCreateWindowSurface(mEGLDisplay, eglConfig, surface, surfaceAttribs, 0);

        //8. 绑定EglContext和Surface到显示设备中
        if (!EGL14.eglMakeCurrent(mEGLDisplay, mEGLSurface, mEGLSurface, mEGLContext)) {
            throw new RuntimeException("eglMakeCurrent fail");
        }
    }


    //9. 刷新数据，显示渲染场景
    public boolean swapBuffers() {
        return EGL14.eglSwapBuffers(mEGLDisplay, mEGLSurface);
    }

    public void destoryEgl() {
        if (mEGLSurface != null && mEGLSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglMakeCurrent(mEGLDisplay, EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);

            EGL14.eglDestroySurface(mEGLDisplay, mEGLSurface);
            mEGLSurface = null;
        }


        if (mEGLContext != null) {
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext);
            mEGLContext = null;
        }


        if (mEGLDisplay != null) {
            EGL14.eglTerminate(mEGLDisplay);
            mEGLDisplay = null;
        }

    }


    public EGLContext getEglContext() {
        return mEGLContext;
    }

    private final int mRedSize = 8;
    private final int mGreenSize = 8;
    private final int mBlueSize = 8;
    private final int mAlphaSize = 8;
    private final int mDepthSize = 8;
    private final int mStencilSize = 8;
    private final int mRenderType = 4;

    private EGLConfig chooseConfig(EGL14 egl, EGLDisplay display,
                                   EGLConfig[] configs) {
        for (EGLConfig config : configs) {
            int d = findConfigAttrib(egl, display, config,
                    EGL14.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttrib(egl, display, config,
                    EGL14.EGL_STENCIL_SIZE, 0);
            if ((d >= mDepthSize) && (s >= mStencilSize)) {
                int r = findConfigAttrib(egl, display, config,
                        EGL14.EGL_RED_SIZE, 0);
                int g = findConfigAttrib(egl, display, config,
                        EGL14.EGL_GREEN_SIZE, 0);
                int b = findConfigAttrib(egl, display, config,
                        EGL14.EGL_BLUE_SIZE, 0);
                int a = findConfigAttrib(egl, display, config,
                        EGL14.EGL_ALPHA_SIZE, 0);
                if ((r == mRedSize) && (g == mGreenSize)
                        && (b == mBlueSize) && (a == mAlphaSize)) {
                    return config;
                }
            }
        }
        return null;
    }

    private int findConfigAttrib(EGL14 egl, EGLDisplay display,
                                 EGLConfig config, int attribute, int defaultValue) {
        int[] value = new int[1];
        if (EGL14.eglGetConfigAttrib(display, config, attribute, value, 0)) {
            return value[0];
        }
        return defaultValue;
    }
}
