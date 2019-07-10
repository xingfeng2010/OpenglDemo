package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.util.MatrixUtils;
import opengl.xingfeng.com.opengldemo.util.TextureHelper;

public class Camera1Renderer implements CustomSurfaceView.Render {
    private int mShowType= MatrixUtils.TYPE_CENTERCROP;          //输出到屏幕上的方式
    private float[] mMVPMatrix = new float[16];
    private float[] mTextureMatrix = new float[16];
    private BeautyShaderProgram beautyShaderProgram;
    private Context mContext;
    private int mTexture;
    private SurfaceTexture mSurfacTexture;
    private SurfaceCreateCallback mSurfaceCreateCallback;

    private ShowFilter showFilter;
    private EffectFilterRender effectFilterRender;


    public Camera1Renderer(Context context) {
        mContext = context;

        effectFilterRender = new EffectFilterRender(context);
        showFilter = new ShowFilter(context);
    }

    public int getTexture() {
        return mTexture;
    }

    @Override
    public void onSurfaceCreated() {
        beautyShaderProgram = new BeautyShaderProgram(mContext);
        GLES20.glClearColor(255.0f, 255.0f, 255.0f, 1.0f);



        mTexture = TextureHelper.loadTexture();
        mSurfacTexture = new SurfaceTexture(mTexture);
        if (mSurfaceCreateCallback != null) {
            mSurfaceCreateCallback.surfaceCreated(mSurfacTexture);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        MatrixUtils.getMatrix(mMVPMatrix, mShowType, 720, 1280, width, height);


        showFilter.setSize(width, height);
        showFilter.setMatrix(mMVPMatrix);
        effectFilterRender.setSize(mDataSize.x,mDataSize.y);
        showFilter.setSize(mDataSize.x,mDataSize.y);
    }

    @Override
    public void onDrawFrame() {
        if(mSurfacTexture!=null) {
            mSurfacTexture.updateTexImage();
        }

        beautyShaderProgram.useProgram();
        mSurfacTexture.getTransformMatrix(mTextureMatrix);
        beautyShaderProgram.setUniforms();
        beautyShaderProgram.setMatrix(mMVPMatrix, mTextureMatrix);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        GLES20.glUniform1i(beautyShaderProgram.getTextureUnitLocation(), 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        beautyShaderProgram.afterDraw();
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

    }


    public void setSurfaceCreateCallback(SurfaceCreateCallback surfaceCreateCallback) {
        this.mSurfaceCreateCallback = surfaceCreateCallback;
    }
}
