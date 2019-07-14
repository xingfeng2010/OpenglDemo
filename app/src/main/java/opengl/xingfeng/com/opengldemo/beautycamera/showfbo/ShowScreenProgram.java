package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.beautycamera.MyShaderProgram;
import opengl.xingfeng.com.opengldemo.util.PermissionUtil;

public class ShowScreenProgram  extends MyShaderProgram{
    //顶点坐标
    private float vertex[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord = {
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };


    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    //Attriocations
    private int aPositionLocation;
    private int aTextureCoordLocation;

    // Uniform locaitons
    private int uTextuUnitLocation;
    private int uMatrixLocation;
    private int vboId;

    protected ShowScreenProgram(Context context) {
        super(context);

        mVertexBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
        mVertexBuffer.position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(coord.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(coord);
        mTextureBuffer.position(0);
    }

    protected void init() {
        super.init(R.raw.show_screen_vertex_shader, R.raw.show_screen_fragment_shader);


        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
        uTextuUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        createVBO();
    }

    public void setUniforms() {
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
    }

    public void bindTexture(int inputTextureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,inputTextureId);
        GLES20.glUniform1i(uTextuUnitLocation, 0);
    }

    public void afterDraw() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
    }

    public void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
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
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex.length * 4 + coord.length * 4, null, GLES20.GL_STATIC_DRAW);
        //4. 为VBO设置顶点数据的值
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertex.length * 4, mVertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertex.length * 4, coord.length * 4, mTextureBuffer);
        //5. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    /**
     * 使用vbo设置顶点位置
     */
    public void useVboSetVertext() {
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        //1. 绑定VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        //2. 设置顶点数据
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 8, 0);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 8, vertex.length * 4);
        //3. 解绑VBO
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }

    public void setMatrix(float[] matrix) {
        //GLES20.glEnableVertexAttribArray(uMatrixLocation);
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0);
    }
}
