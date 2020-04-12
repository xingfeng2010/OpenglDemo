package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.beautycamera.setting.CameraSettingParam;
import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.EffectFilter;
import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.ShowScreenRender;
import opengl.xingfeng.com.opengldemo.render.FrameRateMeter;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class CameralRenderer implements GLSurfaceView.Renderer {
    private Context mContext;
    private SurfaceCreateCallback mSurfaceCreateCallback;
    private FpsUpdateCallback mFpsUpdateCallback;
    private TakePictureCallback mTakePictureCallback;

    private EffectFilter mEffectFilter;
    private BeautyRender beautyRender;
    private ShowScreenRender showScreenRender;
    private CameraWatermaskRender mCameraWatermaskRender;
    private WaterMarkRenderDrawer mWaterMarkRenderDrawer;

    private float[] SM = new float[16];                           //用于绘制到屏幕上的变换矩阵
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式

    int previewWidth, previewHeight;
    int screenWidth, screenHeight;

    private FrameRateMeter mFrameRateMeter;
    private CameraSettingParam mCameraSettingParam;

    public CameralRenderer(Context context, CameraSettingParam cameraSettingParam) {
        mContext = context;

        mEffectFilter = new EffectFilter(context);
        beautyRender = new BeautyRender(context);
        showScreenRender = new ShowScreenRender(context);
        mCameraWatermaskRender = new CameraWatermaskRender(context);
        mWaterMarkRenderDrawer = new WaterMarkRenderDrawer(context);

        mFrameRateMeter = new FrameRateMeter();

        mCameraSettingParam = cameraSettingParam;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig var2) {
        mEffectFilter.onSurfaceCreated();
        beautyRender.onSurfaceCreated();
        showScreenRender.onSurfaceCreated();
        mCameraWatermaskRender.onSurfaceCreated();
        mWaterMarkRenderDrawer.onCreated();
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
        mCameraWatermaskRender.onSurfaceChanged(previewWidth,previewHeight);
        mWaterMarkRenderDrawer.onChanged(previewWidth,previewHeight);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mEffectFilter.onDrawFrame();
        beautyRender.setInputTexture(mEffectFilter.getOnputTextureId());
        beautyRender.onDrawFrame();


        mCameraWatermaskRender.setInputTexture(beautyRender.getOnputTextureId());
        mCameraWatermaskRender.onDrawFrame();

//        mWaterMarkRenderDrawer.setInputTexture(beautyRender.getOnputTextureId());
//        mWaterMarkRenderDrawer.draw();

        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        showScreenRender.setMatrix(SM);
        showScreenRender.setInputTexture(mCameraWatermaskRender.getOnputTextureId());
        showScreenRender.onDrawFrame();

        if (mCameraSettingParam.isTakePicture()) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(screenWidth * screenHeight * 4);

            GLES20.glReadPixels(0, 0, screenWidth, screenHeight, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, buffer);
            buffer.rewind();
            mCameraSettingParam.setTakePicture(false);

            if (mTakePictureCallback != null) {
                mTakePictureCallback.onTakePicture(buffer, screenWidth, screenHeight);
            }
        }

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

    public void setTakePictureCallback(TakePictureCallback callback) {
        mTakePictureCallback = callback;
    }
}
