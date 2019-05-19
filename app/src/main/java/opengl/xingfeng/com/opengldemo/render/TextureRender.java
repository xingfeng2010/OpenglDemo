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
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;
import opengl.xingfeng.com.opengldemo.util.TextureHelper;

public class TextureRender implements GLSurfaceView.Renderer {

    private ShortBuffer mIndexBuffer;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private Context mContext;

    private static final String A_POSITINO =  "aPosition";
    private static final String A_TEXTURECOORD = "aTextureCoord";
    private static final String U_MATRIX = "uMatrix";
    private static final String U_TEXTURE = "uTexture";

    private int aVertexPosition;
    private int aTextureCoord;
    private int uMatrixPosition;
    private int uTexturePositoin;

    private int mProgram;
    private int mTexture;
    private int vboId;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private final float[] mVertexData = {
            0f,    0f,    0f,
            1.0f,  0.75f, 0f,
            -1.0f,  0.75f, 0f,
            -1.0f, -0.75f, 0f,
            1.0f, -0.75f, 0f
    };

    private final short[] mIndexData = {
            0, 1, 2, // 0号点，1号点，2号点组成一个三角形
            0, 2, 3, // 0号点，2号点，3号点组成一个三角形
            0, 3, 4, // 0号点，3号点，4号点组成一个三角形
            0, 4, 1 // 0号点， 4号点，1号点组成一个三角形
    };

    //纹理坐标
    private final float[] mTextureVertexData = {
            0.5f,0.375f,
            1f,0f,
            0f,0f,
            0f,0.75f,
            1f,0.75f
    };

    public TextureRender(Context context) {
        mContext = context;

        initData();
    }

    private void initData() {
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexData.length *4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mVertexData);
        mVertexBuffer.position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(mTextureVertexData.length *4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(mTextureVertexData);
        mTextureBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(mIndexData.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();

        mIndexBuffer.put(mIndexData);
        mIndexBuffer.position(0);
    }

    private void initProgram() {
        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(mContext, R.raw.texture_vertex_shader),
                TextResourceReader.readTextFileFromResource(mContext,R.raw.texture_fragment_shader));


        mTexture = TextureHelper.loadTexture(mContext,R.drawable.beauty);


        aVertexPosition = GLES20.glGetAttribLocation(mProgram, A_POSITINO);
        aTextureCoord = GLES20.glGetAttribLocation(mProgram, A_TEXTURECOORD);
        uTexturePositoin = GLES20.glGetUniformLocation(mProgram, U_TEXTURE);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);


        initProgram();
        createVbo();
    }

    private void createVbo() {
        //1.创建VBO
        int[] vbos = new int[1];
        GLES20.glGenBuffers(vbos.length, vbos, 0);
        vboId = vbos[0];

        //2.绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //3.分配VBO需要的缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexData.length * 4 + mTextureVertexData.length * 4,
                null, GLES20.GL_STATIC_DRAW);
        //4.为VBO设置定点数据的值
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, mVertexData.length * 4, mVertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, mVertexData.length * 4, mTextureVertexData.length * 4, mTextureBuffer);
        //5.解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1,3,7);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //normalDraw();
        vboDraw();
    }

    private void vboDraw() {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(uMatrixPosition, 1, false, mProjectionMatrix, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        GLES20.glEnableVertexAttribArray(aVertexPosition);
        GLES20.glVertexAttribPointer(aVertexPosition, 3, GLES20.GL_FLOAT, false, 12, 0);

        GLES20.glEnableVertexAttribArray(aTextureCoord);
        GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false, 8, mVertexData.length * 4);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);

        GLES20.glUniform1i(uTexturePositoin, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,mIndexData.length,GLES30.GL_UNSIGNED_SHORT,mIndexBuffer);

        GLES20.glDisableVertexAttribArray(aVertexPosition);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void normalDraw() {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);

        GLES20.glUniformMatrix4fv(uMatrixPosition, 1, false, mProjectionMatrix, 0);

        GLES20.glEnableVertexAttribArray(aVertexPosition);
        GLES20.glVertexAttribPointer(aVertexPosition, 3, GLES20.GL_FLOAT, false, 12, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoord);
        GLES20.glVertexAttribPointer(aTextureCoord, 2, GLES20.GL_FLOAT, false, 8, mTextureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);

        GLES20.glUniform1i(uTexturePositoin, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,mIndexData.length,GLES30.GL_UNSIGNED_SHORT,mIndexBuffer);

        GLES20.glDisableVertexAttribArray(aVertexPosition);
    }
}
