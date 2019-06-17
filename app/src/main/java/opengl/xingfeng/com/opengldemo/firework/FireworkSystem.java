package opengl.xingfeng.com.opengldemo.firework;

import android.graphics.Color;

import opengl.xingfeng.com.opengldemo.particles.ParticleShaderProgram;
import opengl.xingfeng.com.opengldemo.particles.VertexArray;
import opengl.xingfeng.com.opengldemo.util.Geometry;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;

public class FireworkSystem {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int VECTOR_COMPONENT_COUNT = 3;
    private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;


    private static final int TOTAL_COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT
           + COLOR_COMPONENT_COUNT
            + VECTOR_COMPONENT_COUNT
            + PARTICLE_START_TIME_COMPONENT_COUNT;

    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

    private float[] particles;
    private VertexArray vertexArray;
    private int maxParticleCount;
    private int currentParticleCount;
    private int nextParticle;

    private float startTime;

    public FireworkSystem(int maxParticleCount) {
        particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
        vertexArray = new VertexArray(particles);
        this.maxParticleCount = maxParticleCount;
    }

    public void addParticle(Geometry.Point position, int color, Geometry.Vector direction, float particleStartTime) {
        startTime = particleStartTime;
        int particleOffset = nextParticle* TOTAL_COMPONENT_COUNT;

        int curentOffset = particleOffset;
        nextParticle ++;

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount ++;
        }

        if (nextParticle == maxParticleCount) {
            nextParticle = 0;
        }

        particles[curentOffset++] = position.x;
        particles[curentOffset++] = position.y;
        particles[curentOffset++] = position.z;

        particles[curentOffset++] = Color.red(color) / 255;
        particles[curentOffset++] = Color.green(color) / 255;
        particles[curentOffset++] = Color.blue(color) / 255;

        particles[curentOffset++] = direction.x;
        particles[curentOffset++] = direction.y;
        particles[curentOffset++] = direction.z;

        particles[curentOffset++] = particleStartTime;
        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT);
    }

    public void bindData(FireShaderProgram particleShaderProgram) {
        int dataOffset = 0;
        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getAttributePositionLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        dataOffset += POSITION_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getAttributeColorLocation(),
                COLOR_COMPONENT_COUNT, STRIDE);
        dataOffset += COLOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getAttributeDirectionVectorLocation(),
                VECTOR_COMPONENT_COUNT, STRIDE);
        dataOffset += VECTOR_COMPONENT_COUNT;

        vertexArray.setVertexAttribPointer(dataOffset,
                particleShaderProgram.getAttributeParticleStartTimeLocation(),
                PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS,0,currentParticleCount);
    }

    public void updateParticle(float currentPosition) {
        particles[1] = currentPosition;
        vertexArray.clearBuffer();

        vertexArray.updateBuffer(particles, 0, TOTAL_COMPONENT_COUNT);
    }
}
