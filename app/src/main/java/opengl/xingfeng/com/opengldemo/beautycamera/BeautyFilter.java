package opengl.xingfeng.com.opengldemo.beautycamera;

import android.content.Context;

public class BeautyFilter implements CustomSurfaceView.Render{
    private int textureId;
    private int flag;

    public BeautyFilter(Context context) {
    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame() {

    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
