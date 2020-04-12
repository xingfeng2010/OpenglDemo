package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;
import opengl.xingfeng.com.opengldemo.util.GlesUtil;

public class WaterMarkRenderDrawer extends BaseRenderDrawer{
    private int mMarkTextureId;
    private int mInputTextureId;
    private Bitmap mBitmap;
    private int avPosition;
    private int afPosition;
    private int sTexture;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    public WaterMarkRenderDrawer(Context context) {
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.watermark);
    }
    @Override
    public void setInputTexture(int textureId) {
        this.mInputTextureId = textureId;
    }

    @Override
    public int getOnputTextureId() {
        return fTexture[0];
    }

    @Override
    protected void onCreated() {

    }

    @Override
    protected void onChanged(int width, int height) {
        mMarkTextureId = GlesUtil.loadBitmapTexture(mBitmap);
        avPosition = GLES20.glGetAttribLocation(mProgram, "av_Position");
        afPosition = GLES20.glGetAttribLocation(mProgram, "af_Position");
        sTexture = GLES20.glGetUniformLocation(mProgram, "sTexture");
    }

    @Override
    public void draw() {
        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        useProgram();
        //clear();
        viewPort(40, 75, mBitmap.getWidth() * 2, mBitmap.getHeight() * 2);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_COLOR, GLES20.GL_DST_ALPHA);
        onDraw();
        GLES20.glDisable(GLES20.GL_BLEND);
        EasyGlUtils.unBindFrameBuffer();
    }

    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(avPosition);
        GLES20.glEnableVertexAttribArray(afPosition);
        //设置顶点位置值
        //GLES20.glVertexAttribPointer(avPosition, CoordsPerVertexCount, GLES20.GL_FLOAT, false, VertexStride, mVertexBuffer);
        //设置纹理位置值
        //GLES20.glVertexAttribPointer(afPosition, CoordsPerTextureCount, GLES20.GL_FLOAT, false, TextureStride, mFrameTextureBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferId);
        GLES20.glVertexAttribPointer(avPosition, CoordsPerVertexCount, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mFrameTextureBufferId);
        GLES20.glVertexAttribPointer(afPosition, CoordsPerTextureCount, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mMarkTextureId);
        GLES20.glUniform1i(sTexture, 0);
        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VertexCount);
        GLES20.glDisableVertexAttribArray(avPosition);
        GLES20.glDisableVertexAttribArray(afPosition);
    }

    @Override
    protected String getVertexSource() {
        final String source =
                "attribute vec4 av_Position; " +
                        "attribute vec2 af_Position; " +
                        "varying vec2 v_texPo; " +
                        "void main() { " +
                        "    v_texPo = af_Position; " +
                        "    gl_Position = av_Position; " +
                        "}";
        return source;
    }

    @Override
    protected String getFragmentSource() {
        final String source =
                "precision mediump float; " +
                        "varying vec2 v_texPo; " +
                        "uniform sampler2D sTexture; " +
                        "void main() { " +
                        "   gl_FragColor = texture2D(sTexture, v_texPo); " +
                        "} ";
        return source;
    }
}
