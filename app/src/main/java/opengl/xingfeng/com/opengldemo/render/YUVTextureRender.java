package opengl.xingfeng.com.opengldemo.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;
import opengl.xingfeng.com.opengldemo.util.TextureHelper;

public class YUVTextureRender implements GLSurfaceView.Renderer {

    private YUV420Texture yuv420Texture;
    private Context mContext;


    public YUVTextureRender(Context context) {
        mContext = context;

        initData();
    }

    private void initData() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        yuv420Texture = new YUV420Texture(mContext);
        yuv420Texture.initYUV();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        yuv420Texture.draw();
    }

    public void setYuvData(int width, int height, byte[] y, byte[] u, byte[] v) {
        if (yuv420Texture != null) {
            yuv420Texture.setYUVData(width, height, y, u, v);
        }
    }
}
