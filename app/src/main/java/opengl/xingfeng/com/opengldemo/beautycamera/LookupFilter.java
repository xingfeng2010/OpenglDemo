package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.util.Arrays;

import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class LookupFilter implements CustomSurfaceView.Render {
    // input texture id
    private int textureId;
    private LookupShaderProgram lookupShaderProgram;
    private int[] mastTextures = new int[1];
    private float intensity;
    private Bitmap mBitmap;
    private Context mContext;

    private int textureType = 0;      //默认使用Texture2D0

    /**
     * 单位矩阵
     */
    private float[] matrix = MatrixUtils.getOriginalMatrix();

    public LookupFilter(Context context) {
        mContext = context;
        lookupShaderProgram = new LookupShaderProgram(context);
        MatrixUtils.flip(matrix,false,true);
    }

    public void setIntensity(float value) {
        this.intensity = value;
    }

    public void setMaskImage(String mask) {
        try {
            mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open(mask));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceCreated() {
        lookupShaderProgram.init();
        EasyGlUtils.genTexturesWithParameter(1, mastTextures, 0, GLES20.GL_RGBA, 512, 512);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        lookupShaderProgram.useProgram();
        lookupShaderProgram.setMatrix(matrix);
        onSetExpandData();
        bindTexture();

        lookupShaderProgram.setUniforms();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        //lookupShaderProgram.afterDraw();
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    private void onSetExpandData() {
        GLES20.glUniform1f(lookupShaderProgram.getIntensityLocation(), intensity);
        if (mastTextures[0] != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mastTextures[0]);
            if (mBitmap != null && !mBitmap.isRecycled()) {
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);
                mBitmap.recycle();
            }
            GLES20.glUniform1i(lookupShaderProgram.getMaskTextureLocation(), textureType + 1);
        }
    }

    /**
     * 绑定默认纹理
     */
    protected void bindTexture(){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+textureType);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(lookupShaderProgram.getTextureUnitLocation(),textureType);
    }

}
