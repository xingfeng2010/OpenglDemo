package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView;
import opengl.xingfeng.com.opengldemo.util.DisplayUtil;
import opengl.xingfeng.com.opengldemo.util.Gl2Utils;

public class ShowFilter implements CustomSurfaceView.Render {
    private ShowShaderProgram showShaderProgram;
    private Context mContext;
    private float[] matrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private int screenWidth, screenHeight;

    private ShowScreenRender showScreenRender;

    public ShowFilter(Context context) {
        mContext = context;
        showScreenRender = new ShowScreenRender(context);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    @Override
    public void onSurfaceCreated() {
        screenWidth = DisplayUtil.getScreenW(mContext);
        screenHeight = DisplayUtil.getScreenH(mContext);

        showShaderProgram = new ShowShaderProgram(mContext);
        showShaderProgram.createVBO();
        showShaderProgram.createFBO(screenWidth, screenHeight);
        showShaderProgram.createCameraRenderTexture();

        surfaceTexture = new SurfaceTexture(showShaderProgram.getInputTextureId());
//        showShaderProgram.setUniforms();

        showScreenRender.setInputTexture(showShaderProgram.getOnputTextureId());
        showScreenRender.onSurfaceCreated();
        Log.i("EGLThread", "onSurfaceCreated: " + Thread.currentThread().getName());
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
       // Matrix.setIdentityM(matrix, 0);
        showScreenRender.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
        Log.i("EGLThread", "onDrawFrame: " + Thread.currentThread().getName());
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();
        }
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        showShaderProgram.useProgram();
        showShaderProgram.bindFramebuffer();
        showShaderProgram.useExternalTexture();
        showShaderProgram.setMatrix(matrix);
        showShaderProgram.useVboSetVertext();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        showShaderProgram.afterDraw();
        showShaderProgram.unbindFramebuffer();

        showScreenRender.onDrawFrame();
    }

    public void setSize(int width, int height) {
        Gl2Utils.getShowMatrix(matrix,width,height,screenWidth,screenHeight);
    }

    public void setMatrix(float[] matrix) {
        // this.matrix = matrix;
    }

    public void setinputTextureId(int inputTextureId) {
        //this.inputTextureId = inputTextureId;
    }

    /**
     * 初始化矩阵
     */
    public void resetMatirx() {
        //初始化
        Matrix.setIdentityM(matrix, 0);
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
        Matrix.rotateM(matrix, 0, angle, x, y, z);
    }
}
