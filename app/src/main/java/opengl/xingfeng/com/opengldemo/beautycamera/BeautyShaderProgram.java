package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;

public class BeautyShaderProgram extends MyShaderProgram {
    // Uniform locaitons
    private int uMatrixLocation;
    private int uTextuUnitLocation;
    private int uCoordMatrixLocation;

    //Attriocations
    private int aPositionLocation;
    private int aTextureCoordLocation;

    //顶点坐标
    private float vertex[] = {
            -1.0f,  1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            1.0f,  -1.0f,
    };

    //纹理坐标
    private float[] coord={
            0.0f, 0.0f,
            0.0f,  1.0f,
            1.0f,  0.0f,
            1.0f, 1.0f,
    };

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    protected BeautyShaderProgram(Context context) {
        super(context);

        mVertexBuffer = ByteBuffer.allocateDirect(vertex.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
        mVertexBuffer.position(0);

        mTextureBuffer = ByteBuffer.allocateDirect(coord.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertex);
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
        GLES20.glDisableVertexAttribArray(uMatrixLocation);
        GLES20.glDisableVertexAttribArray(uCoordMatrixLocation);
    }

    public int getTextureUnitLocation() {
        return uTextuUnitLocation;
    }

    public void setMatrix(float[] mMVPMatrix, float[] mTextureMatrix) {
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,mMVPMatrix,0);
        GLES20.glUniformMatrix4fv(uCoordMatrixLocation,1,false,mTextureMatrix,0);
    }
}
