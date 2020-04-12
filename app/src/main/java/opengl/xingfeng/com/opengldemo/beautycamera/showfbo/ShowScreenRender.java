package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.opengl.GLES20;


import opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class ShowScreenRender implements CustomSurfaceView.Render{

    private Context mContext;
    private int inputTexture;
    private ShowScreenProgram showScreenProgram;

    int width, height;

    private float[] matrix = MatrixUtils.getOriginalMatrix();

    public ShowScreenRender(Context context) {
        mContext = context;
        showScreenProgram = new ShowScreenProgram(mContext);
    }

    @Override
    public void onSurfaceCreated() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        showScreenProgram.init();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //宽高
        GLES20.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;
    }

    @Override
    public void onDrawFrame() {
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        showScreenProgram.useProgram();
        //showScreenProgram.setUniforms();
        showScreenProgram.setMatrix(matrix);
        showScreenProgram.bindTexture(inputTexture);
        showScreenProgram.useVboSetVertext();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        showScreenProgram.afterDraw();
        showScreenProgram.unbindTexture();
    }

    public void setInputTexture(int inputTexture) {
        this.inputTexture = inputTexture;
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
