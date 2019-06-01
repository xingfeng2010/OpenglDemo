package opengl.xingfeng.com.opengldemo.record;

import android.content.Context;

public class VideoEncodeRecode extends BaseVideoEncoder{
    public VideoEncodeRecode(Context context, int textureId) {
        super(context);

        setRender(new VideoEncodeRender(context, textureId));
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }
}
