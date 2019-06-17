package opengl.xingfeng.com.opengldemo.firework;

import android.util.Log;

import java.util.Random;

import opengl.xingfeng.com.opengldemo.particles.ParticleSystem;
import opengl.xingfeng.com.opengldemo.util.Geometry.Point;
import opengl.xingfeng.com.opengldemo.util.Geometry.Vector;

import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.setRotateEulerM;

public class FireworkShooter {
    private float angleVariance;
    private float speedVariance;

    private Random random = new Random();
    private float[] rotationMatrix = new float[16];
    private float[] directionVector = new float[4];
    private float[] resultVector = new float[4];

    private Point position;
    private Vector direction;
    private int color;

    private float startTime;

    private float initspeed = 0.3f;

    private int fireCount;

    private float previousPosition;
    private boolean toTheTop;

    public FireworkShooter(Point position, Vector direction, int color,
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

    public void addParticles(FireworkSystem particleSystem, float currentTime, int count) {
        fireCount += count;
        startTime = currentTime;

        for (int i = 0; i < 50; i++) {
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

            particleSystem.addParticle(position, color, direction, currentTime);
        }
    }

    public void updateParaticles(FireworkSystem particleSystem, float currentTime) {
        float elapsedTime = currentTime - startTime;
        float graviityFactor = elapsedTime * elapsedTime / (float) 8.0;
        float currentPosition = position.y + initspeed +(direction.y * elapsedTime);


        currentPosition -= graviityFactor;
        if (currentPosition <= previousPosition) {
            toTheTop = true;
        } else {
            previousPosition = currentPosition;
            Log.i("FireworkShooter","updateParaticles currentPosition:" + currentPosition);
            particleSystem.updateParticle(currentPosition);
            toTheTop = false;
        }

    }

    public boolean hasFirework() {
        return fireCount > 0 ? true: false;
    }

    public boolean isToTheTop() {
        return toTheTop;
    }

    public void addFireWork(FireworkSystem fireworkSystem, float currentTime, int count) {
        for (int i = 0; i < count; i++) {
//            setRotateEulerM(rotationMatrix, 0,
//                    (random.nextFloat() - 0.5f) * angleVariance,
//                    (random.nextFloat() - 0.5f) * angleVariance,
//                    (random.nextFloat() - 0.5f) * angleVariance);
//
//            multiplyMV(resultVector ,0,
//                    rotationMatrix ,0,
//                    directionVector, 0);
//
//            float speedAdustment = 1f + random.nextFloat() * speedVariance;


            float angleInRadians =
                    ((float) i / (float) count)
                            * ((float) Math.PI * 2f);

            Vector thisDirection = new Vector(
                    (float) Math.cos(angleInRadians),
                    0,
                    (float) Math.sin(angleInRadians)

            );


            position.y = previousPosition;
            fireworkSystem.addParticle(position, color, thisDirection, currentTime);
        }
    }
}
