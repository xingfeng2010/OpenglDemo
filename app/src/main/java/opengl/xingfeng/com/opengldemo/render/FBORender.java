package opengl.xingfeng.com.opengldemo.render;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.R;

public class FBORender implements GLSurfaceView.Renderer {

    private BitmapFboTexture bitmapFboTexture;
    private BitmapRenderTexture bitmapRenderTexture;

    public FBORender(Context context) {
        bitmapFboTexture = new BitmapFboTexture(context);
        bitmapFboTexture.setBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.beauty));

        bitmapRenderTexture = new BitmapRenderTexture(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        bitmapFboTexture.onSurfaceCreated();
        bitmapRenderTexture.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        //宽高
        GLES20.glViewport(0, 0, width, height);

        bitmapFboTexture.onSurfaceChanged(width, height);
        bitmapRenderTexture.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//清空颜色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //设置背景颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        //FBO处理
        bitmapFboTexture.draw();
        //通过FBO处理之后，拿到纹理id，然后渲染
        bitmapRenderTexture.draw(bitmapFboTexture.getFboTextureId());
    }
}
