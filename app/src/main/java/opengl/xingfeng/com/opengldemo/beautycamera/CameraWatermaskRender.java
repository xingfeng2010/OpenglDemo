package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;

public class CameraWatermaskRender implements CustomSurfaceView.Render {
    private int inputTexture;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private CameraWatermaskProgram mCameraWatermaskProgram;

    private int width, height;


    public CameraWatermaskRender(Context context) {
        mCameraWatermaskProgram = new CameraWatermaskProgram(context);

    }

    @Override
    public void onSurfaceCreated() {
        //启用透明
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mCameraWatermaskProgram.init();

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //宽高
       //GLES20.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;

        deleteFrameBuffer();

        GLES20.glGenFramebuffers(1, fFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, fTexture,0,GLES20.GL_RGBA,width,height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        //宽高
        mCameraWatermaskProgram.setTextureId(inputTexture);
        mCameraWatermaskProgram.draw(width, height);
        EasyGlUtils.unBindFrameBuffer();
    }


    public void setInputTexture(int inputTexture) {
        this.inputTexture = inputTexture;
    }

    public int getOnputTextureId() {
        return fTexture[0];
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture,0);
    }

    public void setMatrix(float[] matrix) {
        mCameraWatermaskProgram.setMatrix(matrix);
    }
}
