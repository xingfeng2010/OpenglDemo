package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.opengl.GLES20;

import opengl.xingfeng.com.opengldemo.beautycamera.CustomSurfaceView;

public class ShowScreenRender implements CustomSurfaceView.Render{

    private Context mContext;
    private int inputTexture;
    private ShowScreenProgram showScreenProgram;

    public ShowScreenRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated() {
        showScreenProgram = new ShowScreenProgram(mContext);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //宽高
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame() {
        //清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        showScreenProgram.useProgram();
        //showScreenProgram.setUniforms();
        showScreenProgram.bindTexture(inputTexture);
        showScreenProgram.useVboSetVertext();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        showScreenProgram.afterDraw();
        showScreenProgram.unbindTexture();
    }

    public void setInputTexture(int inputTexture) {
        this.inputTexture = inputTexture;
    }
}
