package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;

import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public abstract class MyShaderProgram {
    protected static final int BYTES_PER_FLOAT = 4;

    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";
    protected static final String U_TEXTURE_UNIT = "uTexture";
    protected static final String U_COOD_MATRIX = "u_CoordMatrix";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    protected final int program;

    protected MyShaderProgram(Context context, int vertexShaderResourceId,
                              int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader
                        .readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader
                        .readTextFileFromResource(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
