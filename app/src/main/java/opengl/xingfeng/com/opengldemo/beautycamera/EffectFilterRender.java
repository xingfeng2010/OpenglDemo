package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;

public class EffectFilterRender implements CustomSurfaceView.Render{
    private CameraFilter cameraFilter;
    private int width=0;
    private int height=0;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int[] mCameraTexture=new int[1];

    private float[] mTextureMatrix = new float[16];

    private SurfaceTexture mSurfaceTexture;


    public EffectFilterRender(Context context) {
        cameraFilter = new CameraFilter(context);
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    @Override
    public void onSurfaceCreated() {
        init();
    }

    public void setMatrix(float[] matrix) {
        cameraFilter.setMatrix(matrix);
    }

    private void init() {
        GLES20.glGenTextures(1,mCameraTexture,0);
        mSurfaceTexture=new SurfaceTexture(mCameraTexture[0]);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        cameraFilter.setSize(width, height);
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
            
            deleteFramebuffer();
            GLES20.glGenFramebuffers(1, fFrame, 0);
            EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        }
    }

    /**
     * 这个方法至关重要！！！
     * @return
     */
    public int getOutputTexture() {
        return fTexture[0];
    }

    private void deleteFramebuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture, 0);
    }

    @Override
    public void onDrawFrame() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTextureMatrix);
            cameraFilter.setTextureMatrix(mTextureMatrix);
        }

        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES20.glViewport(0, 0, width, height);
        cameraFilter.setTextureId(mCameraTexture[0]);
        cameraFilter.onDrawFrame();
        EasyGlUtils.unBindFrameBuffer();
    }

    public void setSize(int x, int y) {
    }
}
