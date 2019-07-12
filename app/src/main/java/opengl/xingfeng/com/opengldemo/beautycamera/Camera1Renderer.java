package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Matrix;

import opengl.xingfeng.com.opengldemo.beautycamera.showfbo.ShowFilter;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class Camera1Renderer implements CustomSurfaceView.Render {
    private int mShowType= MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    private float[] mMVPMatrix = new float[16];
    private Context mContext;
    private SurfaceCreateCallback mSurfaceCreateCallback;

    private ShowFilter showFilter;
    private EffectFilterRender effectFilterRender;
    private Point mDataSize;

    public Camera1Renderer(Context context) {
        mContext = context;

        //设置默认的DateSize，DataSize由AiyaProvider根据数据源的图像宽高进行设置
//        mDataSize=new Point(720,1280);
//        effectFilterRender = new EffectFilterRender(context);
        showFilter = new ShowFilter(context);
    }

    @Override
    public void onSurfaceCreated() {
        //effectFilterRender.onSurfaceCreated();
        showFilter.onSurfaceCreated();
        if (mSurfaceCreateCallback != null) {
            mSurfaceCreateCallback.surfaceCreated(showFilter.getSurfaceTexture());
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //GLES20.glViewport(0, 0, width, height);
        showFilter.onSurfaceChanged(width, height);
//        MatrixUtils.getMatrix(mMVPMatrix, mShowType, 720, 1280, width, height);


//        showFilter.setSize(width, height);
//        showFilter.setMatrix(mMVPMatrix);
//        effectFilterRender.setSize(mDataSize.x,mDataSize.y);
//        showFilter.setSize(mDataSize.x,mDataSize.y);

//        effectFilterRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
//        effectFilterRender.onDrawFrame();
//        mGroupFilter.setTextureId(mEffectFilter.getOutputTexture());
//        mGroupFilter.draw();

        //显示传入的texture上，一般是显示在屏幕上
/*        GLES20.glViewport(0,0,720, 1280);*/
        //showFilter.setMatrix(mMVPMatrix);
       // showFilter.setTextureId(effectFilterRender.getOutputTexture());
        showFilter.onDrawFrame();
    }


    public void setSurfaceCreateCallback(SurfaceCreateCallback surfaceCreateCallback) {
        this.mSurfaceCreateCallback = surfaceCreateCallback;
    }

    public void setDataSize(int width, int height) {
        //showFilter.setSize(width, height);
    }

    /**
     * 初始化矩阵
     */
    public void resetMatirx() {
        //初始化
        showFilter.resetMatirx();
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
        showFilter.setAngle(angle, x, y, z);
    }
}
