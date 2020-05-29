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

public class ImageFilterProgram extends MyShaderProgram {
    private float[] matrix = MatrixUtils.getOriginalMatrix();

    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -1.0f, -1.0f,  // 0 bottom left
            1.0f,  -1.0f,  // 1 bottom right
            -1.0f,  1.0f,  // 2 top left
            1.0f,   1.0f,  // 3 top right
    };

    //纹理坐标  对应顶点坐标  与之映射
    static float textureData[] = {   // in counterclockwise order:
            0.0f, 0.0f,     // 0 left bottom
            1.0f, 0.0f,     // 1 right bottom
            0.0f, 1.0f,     // 2 left top
            1.0f, 1.0f      // 3 right top
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    //每一次取点的时候取几个点
    static final int COORDS_PER_VERTEX = 2;

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

    public ImageFilterProgram(Context context) {
        super(context);

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

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    public void reinit(int stickerTexture, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        this.textureId = stickerTexture;
        mVertexBuffer = vertexBuffer;
        mTextureBuffer = textureBuffer;

        createVBO();
    }
}
