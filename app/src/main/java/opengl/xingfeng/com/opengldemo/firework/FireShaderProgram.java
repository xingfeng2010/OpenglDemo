package opengl.xingfeng.com.opengldemo.firework;

import android.content.Context;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.particles.ShaderProgram;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class FireShaderProgram extends ShaderProgram{
    // Uniform locaitons
    private final int uMatrixLocation;
    private final int uTimeLocation;

    //Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;
    private final int aDirectionVectorLocation;
    private final int aParticleStartTimeLocation;
    private final int uTextureUnitLocation;

    public FireShaderProgram(Context context) {
        super(context, R.raw.firework_vertex_shader, R.raw.firwork_fragment_shader);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTimeLocation = glGetUniformLocation(program, U_TIME);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aDirectionVectorLocation = glGetAttribLocation(program, A_DIRECTION_VECTOR);
        aParticleStartTimeLocation = glGetAttribLocation(program, A_PARTICLE_START_TIME);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
    }

    public void setUniforms(float[] matrix, float elapsedTime, int textureId) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        glUniform1f(uTimeLocation, elapsedTime);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform1f(uTextureUnitLocation, 0);
    }

    public int getAttributePositionLocation() {
        return aPositionLocation;
    }

    public int getAttributeColorLocation() {
        return aColorLocation;
    }

    public int getAttributeDirectionVectorLocation() {
        return aDirectionVectorLocation;
    }

    public int getAttributeParticleStartTimeLocation() {
        return aParticleStartTimeLocation;
    }
}
