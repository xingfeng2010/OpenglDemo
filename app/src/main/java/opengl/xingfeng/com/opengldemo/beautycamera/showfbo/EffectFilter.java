package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView;
import opengl.xingfeng.com.opengldemo.util.DisplayUtil;
import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;
import opengl.xingfeng.com.opengldemo.util.Gl2Utils;

public class EffectFilter implements CustomSurfaceView.Render {
    private EffectShaderProgram showShaderProgram;
    private Context mContext;
    private float[] matrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private int width, height;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int[] mCameraTexture=new int[1];
    private float[] mCoordOM=new float[16];

    public EffectFilter(Context context) {
        mContext = context;
        showShaderProgram = new EffectShaderProgram(mContext);
    }

    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }

    @Override
    public void onSurfaceCreated() {
        showShaderProgram.init();
        createOesTexture();
        surfaceTexture = new SurfaceTexture(mCameraTexture[0]);
        Log.i("EGLThread", "onSurfaceCreated: " + Thread.currentThread().getName());
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //GLES20.glViewport(0, 0, width, height);
        //showShaderProgram.setSize(width, height);
        this.width = width;
        this.height = height;
        //创建FrameBuffer和Texture
        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1, fFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, fTexture,0,GLES20.GL_RGBA,width,height);
    }

    @Override
    public void onDrawFrame() {
        Log.i("EGLThread", "onDrawFrame: " + Thread.currentThread().getName());
        boolean a=GLES20.glIsEnabled(GLES20.GL_DEPTH_TEST);
        if(a){
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
        if (surfaceTexture != null) {
            surfaceTexture.updateTexImage();

            surfaceTexture.getTransformMatrix(mCoordOM);
            showShaderProgram.setCoordMatrix(mCoordOM);
        }
        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        GLES20.glViewport(0,0,width,height);
        showShaderProgram.setTextureId(mCameraTexture[0]);
        showShaderProgram.draw();
        Log.e("wuwang","textureFilter draw");
        EasyGlUtils.unBindFrameBuffer();

        if(a){
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }
    }

    public void setMatrix(float[] matrix) {
        // this.matrix = matrix;
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

    public int getOnputTextureId() {
        return fTexture[0];
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture,0);
    }

    private void createOesTexture(){
        GLES20.glGenTextures(1,mCameraTexture,0);
    }
}
