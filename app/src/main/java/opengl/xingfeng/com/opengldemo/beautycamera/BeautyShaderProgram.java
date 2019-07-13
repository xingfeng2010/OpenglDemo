package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;

public class BeautyShaderProgram extends MyShaderProgram{
    private static final String U_ITER_NUM = "uIternum";
    private static final String U_AA_COEF = "uAaCoef";
    private static final String U_MIX_COEF = "uMixCoef";
    private static final String U_WIDTH = "mWidth";
    private static final String U_HEIGHT = "mHeight";


    // Uniform locaitons
    private int uMatrixLocation;
    private int uTextureUnitLocation;
    private int uIternumLocation;
    private int uAaCoefLocation;
    private int uMixCoefLocation;
    private int uWidthLocation;
    private int uHeightLocation;

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
    private float[] coord ={
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
                .put(coord);
        mTextureBuffer.position(0);
    }

    protected void init() {
        super.init(R.raw.beauty_vertex_shader, R.raw.beauty_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        uIternumLocation = GLES20.glGetUniformLocation(program, U_ITER_NUM);
        uAaCoefLocation = GLES20.glGetUniformLocation(program, U_AA_COEF);
        uMixCoefLocation = GLES20.glGetUniformLocation(program, U_MIX_COEF);
        uWidthLocation=GLES20.glGetUniformLocation(program,U_WIDTH);
        uHeightLocation=GLES20.glGetUniformLocation(program, U_HEIGHT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(int width, int height, int iternum, float aacoef, float mixcoef) {
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);

        GLES20.glUniform1i(uWidthLocation, width);
        GLES20.glUniform1i(uHeightLocation, height);
        GLES20.glUniform1i(uIternumLocation, iternum);
        GLES20.glUniform1f(uAaCoefLocation, aacoef);
        GLES20.glUniform1f(uMixCoefLocation, mixcoef);
    }

    public void afterDraw() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aTextureCoordLocation);
        GLES20.glDisableVertexAttribArray(uMatrixLocation);
    }


    public void setMatrix(float[] mMVPMatrix) {
        GLES20.glUniformMatrix4fv(uMatrixLocation,1,false,mMVPMatrix,0);
    }

    /**
     * 绑定默认纹理
     */
    protected void bindTexture(int inputTexture){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,inputTexture);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }
}
