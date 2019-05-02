package opengl.xingfeng.com.opengldemo.shape;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;

public class Triangle {
    private FloatBuffer vertexBuffer;

    private int mProgram;

    private static final String A_POSITION = "a_Position";
    private static final String U_COLOR = "uColor";
    private static final String U_MVPMATRIX = "uMVPMatrix";

    private int aVertexPosition;
    private int uColorPosition;
    private int uMVPMatrixPosition;

    float triangleCoords[] = {
            0.0f, 0.622008459f, 0.0f,
            -0.5f, -0.311004243f, 0.0f,
            0.5f, -0.311004243f, 0.0f
    };

    float color[] = {
            0.63671875f, 0.76953125f, 0.22265625f, 0.0f
    };

    public Triangle(Context context) {
        vertexBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(triangleCoords);
        vertexBuffer.position(0);

        mProgram = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.triangle_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.triangle_fragment_shader));
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mProgram);
        aVertexPosition = GLES20.glGetAttribLocation(mProgram, A_POSITION);
        GLES20.glEnableVertexAttribArray(aVertexPosition);
        GLES20.glVertexAttribPointer(
                aVertexPosition, 3,
                GLES20.GL_FLOAT, false,
                12,vertexBuffer);

        uColorPosition = GLES20.glGetUniformLocation(mProgram, U_COLOR);
        GLES20.glUniform4fv(uColorPosition, 1, color, 0);

        uMVPMatrixPosition = GLES20.glGetUniformLocation(mProgram, U_MVPMATRIX);
        GLES20.glUniformMatrix4fv(uMVPMatrixPosition, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);

        GLES20.glDisableVertexAttribArray(aVertexPosition);
    }
}
