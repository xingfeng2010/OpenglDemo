package opengl.xingfeng.com.opengldemo.firework;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.particles.ParticleShaderProgram;
import opengl.xingfeng.com.opengldemo.particles.ParticleShooter;
import opengl.xingfeng.com.opengldemo.particles.ParticleSystem;
import opengl.xingfeng.com.opengldemo.util.Geometry.Point;
import opengl.xingfeng.com.opengldemo.util.Geometry.Vector;
import opengl.xingfeng.com.opengldemo.util.MatrixHelper;
import opengl.xingfeng.com.opengldemo.util.TextureHelper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class FireworkRender implements GLSurfaceView.Renderer{
    private Context mContext;

    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] viewProjectionMatrix = new float[16];

    private FireShaderProgram particleShaderProgram;
    private FireworkSystem particleSystem;
    private FireworkShooter redParticleShooter;
    private FireworkShooter greenParticleShooter;
    private FireworkShooter blueParticleShooter;

    private long globalStartTime;

    private float angleVarianceInDegrees = 5f;
    private float speedVariance = 1f;

    private int texture;


    public FireworkRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram = new FireShaderProgram(mContext);
        particleSystem = new FireworkSystem(10000);
        globalStartTime = System.nanoTime();

        Vector particleDirection = new Vector(0f, 0.5f, 0f);

        redParticleShooter = new FireworkShooter(
                new Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance
        );

        greenParticleShooter = new FireworkShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance
        );

        blueParticleShooter = new FireworkShooter(
                new Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance
        );

        texture = TextureHelper.loadTexture(mContext, R.drawable.particle_texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
       glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float) height, 1f, 10f);
        setIdentityM(viewMatrix, 0);
        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

//        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
//        greenParticleShooter.addParticles(particleSystem, currentTime, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;


        if (!greenParticleShooter.hasFirework()) {
            greenParticleShooter.addParticles(particleSystem, currentTime, 1);
        } else {
            if (!greenParticleShooter.isToTheTop()) {
                greenParticleShooter.updateParaticles(particleSystem, currentTime);
            } else {
                greenParticleShooter.addFireWork(particleSystem, currentTime, 10);
            }
        }
       // redParticleShooter.addParticles(particleSystem, currentTime, 5);
      //  greenParticleShooter.addParticles(particleSystem, currentTime, 1);
      //  blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();
    }
}
