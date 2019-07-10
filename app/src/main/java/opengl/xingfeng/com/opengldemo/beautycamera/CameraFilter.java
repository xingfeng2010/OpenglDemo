package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES20;

public class CameraFilter implements CustomSurfaceView.Render{
    private float[] textureMatrix;
    private int textureId;
    private float[] matrix;

    private BeautyShaderProgram beautyShaderProgram;

    public CameraFilter(Context context) {
        beautyShaderProgram = new BeautyShaderProgram(context);
    }


    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame() {
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

    public void setSize(int width, int height) {
    }

    public void setTextureMatrix(float[] textureMatrix) {
        this.textureMatrix = textureMatrix;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
