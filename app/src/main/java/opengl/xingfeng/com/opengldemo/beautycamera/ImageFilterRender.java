package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

import com.cgfay.filter.glfilter.resource.ResourceCodec;
import com.cgfay.filter.glfilter.resource.ResourceHelper;
import com.cgfay.filter.glfilter.resource.ResourceJsonCodec;
import com.cgfay.filter.glfilter.resource.bean.ResourceData;
import com.cgfay.filter.glfilter.resource.bean.ResourceType;
import com.cgfay.filter.glfilter.stickers.DynamicStickerLoader;
import com.cgfay.filter.glfilter.stickers.bean.DynamicSticker;
import com.cgfay.filter.glfilter.stickers.bean.DynamicStickerNormalData;
import com.cgfay.landmark.FacePointsUtils;
import com.cgfay.landmark.LandmarkEngine;
import com.cgfay.landmark.OneFace;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import opengl.xingfeng.com.opengldemo.util.EasyGlUtils;

import static opengl.xingfeng.com.opengldemo.beautycamera.CameralRenderer.TAG;

public class ImageFilterRender implements CustomSurfaceView.Render {
    private int inputTexture;

    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];

    private ImageFilterProgram mCameraWatermaskProgram;

    private int width, height;

    // 渲染的Image的宽高
    protected int mImageWidth;
    protected int mImageHeight;


    // 贴纸数据
    protected DynamicSticker mDynamicSticker;

    // 贴纸加载器列表
    protected List<DynamicStickerLoader> mStickerLoaderList;

    // 贴纸坐标缓冲
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    // 视椎体缩放倍数，具体数据与setLookAt 和 frustumM有关
    // 备注：setLookAt 和 frustumM 设置的结果导致了视点(eye)到近平面(near)和视点(eye)到贴纸(center)恰好是2倍的关系
    private static final float ProjectionScale = 2.0f;

    // 长宽比
    private float mRatio;

    // 贴纸顶点
    private float[] mStickerVertices = new float[8];

    // 贴纸变换矩阵
    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    // 资源存储路径
    private static final String ResourceDirectory = "Resource";
    // 资源列表
    private static final List<ResourceData> mResourceList = new ArrayList<>();

    public ImageFilterRender(Context context) {
        mCameraWatermaskProgram = new ImageFilterProgram(context);
        mStickerLoaderList = new ArrayList<>();

        mResourceList.add(new ResourceData("cat", "assets://resource/cat.zip", ResourceType.STICKER, "cat", "assets://thumbs/resource/cat.png"));

        // 解压所有资源
        decompressResource(context, mResourceList);
        String folderPath = ResourceHelper.getResourceDirectory(context) + File.separator + mResourceList.get(0).unzipFolder;

        Log.i(TAG,"ImageFilterRender folderPath:" + folderPath);
        try {
            mDynamicSticker = ResourceJsonCodec.decodeStickerData(folderPath);
        } catch (IOException e) {

            Log.i(TAG,"ImageFilterRender IOException:" + e.toString());
            e.printStackTrace();
        } catch (JSONException e) {
            Log.i(TAG,"ImageFilterRender JSONException:" + e.toString());
            e.printStackTrace();
        }

        // 创建贴纸加载器列表
        if (mDynamicSticker != null && mDynamicSticker.dataList != null) {
            for (int i = 0; i < mDynamicSticker.dataList.size(); i++) {
                if (mDynamicSticker.dataList.get(i) instanceof DynamicStickerNormalData) {
                    String path = mDynamicSticker.unzipPath + "/" + mDynamicSticker.dataList.get(i).stickerName;
                    mStickerLoaderList.add(new DynamicStickerLoader(null, mDynamicSticker.dataList.get(i), path));
                }
            }
        }
    }

    @Override
    public void onSurfaceCreated() {
        //启用透明
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        mCameraWatermaskProgram.init();

    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        //宽高
       //GLES20.glViewport(0, 0, width, height);

        this.width = width;
        this.height = height;
        mImageWidth = width;
        mImageHeight = height;

        deleteFrameBuffer();

        GLES20.glGenFramebuffers(1, fFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, fTexture,0,GLES20.GL_RGBA,width,height);
    }

    @Override
    public void onDrawFrame() {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        EasyGlUtils.bindFrameTexture(fFrame[0],fTexture[0]);
        //宽高
        mCameraWatermaskProgram.setTextureId(inputTexture);
        mCameraWatermaskProgram.draw(width, height);

        if (LandmarkEngine.getInstance().hasFace()) {
            Log.i(TAG,"LandmarkEngine hasFace!!");
            // 逐个人脸绘制
            int faceCount = LandmarkEngine.getInstance().getFaceSize();
            for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
                OneFace oneFace = LandmarkEngine.getInstance().getOneFace(faceIndex);
                // 如果置信度大于0.5，表示这是一个正常的人脸，绘制贴纸
                if (oneFace.confidence > 0.5f) {
                    for (int stickerIndex = 0; stickerIndex < mStickerLoaderList.size(); stickerIndex++) {
                        synchronized (this) {
                            mStickerLoaderList.get(stickerIndex).updateStickerTexture();
                            calculateStickerVertices((DynamicStickerNormalData) mStickerLoaderList.get(stickerIndex).getStickerData(),
                                    oneFace);
                            mCameraWatermaskProgram.reinit(mStickerLoaderList.get(stickerIndex).getStickerTexture(), mVertexBuffer, mTextureBuffer);
                            mCameraWatermaskProgram.draw(width, height);
                            //super.drawFrameBuffer(mStickerLoaderList.get(stickerIndex).getStickerTexture(), mVertexBuffer, mTextureBuffer);
                        }
                    }
                }
            }
            GLES30.glFlush();
        } else {
            Log.i(TAG,"LandmarkEngine don't hasFace!!");
        }

        EasyGlUtils.unBindFrameBuffer();
    }


    public void setInputTexture(int inputTexture) {
        this.inputTexture = inputTexture;
    }

    public int getOnputTextureId() {
        return fTexture[0];
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, fFrame, 0);
        GLES20.glDeleteTextures(1, fTexture,0);
    }

    public void setMatrix(float[] matrix) {
        mCameraWatermaskProgram.setMatrix(matrix);
    }

    /**
     * 更新贴纸顶点
     * TODO 待优化的点：消除姿态角误差、姿态角给贴纸偏移量造成的误差
     * @param stickerData
     */
    private void calculateStickerVertices(DynamicStickerNormalData stickerData, OneFace oneFace) {
        if (oneFace == null || oneFace.vertexPoints == null) {
            return;
        }
        // 步骤一、计算贴纸的中心点和顶点坐标
        // 备注：由于frustumM设置的bottom 和top 为 -1.0 和 1.0，这里为了方便计算，直接用高度作为基准值来计算
        // 1.1、计算贴纸相对于人脸的宽高
        float stickerWidth = (float) FacePointsUtils.getDistance(
                (oneFace.vertexPoints[stickerData.startIndex * 2] * 0.5f + 0.5f) * mImageWidth,
                (oneFace.vertexPoints[stickerData.startIndex * 2 + 1] * 0.5f + 0.5f) * mImageHeight,
                (oneFace.vertexPoints[stickerData.endIndex * 2] * 0.5f + 0.5f) * mImageWidth,
                (oneFace.vertexPoints[stickerData.endIndex * 2 + 1] * 0.5f + 0.5f) * mImageHeight) * stickerData.baseScale;
        float stickerHeight = stickerWidth * (float) stickerData.height / (float) stickerData.width;

        // 1.2、根据贴纸的参数计算出中心点的坐标
        float centerX = 0.0f;
        float centerY = 0.0f;
        for (int i = 0; i < stickerData.centerIndexList.length; i++) {
            centerX += (oneFace.vertexPoints[stickerData.centerIndexList[i] * 2] * 0.5f + 0.5f) * mImageWidth;
            centerY += (oneFace.vertexPoints[stickerData.centerIndexList[i] * 2 + 1] * 0.5f + 0.5f) * mImageHeight;
        }
        centerX /= (float) stickerData.centerIndexList.length;
        centerY /= (float) stickerData.centerIndexList.length;
        centerX = centerX / mImageHeight * ProjectionScale;
        centerY = centerY / mImageHeight * ProjectionScale;
        // 1.3、求出真正的中心点顶点坐标，这里由于frustumM设置了长宽比，因此ndc坐标计算时需要变成mRatio:1，这里需要转换一下
        float ndcCenterX = (centerX - mRatio) * ProjectionScale;
        float ndcCenterY = (centerY - 1.0f) * ProjectionScale;

        // 1.4、贴纸的宽高在ndc坐标系中的长度
        float ndcStickerWidth = stickerWidth / mImageHeight * ProjectionScale;
        float ndcStickerHeight = ndcStickerWidth * (float) stickerData.height / (float) stickerData.width;

        // 1.5、根据贴纸参数求偏移的ndc坐标
        float offsetX = (stickerWidth * stickerData.offsetX) / mImageHeight * ProjectionScale;
        float offsetY = (stickerHeight * stickerData.offsetY) / mImageHeight * ProjectionScale;

        // 1.6、贴纸带偏移量的锚点的ndc坐标，即实际贴纸的中心点在OpenGL的顶点坐标系中的位置
        float anchorX = ndcCenterX + offsetX * ProjectionScale;
        float anchorY = ndcCenterY + offsetY * ProjectionScale;

        // 1.7、根据前面的锚点，计算出贴纸实际的顶点坐标
        mStickerVertices[0] = anchorX - ndcStickerWidth; mStickerVertices[1] = anchorY - ndcStickerHeight;
        mStickerVertices[2] = anchorX + ndcStickerWidth; mStickerVertices[3] = anchorY - ndcStickerHeight;
        mStickerVertices[4] = anchorX - ndcStickerWidth; mStickerVertices[5] = anchorY + ndcStickerHeight;
        mStickerVertices[6] = anchorX + ndcStickerWidth; mStickerVertices[7] = anchorY + ndcStickerHeight;
        mVertexBuffer.clear();
        mVertexBuffer.position(0);
        mVertexBuffer.put(mStickerVertices);

        // 步骤二、根据人脸姿态角计算透视变换的总变换矩阵
        // 2.1、将Z轴平移到贴纸中心点，因为贴纸模型矩阵需要做姿态角变换
        // 平移主要是防止贴纸变形
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, ndcCenterX, ndcCenterY, 0);

        // 2.2、贴纸姿态角旋转
        // TODO 人脸关键点给回来的pitch角度似乎不太对？？SDK给过来的pitch角度值太小了，比如抬头低头pitch的实际角度30度了，SDK返回的结果才十几度，后续再看看如何优化
        float pitchAngle = -(float) (oneFace.pitch * 180f / Math.PI);
        float yawAngle = (float) (oneFace.yaw * 180f / Math.PI);
        float rollAngle = (float) (oneFace.roll * 180f / Math.PI);
        // 限定左右扭头幅度不超过50°，销毁人脸关键点SDK带来的偏差
        if (Math.abs(yawAngle) > 50) {
            yawAngle = (yawAngle / Math.abs(yawAngle)) * 50;
        }
        // 限定抬头低头最大角度，消除人脸关键点SDK带来的偏差
        if (Math.abs(pitchAngle) > 30) {
            pitchAngle = (pitchAngle / Math.abs(pitchAngle)) * 30;
        }
        // 贴纸姿态角变换，优先z轴变换，消除手机旋转的角度影响，否则会导致扭头、抬头、低头时贴纸变形的情况
        Matrix.rotateM(mModelMatrix, 0, rollAngle, 0, 0, 1);
        Matrix.rotateM(mModelMatrix, 0, yawAngle, 0, 1, 0);
        Matrix.rotateM(mModelMatrix, 0, pitchAngle, 1, 0, 0);

        // 2.4、将Z轴平移回到原来构建的视椎体的位置，即需要将坐标z轴平移回到屏幕中心，此时才是贴纸的实际模型矩阵
        Matrix.translateM(mModelMatrix, 0, -ndcCenterX, -ndcCenterY, 0);

        // 2.5、计算总变换矩阵。MVPMatrix 的矩阵计算是 MVPMatrix = ProjectionMatrix * ViewMatrix * ModelMatrix
        // 备注：矩阵相乘的顺序不同得到的结果是不一样的，不同的顺序会导致前面计算过程不一致，这点希望大家要注意
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mModelMatrix, 0);
    }


    /**
     * 解压所有资源
     * @param context
     * @param resourceList 资源列表
     */
    public static void decompressResource(Context context, List<ResourceData> resourceList) {
        // 检查路径是否存在
        boolean result = checkResourceDirectory(context);
        // 存放资源路径无法创建，则直接返回
        if (!result) {
            return;
        }
        String resourcePath = getResourceDirectory(context);
        // 解码列表中的所有资源
        for (ResourceData item : resourceList) {
            if (item.type.getIndex() >= 0) {
                if (item.zipPath.startsWith("assets://")) {
                    decompressAsset(context, item.zipPath.substring("assets://".length()), item.unzipFolder, resourcePath);
                } else if (item.zipPath.startsWith("file://")) {    // 绝对目录中的资源
                    decompressFile(item.zipPath.substring("file://".length()), item.unzipFolder, resourcePath);
                }
            }
        }
    }

    /**
     * 检查资源路径是否存在
     * @param context
     */
    private static boolean checkResourceDirectory(Context context) {
        String resourcePath = getResourceDirectory(context);
        File file = new File(resourcePath);
        if (file.exists()) {
            return file.isDirectory();
        }
        return file.mkdirs();
    }

    /**
     * 获取资源路径
     * @param context
     * @return
     */
    public static String getResourceDirectory(Context context) {
        String resourcePath;
        // 判断外部存储是否可用，如果不可用则使用内部存储路径
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            resourcePath = context.getExternalFilesDir(ResourceDirectory).getAbsolutePath();
        } else { // 使用内部存储
            resourcePath = context.getFilesDir() + File.separator + ResourceDirectory;
        }
        return resourcePath;
    }

    /**
     * 解压Asset文件夹目录下的资源
     * @param context
     * @param assetName     assets文件夹路径
     * @param unzipFolder   解压的文件夹名称
     * @param parentFolder  解压目录
     */
    protected static void decompressAsset(Context context, String assetName, String unzipFolder, String parentFolder) {

        // 如果路径已经存在，则直接返回
        if (new File(parentFolder + "/" + unzipFolder).exists()) {
            Log.d(TAG, "decompressAsset: directory " + unzipFolder + " is existed!");
            return;
        }

        // 打开输入流
        AssetManager manager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = manager.open(assetName);
        } catch (IOException e) {
            Log.e(TAG, "decompressAsset: ", e);
            return;
        }

        // 获取所有zip包
        Map<String, ArrayList<ResourceCodec.FileDescription>> dirList = null;
        try {
            dirList = ResourceCodec.getFileFromZip(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "decompressAsset: ", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果zip包不存在，则不做处理
        if (dirList == null) {
            return;
        }

        // 将zip包解压到目录中
        try {
            inputStream = manager.open(assetName);
        } catch (IOException e) {
            Log.e(TAG, "decompressAsset: ", e);
            return;
        }
        try {
            if (inputStream != null) {
                ResourceCodec.unzipToFolder(inputStream, new File(parentFolder), dirList);
            }
        } catch (IOException e) {
            Log.e(TAG, "decompressAsset: ", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压绝对路径目录下的资源
     * @param zipPath       zip绝对路径
     * @param unzipPath     解压的目录
     * @param parentFolder  解压目录
     */
    protected static void decompressFile(String zipPath, String unzipPath, String parentFolder) {
        // 如果资源路径已经存在，则直接返回
        if (new File(parentFolder + "/" + unzipPath).exists()) {
            Log.d(TAG, "decompressFile: directory " + unzipPath + "is existed!");
            return;
        }

        // 打开文件输入流
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(zipPath);
        } catch (IOException e) {
            Log.e(TAG, "decompressFile: ", e);
            return;
        }

        // 获取所有zip包
        Map<String, ArrayList<ResourceCodec.FileDescription>> dirList = null;
        try {
            dirList = ResourceCodec.getFileFromZip(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "decompressFile: ", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果zip包不存在，则不做处理
        if (dirList == null) {
            return;
        }

        // 将zip包解压到目录中
        try {
            inputStream = new FileInputStream(zipPath);
        } catch (IOException e) {
            Log.e(TAG, "decompressFile: ", e);
            return;
        }
        try {
            ResourceCodec.unzipToFolder(inputStream, new File(parentFolder), dirList);
        } catch (IOException e) {
            Log.e(TAG, "decompressFile: ", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
