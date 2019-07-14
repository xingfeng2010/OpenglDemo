package opengl.xingfeng.com.opengldemo.beautycamera.showfbo;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.beautycamera.MyShaderProgram;
import opengl.xingfeng.com.opengldemo.util.MatrixUtils;

public class EffectShaderProgram extends MyShaderProgram {

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
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };


    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    // Uniform locaitons
    private int uMatrixLocation;
    private int uCoordMatrixLocation;
    private int uTextuUnitLocation;

    //Attriocations
    private int aPositionLocation;
    private int aTextureCoordLocation;

    private int textureType = 0;      //默认使用Texture2D0
    private int textureId = 0;

    protected EffectShaderProgram(Context context) {
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
        super.init(R.raw.oes_base_vertex, R.raw.oes_base_fragment);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextuUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        uCoordMatrixLocation = GLES20.glGetUniformLocation(program, U_COOD_MATRIX);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms() {
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
    }

    public void afterDraw() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
    }

    public void setSize(int width, int height) {
    }

    /**
     * 单位矩阵
     */
    private float[] matrix = MatrixUtils.getOriginalMatrix();
    private float[] coordMatrix = MatrixUtils.getOriginalMatrix();
    public void setCoordMatrix(float[] coordMatrix) {
        this.coordMatrix = coordMatrix;
    }

    public void draw() {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        useProgram();

        GLES20.glUniformMatrix4fv(uMatrixLocation,1, false, matrix, 0);
        GLES20.glUniformMatrix4fv(uCoordMatrixLocation,1,false,coordMatrix,0);

        setUniforms();
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureType);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(uTextuUnitLocation, textureType);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
//        afterDraw();
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }
}
