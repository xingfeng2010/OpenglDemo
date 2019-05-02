package opengl.xingfeng.com.opengldemo.shape;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;

public class Square {
    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    private int mProgram;

    private static final String A_POSITION = "vPosition";
    private static final String U_COLOR = "uColor";
    private static final String U_MVPMATRIX = "uMVPMatrix";

    private int aVertexPosition;
    private int uColorPosition;
    private int uMVPMatrixPosition;

    private float squareCoords[] = {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f,-0.5f,0.0f,
            0.5f,0.5f,0.0f
    };

    private final short drawOrder[] = {0, 1, 2, 0, 2, 3};
    private final int vertexStride = 3 * 4;
    private float[] color = {0.2f, 0.709803922f, 0.898039216f, 1.0f};

    public Square(Context context) {
        vertexBuffer = ByteBuffer.allocateDirect(squareCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(squareCoords);
        vertexBuffer.position(0);

        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.square_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.square_fragment_shader));
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);

        aVertexPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(aVertexPosition);
        GLES20.glVertexAttribPointer(aVertexPosition,3,
                GLES20.GL_FLOAT, false, 12, vertexBuffer);

        uColorPosition = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(uColorPosition, 1, color, 0);

        uMVPMatrixPosition = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(uMVPMatrixPosition, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN,0, 4);

        GLES20.glDisableVertexAttribArray(aVertexPosition);
    }

}
