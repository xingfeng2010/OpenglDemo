package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Point;

import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.EffectFilter;
import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.ShowScreenRender;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class CameralRenderer implements CustomSurfaceView.Render {
    private Context mContext;
    private SurfaceCreateCallback mSurfaceCreateCallback;

    private EffectFilter mEffectFilter;
    private BeautyRender beautyRender;
    private ShowScreenRender showScreenRender;

    public CameralRenderer(Context context) {
        mContext = context;

        mEffectFilter = new EffectFilter(context);
        beautyRender = new BeautyRender(context);
        showScreenRender = new ShowScreenRender(context);
    }

    @Override
    public void onSurfaceCreated() {
        mEffectFilter.onSurfaceCreated();
        beautyRender.onSurfaceCreated();
        showScreenRender.onSurfaceCreated();
        beautyRender.setInputTexture(mEffectFilter.getOnputTextureId());
        showScreenRender.setInputTexture(beautyRender.getOnputTextureId());
        if (mSurfaceCreateCallback != null) {
            mSurfaceCreateCallback.surfaceCreated(mEffectFilter.getSurfaceTexture());
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mEffectFilter.onSurfaceChanged(width, height);
        beautyRender.onSurfaceChanged(width, height);
        showScreenRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        mEffectFilter.onDrawFrame();
        beautyRender.onDrawFrame();
        showScreenRender.onDrawFrame();
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
}
