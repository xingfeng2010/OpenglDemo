package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.EffectFilter;
import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.ShowScreenRender;
import opengl.xingfeng.com.opengldemo.render.FrameRateMeter;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class CameralRenderer implements GLSurfaceView.Renderer {
    private Context mContext;
    private SurfaceCreateCallback mSurfaceCreateCallback;
    private FpsUpdateCallback mFpsUpdateCallback;

    private EffectFilter mEffectFilter;
    private BeautyRender beautyRender;
    private ShowScreenRender showScreenRender;

    private float[] SM = new float[16];                           //用于绘制到屏幕上的变换矩阵
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式

    int previewWidth, previewHeight;
    int screenWidth, screenHeight;

    private FrameRateMeter mFrameRateMeter;

    public CameralRenderer(Context context) {
        mContext = context;

        mEffectFilter = new EffectFilter(context);
        beautyRender = new BeautyRender(context);
        showScreenRender = new ShowScreenRender(context);

        mFrameRateMeter = new FrameRateMeter();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig var2) {
        mEffectFilter.onSurfaceCreated();
        beautyRender.onSurfaceCreated();
        showScreenRender.onSurfaceCreated();
        if (mSurfaceCreateCallback != null) {
            mSurfaceCreateCallback.surfaceCreated(mEffectFilter.getSurfaceTexture());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        screenWidth = width;
        screenHeight = height;

        MatrixUtils.getMatrix(SM, mShowType, previewWidth, previewHeight, width, height);
        showScreenRender.onSurfaceChanged(width, height);
        showScreenRender.setMatrix(SM);
        mEffectFilter.onSurfaceChanged(previewWidth, previewHeight);
        beautyRender.onSurfaceChanged(previewWidth, previewHeight);
        showScreenRender.onSurfaceChanged(previewWidth, previewHeight);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mEffectFilter.onDrawFrame();
        beautyRender.setInputTexture(mEffectFilter.getOnputTextureId());
        beautyRender.onDrawFrame();
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        showScreenRender.setMatrix(SM);
        showScreenRender.setInputTexture(beautyRender.getOnputTextureId());
        showScreenRender.onDrawFrame();

        updateFps();
    }

    private void updateFps() {
        if (mFpsUpdateCallback != null) {
            mFrameRateMeter.drawFrameCount();
            mFpsUpdateCallback.fpsUpdate(mFrameRateMeter.getFPS());
        }
    }


    public void setSurfaceCreateCallback(SurfaceCreateCallback surfaceCreateCallback) {
        this.mSurfaceCreateCallback = surfaceCreateCallback;
    }

    /**
     * 初始化矩阵
     */
    public void resetMatirx() {
        //初始化
        mEffectFilter.resetMatirx();
    }


    /**
     * 旋转
     *
     * @param angle
     * @param x
     * @param y
     * @param z
     */
    public void setAngle(float angle, float x, float y, float z) {
        //旋转
        mEffectFilter.setAngle(angle, x, y, z);
    }

    public void setIntensity(float value) {
        beautyRender.setIntensity(value);
    }

    public void setFlag(int flag) {
        beautyRender.setFlag(flag);
    }

    public void setPreViewSize(int previewWidth, int previewHeight) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }

    public void setFpsUpdateCallback(FpsUpdateCallback callback) {
        mFpsUpdateCallback = callback;
    }
}
