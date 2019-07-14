package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import opengl.xingfeng.com.opengldemo.util.DisplayUtil;

public class BeautyRender implements CustomSurfaceView.Render {
    private Context mContext;
    private int inputTexture;
    private int fTextureSize = 2;
    private int[] fTexture = new int[fTextureSize];
    private int[] fFrame = new int[1];
    private int[] fRender = new int[1];
    private int textureIndex = 0;

    private LookupFilter lookupFilter;
    private BeautyFilter beautyFilter;

    private int width, height;

    public BeautyRender(Context context) {
        lookupFilter = new LookupFilter(context);
        beautyFilter = new BeautyFilter(context);

        lookupFilter.setMaskImage("purity.png");
        lookupFilter.setIntensity(0.0f);

        mContext = context;
        width = DisplayUtil.getScreenW(context);
        height = DisplayUtil.getScreenH(context);
    }

    @Override
    public void onSurfaceCreated() {
        lookupFilter.onSurfaceCreated();
        beautyFilter.onSurfaceCreated();

//        createFBO(width, height);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        lookupFilter.onSurfaceChanged(width, height);
        beautyFilter.onSurfaceChanged(width, height);

        this.width = width;
        this.height = height;
        createFrameBuffer();
    }

    @Override
    public void onDrawFrame() {
        textureIndex = 0;
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[textureIndex%2], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);

        GLES20.glViewport(0, 0, width, height);
        lookupFilter.setTextureId(inputTexture);
        lookupFilter.onDrawFrame();
        unBindFrame();
        textureIndex = 1;


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[textureIndex%2], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);

        GLES20.glViewport(0, 0, width, height);
        beautyFilter.setTextureId(fTexture[(textureIndex-1)%2]);
        beautyFilter.onDrawFrame();
        unBindFrame();
        textureIndex = 2;

    }

    public void setInputTexture(int inputTexture) {
        this.inputTexture = inputTexture;
    }

    public int getOnputTextureId() {
        Log.i("XXXX", "getOnputTextureId:" + textureIndex);
        return fTexture[(textureIndex-1)%2];
    }

    //创建FrameBuffer
    private boolean createFrameBuffer() {
        GLES20.glGenFramebuffers(1, fFrame, 0);
        GLES20.glGenRenderbuffers(1, fRender, 0);

        genTextures();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fFrame[0]);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, fRender[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width,
                height);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fTexture[0], 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER, fRender[0]);
//        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
//        if(status==GLES20.GL_FRAMEBUFFER_COMPLETE){
//            return true;
//        }
        unBindFrame();
        return false;
    }


    //生成Textures
    private void genTextures() {
        GLES20.glGenTextures(fTextureSize, fTexture, 0);
        for (int i = 0; i < fTextureSize; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fTexture[i]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                    0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        }
    }

    //取消绑定Texture
    private void unBindFrame() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void setIntensity(float intensity) {
        lookupFilter.setIntensity(intensity);
    }

    public void setFlag(int flag) {
        beautyFilter.setFlag(flag);
    }

//    /**
//     * 创建fbo
//     *
//     * @param w
//     * @param h
//     */
//    public void createFBO(int w, int h) {
//        //1. 创建FBO
//        int[] fbos = new int[1];
//        GLES20.glGenFramebuffers(1, fbos, 0);
//        fboId = fbos[0];
//        //2. 绑定FBO
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
//
//        //3. 创建FBO纹理
//        int[] textureIds = new int[1];
//        //创建纹理
//        GLES20.glGenTextures(1, textureIds, 0);
//        outTexture = textureIds[0];
//        //绑定纹理
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, outTexture);
//        //环绕（超出纹理坐标范围）  （s==x t==y GL_REPEAT 重复）
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
//        //过滤（纹理像素映射到坐标点）  （缩小、放大：GL_LINEAR线性）
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//
//        //4. 把纹理绑定到FBO
//        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
//                GLES20.GL_TEXTURE_2D, outTexture, 0);
//
//        //5. 设置FBO分配内存大小
//        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, w, h,
//                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
//
//        //6. 检测是否绑定从成功
//        if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
//                != GLES20.GL_FRAMEBUFFER_COMPLETE) {
//        }
//        //7. 解绑纹理和FBO
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//    }
}
