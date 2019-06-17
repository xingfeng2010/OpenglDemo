package opengl.xingfeng.com.opengldemo.particles;

import java.util.Random;

import opengl.xingfeng.com.opengldemo.util.Geometry.Point;
import opengl.xingfeng.com.opengldemo.util.Geometry.Vector;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

public class ParticleShooter {
    private float angleVariance;
    private float speedVariance;

    private Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    private Point position;
    private Vector direction;
    private int color;

    public ParticleShooter(Point position, Vector direction, int color,
    float angleVarianceInDegrees, float speedVariance) {
        this.position = position;
        this.direction = direction;
        this.color = color;

        this.angleVariance = angleVarianceInDegrees;
        this.speedVariance = speedVariance;

        directionVector[0] = direction.x;
        directionVector[1] = direction.y;
        directionVector[2] = direction.z;
    }

    public void addParticles(ParticleSystem particleSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
            setRotateEulerM(rotationMatrix, 0,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance,
                    (random.nextFloat() - 0.5f) * angleVariance);

            multiplyMV(resultVector ,0,
                    rotationMatrix ,0,
                    directionVector, 0);

            float speedAdustment = 1f + random.nextFloat() * speedVariance;

            Vector thisDirection = new Vector(
                    resultVector[0] * speedAdustment,
                    resultVector[1] * speedAdustment,
                    resultVector[2] * speedAdustment
                    );

            particleSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}
