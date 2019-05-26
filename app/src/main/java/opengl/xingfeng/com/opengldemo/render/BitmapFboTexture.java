package opengl.xingfeng.com.opengldemo.render;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.ShaderHelper;
import opengl.xingfeng.com.opengldemo.util.TextResourceReader;


//纹理  根据坐标系映射
public class BitmapFboTexture {


    //顶点坐标
    static float vertexData[] = {   // in counterclockwise order:
            -1f, -1f, 0.0f, // bottom left
            1f, -1f, 0.0f, // bottom right
            -1f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };

    //正常纹理坐标  对应顶点坐标  与之映射
//    static float textureData[] = {   // in counterclockwise order:
//            0f, 1f, 0.0f, // bottom left
//            1f, 1f, 0.0f, // bottom right
//            0f, 0f, 0.0f, // top left
//            1f, 0f, 0.0f,  // top right
//    };

    //fbo 纹理坐标
    static float textureData[] = {   // in counterclockwise order:
            0f, 0f, 0.0f, // bottom left
            1f, 0f, 0.0f, // bottom right
            0f, 1f, 0.0f, // top left
            1f, 1f, 0.0f,  // top right
    };

    //每一次取点的时候取几个点
    static final int COORDS_PER_VERTEX = 3;

    private final int vertexCount = vertexData.length / COORDS_PER_VERTEX;
    //每一次取的总的点 大小
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    private Context context;

    //位置
    private FloatBuffer vertexBuffer;
    //纹理
    private FloatBuffer textureBuffer;
    private int program;
    private int avPosition;
    //纹理位置
    private int afPosition;
    //需要渲染的纹理id
    private int imageTextureId;
    //fbo纹理id
    private int fboTextureId;
    //fbo Id
    private int fboId;

    private Bitmap bitmap;

    private int uMatrix;

    private float[] matrix = new float[16];

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapFboTexture(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureData);
        textureBuffer.position(0);
    }


    public void onSurfaceCreated() {
        String vertexSource = TextResourceReader.readTextFileFromResource(context, R.raw.fbo_vertex_shader);
        String fragmentSource = TextResourceReader.readTextFileFromResource(context, R.raw.fbo_fragment_shader);
        program = ShaderHelper.buildProgram(vertexSource, fragmentSource);

        if (program > 0) {
            //获取顶点坐标字段
            avPosition = GLES20.glGetAttribLocation(program, "aPosition");
            //获取纹理坐标字段
            afPosition = GLES20.glGetAttribLocation(program, "aTextureCoord");

            uMatrix = GLES20.glGetUniformLocation(program, "uMatrix");

           // createFBO();
            imageTextureId = createImageTexture();
        }
    }

    public void draw() {

        //绑定fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        //使用程序
        GLES20.glUseProgram(program);

        //绑定渲染纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTextureId);

        GLES20.glEnableVertexAttribArray(avPosition);
        GLES20.glEnableVertexAttribArray(afPosition);
        GLES20.glEnableVertexAttribArray(uMatrix);

        GLES20.glUniformMatrix4fv(uMatrix, 1, false, matrix, 0);

        //设置顶点位置值
        GLES20.glVertexAttribPointer(avPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        //设置纹理位置值
        GLES20.glVertexAttribPointer(afPosition, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, textureBuffer);
        //绘制 GLES20.GL_TRIANGLE_STRIP:复用坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(avPosition);
        GLES20.glDisableVertexAttribArray(afPosition);
        GLES20.glDisableVertexAttribArray(uMatrix);

        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //解绑fbo
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    private void createFBO(int width, int height) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap is  null");
        }

        //1. 创建FBO
        int[] fbos = new int[1];
        GLES20.glGenFramebuffers(1, fbos, 0);
        fboId = fbos[0];
        //2. 绑定FBO
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);

        //3. 创建FBO纹理
        fboTextureId = createTexture();

        //4. 把纹理绑定到颜色附件
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fboTextureId, 0);

        //5. 设置FBO分配内存大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        //6. 检测是否绑定从成功
        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
                != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("zzz", "glFramebufferTexture2D error");
        }
        //7. 解绑纹理和FBO
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private int createImageTexture() {
        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        //测试图片
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return textureIds[0];
    }


    private int createTexture() {
        int[] textureIds = new int[1];
        //创建纹理
        GLES20.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            return 0;
        }
        //绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0]);
        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        return textureIds[0];
    }

    public int getFboTextureId() {
        return fboTextureId;
    }

    public void onSurfaceChanged(int width, int height) {
       if (width > height) {
           float x = width / ((float) height / bitmap.getHeight() * bitmap.getWidth());

           Matrix.orthoM(matrix, 0, -x, x, -1, 1, -1, 1);
       } else {
           float y = height / ((float) width / bitmap.getWidth() * bitmap.getHeight());
           Matrix.orthoM(matrix, 0, -1, 1, -y, y, -1, 1);
       }


        if (fboId != 0) {
            GLES20.glDeleteFramebuffers(1, new int[]{fboId}, 0);
            GLES20.glDeleteTextures(1, new int[]{imageTextureId}, 0);
        }

        createFBO(width, height);
    }
}
