package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.view.CustomGlSurfaceView;

public class ShowFilter  implements CustomGlSurfaceView.Renderer{
    private ShowShaderProgram showShaderProgram;
    private Context mContext;


    public ShowFilter(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        showShaderProgram = new ShowShaderProgram(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    public void setSize(int width, int height) {
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
