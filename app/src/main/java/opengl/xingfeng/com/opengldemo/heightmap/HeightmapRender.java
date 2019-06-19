package opengl.xingfeng.com.opengldemo.heightmap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.Skybox.SkeyboxShaderProgram;
import opengl.xingfeng.com.opengldemo.Skybox.Skybox;
import opengl.xingfeng.com.opengldemo.particles.ParticleShaderProgram;
import opengl.xingfeng.com.opengldemo.particles.ParticleShooter;
import opengl.xingfeng.com.opengldemo.particles.ParticleSystem;
import opengl.xingfeng.com.opengldemo.util.Geometry.Point;
import opengl.xingfeng.com.opengldemo.util.Geometry.Vector;
import opengl.xingfeng.com.opengldemo.util.MatrixHelper;
import opengl.xingfeng.com.opengldemo.util.TextureHelper;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

public class HeightmapRender implements GLSurfaceView.Renderer{
    private Context mContext;

    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] viewMatrixForSkybox = new float[16];
    private float[] projectionMatrix = new float[16];

    private float[] tempMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleShaderProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;

    private long globalStartTime;

    private float angleVarianceInDegrees = 5f;
    private float speedVariance = 1f;

    private int particleTexture;

    private SkeyboxShaderProgram skeyboxShaderProgram;
    private Skybox skybox;
    private int skyboxTexture;

    private float xRotation, yRotation;

    private HeightmapShaderProgram heightmapShaderProgram;
    private Heightmap heightmap;

    public HeightmapRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST | GL_CULL_FACE);

        particleShaderProgram = new ParticleShaderProgram(mContext);
        heightmapShaderProgram = new HeightmapShaderProgram(mContext);
        heightmap = new Heightmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heightmap));
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();
        skeyboxShaderProgram = new SkeyboxShaderProgram(mContext);
        skybox = new Skybox();

        Vector particleDirection = new Vector(0f, 0.5f, 0f);

        redParticleShooter = new ParticleShooter(
                new Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance
        );

        greenParticleShooter = new ParticleShooter(
                new Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance
        );

        blueParticleShooter = new ParticleShooter(
                new Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5, 50, 255),
                angleVarianceInDegrees,
                speedVariance
        );

        particleTexture = TextureHelper.loadTexture(mContext, R.drawable.particle_texture);
        skyboxTexture = TextureHelper.loadCubeMap(mContext, new int[] {
              R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top,
                R.drawable.front,  R.drawable.back
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
       glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float) height, 1f, 100f);

        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawHeightmap();
        drawSkybox();
        drawParticles();
    }

    private void drawSkybox() {
        setIdentityM(viewMatrix, 0);
//        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
//        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        updateMvpMatrixForSkybox();

        glDepthFunc(GL_LEQUAL);
        skeyboxShaderProgram.useProgram();
        skeyboxShaderProgram.setUniform(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skeyboxShaderProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 1);
        greenParticleShooter.addParticles(particleSystem, currentTime, 1);
        blueParticleShooter.addParticles(particleSystem, currentTime, 1);

//        setIdentityM(viewMatrix, 0);
//        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
//        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
//        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        setIdentityM(modelMatrix, 0);
        updateMvpMatrix();

        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleShaderProgram.useProgram();
        particleShaderProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleShaderProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    public void handleTouchDrag(float deltX, float deltY) {
        xRotation += deltX / 16f;
        yRotation += deltY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }

        updateViewMatrices();
    }

    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);

        System.arraycopy(viewMatrix, 0 , viewMatrixForSkybox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0, -1.5f, -5f);
    }

    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapShaderProgram.useProgram();
        heightmapShaderProgram.setUniforms(modelViewProjectionMatrix);
        heightmap.bindData(heightmapShaderProgram);
        heightmap.draw();
    }
}
