package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class CameraFilter implements CustomSurfaceView.Render{
    private float[] textureMatrix;
    private int textureId;
    private float[] matrix;

    private BeautyShaderProgram beautyShaderProgram;

    private Context mContext;

    public CameraFilter(Context context) {
        mContext = context;
    }


    @Override
    public void onSurfaceCreated() {
        beautyShaderProgram = new BeautyShaderProgram(mContext);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame() {
        beautyShaderProgram.setUniforms();
        beautyShaderProgram.setMatrix(matrix, textureMatrix);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
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
