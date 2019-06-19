package opengl.xingfeng.com.opengldemo.heightmap;

import android.graphics.Bitmap;
import android.graphics.Color;

import opengl.xingfeng.com.opengldemo.particles.ParticlesRender;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

public class Heightmap {
    private static int POSITION_COMPONENT_COUNT = 3;

    private int width;
    private int height;
    private int numElements;
    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;

    public Heightmap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        if (width * height > 65536) {
            throw new RuntimeException("Heightmap is too large for the index buffer.");
        }
        
        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private short[] createIndexData() {
       short[] indexData = new short[numElements];
       int offset = 0;

       for (int row = 0; row < height- 1; row ++) {
           for (int col = 0; col < width - 1; col ++) {
               short topLeftIndexNum = (short) (row * width + col);
               short topRightIndexNum = (short) (row * width + col + 1);
               short bottomLeftIndexNum = (short) ((row + 1) * width + col);
               short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);

               //Write out two triangles.
               indexData[offset ++] = topLeftIndexNum;
               indexData[offset ++] = bottomLeftIndexNum;
               indexData[offset ++] = topRightIndexNum;

               indexData[offset ++] = topRightIndexNum;
               indexData[offset ++] = bottomLeftIndexNum;
               indexData[offset ++] = bottomRightIndexNum;
           }
       }

       return indexData;
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();

        float[] heightmapVertices = new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;
        for (int row = 0; row < height; row ++) {
            for (int col = 0; col < width; col ++) {
                float xPosition = ((float) col / (float)(width - 1)) - 0.5f;
                float yPosition = (float) Color.red(pixels[row * height + col]) / (float)255;
                float zPosition = ((float) row / (float)(height - 1)) - 0.5f;

                heightmapVertices[offset ++] = xPosition;
                heightmapVertices[offset ++] = yPosition;
                heightmapVertices[offset ++] = zPosition;
            }
        }

        return heightmapVertices;
    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    public void bindData(HeightmapShaderProgram heightmapShaderProgram) {
        vertexBuffer.setVertexAttribPointer(0,
                heightmapShaderProgram.getAttributePositionLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
