package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;
import opengl.xingfeng.com.opengldemo.water.ShaderUtil;

public class CameraWatermaskProgram extends MyShaderProgram {
    private float[] matrix = MatrixUtils.getOriginalMatrix();

    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -1f, 1f, 0.0f,
            -1f, -1f, 0.0f,
            1f, 1f, 0.0f,
            1f, -1f, 0.0f,

            0f, 0f, 0f,//水印预留位置
            0f, 0f, 0f,
            0f, 0f, 0f,
            0f, 0f, 0f
    };

    //纹理坐标  对应顶点坐标  与之映射
    static float textureData[] = {   // in counterclockwise order:
            0f, 0f, 0.0f,
            0f, 1f, 0.0f,
            1f, 0f, 0.0f,
            1f, 1f, 0.0f,
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    //每一次取点的时候取几个点
    static final int COORDS_PER_VERTEX = 3;

    final int vertexCount = vertexData.length / COORDS_PER_VERTEX;
    //每一次取的总的点 大小
    static final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex



    private Bitmap bitmap;
    private int waterTextureId;

    private int uTextuUnitLocation;

    //Attriocations
    private int aPositionLocation;
    private int aTextureCoordLocation;
    private int uMatrixLocation;

    //vbo id
    private int vboId;

    private int textureType = 0;      //默认使用Texture2D0
    private int textureId = 0;

    public CameraWatermaskProgram(Context context) {
        super(context);

        initWaterInfo();

        mVertexBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        mVertexBuffer.position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(textureData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        mTextureBuffer.position(0);
    }

    protected void init() {
        super.init(R.raw.watermask_vertex_shader, R.raw.watermask_fragment_shader);

        uTextuUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        createVBO();

        createWaterTextureId();
    }

    private void initWaterInfo() {
        bitmap = ShaderUtil.createTextImage("我是水印", 20, "#fff000", "#00000000", 0);

        //设置位置 根据需求自己配置
        float r = 1.0f * bitmap.getWidth() / bitmap.getHeight();
        //float r = 1.0f;
        float w = r * 0.1f;
        vertexData[12] = 1.0f - w;
        vertexData[13] = -0.1f;
        vertexData[14] = 0;

        vertexData[15] = 1.0f - w;
        vertexData[16] = -0.3f;
        vertexData[17] = 0;



        vertexData[18] = 1.0f;
        vertexData[19] = -0.1f;
        vertexData[20] = 0;

        vertexData[21] = 1.0f;
        vertexData[22] = -0.3f;
        vertexData[23] = 0;
    }

    /**
     * 创建vbo
     */
    private void createVBO() {
        //1. 创建VBO
        int[] vbos = new int[1];
        GLES20.glGenBuffers(vbos.length, vbos, 0);
        vboId = vbos[0];
        //2. 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //3. 分配VBO需要的缓存大小
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + textureData.length * 4, null, GLES20.GL_STATIC_DRAW);
        //4. 为VBO设置顶点数据的值
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, mVertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, textureData.length * 4, mTextureBuffer);
        //5. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    private void createWaterTextureId() {

        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        waterTextureId = textureIds[0];
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        ByteBuffer bitmapBuffer = ByteBuffer.allocate(bitmap.getHeight() * bitmap.getWidth() * 4);//RGBA
        bitmap.copyPixelsToBuffer(bitmapBuffer);
        //将bitmapBuffer位置移动到初始位置
        bitmapBuffer.flip();

        //设置内存大小绑定内存地址
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap.getWidth(), bitmap.getHeight(),
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bitmapBuffer);

        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void setTextureId(int inputTexture) {
        this.textureId = inputTexture;
    }

    public void draw(int width, int height) {
        //GLES20.glViewport(0, 0, width, height);
        useProgram();
        setUniforms();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        //GLES20.glUniform1i(uTextuUnitLocation, textureType);

        //使用VBO设置纹理和顶点值
        useVboSetVertext();

        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glViewport(0, 0, width, height);
        drawWater();
    }

    public void setUniforms() {
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);

        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
    }

    /**
     * 使用vbo设置顶点位置
     */
    private void useVboSetVertext() {
        //1. 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //2. 设置顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, 0);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexData.length * 4);
        //3. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void drawWater() {
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, waterTextureId);

        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);

        GLES20.glVertexAttribPointer(aPositionLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride,
                vertexStride * 4);//四个坐标之后的是水印的坐标
        GLES20.glVertexAttribPointer(aTextureCoordLocation, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }
}
