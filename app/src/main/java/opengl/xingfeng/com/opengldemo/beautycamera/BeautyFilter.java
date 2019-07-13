package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class BeautyFilter implements CustomSurfaceView.Render{
    /**
     * 单位矩阵
     */
    private float[] matrix = MatrixUtils.getOriginalMatrix();

    private int inputTextureId;
    private int flag;

    private int mWidth=720;
    private int mHeight=1280;

    private float aaCoef;
    private float mixCoef;
    private int iternum;

    private BeautyShaderProgram beautyShaderProgram;

    public BeautyFilter(Context context) {
        setFlag(0);
        beautyShaderProgram = new BeautyShaderProgram(context);
    }

    @Override
    public void onSurfaceCreated() {
        beautyShaderProgram.init();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        this.mWidth=width;
        this.mHeight=height;
    }

    @Override
    public void onDrawFrame() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        beautyShaderProgram.useProgram();
        beautyShaderProgram.setUniforms(mWidth, mHeight, iternum, aaCoef, mixCoef);
        beautyShaderProgram.setMatrix(matrix);
        beautyShaderProgram.bindTexture(inputTextureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
        beautyShaderProgram.afterDraw();
    }

    public void setTextureId(int textureId) {
        this.inputTextureId = textureId;
    }

    public void setFlag(int flag) {
        this.flag = flag;
        switch (flag){
            case 1:
                a(1,0.19f,0.54f);
                break;
            case 2:
                a(2,0.29f,0.54f);
                break;
            case 3:
                a(3,0.17f,0.39f);
                break;
            case 4:
                a(3,0.25f,0.54f);
                break;
            case 5:
                a(4,0.13f,0.54f);
                break;
            case 6:
                a(4,0.19f,0.69f);
                break;
            default:
                a(0,0f,0f);
                break;
        }
    }

    private void a(int a,float b,float c){
        this.iternum=a;
        this.aaCoef=b;
        this.mixCoef=c;
    }
}
