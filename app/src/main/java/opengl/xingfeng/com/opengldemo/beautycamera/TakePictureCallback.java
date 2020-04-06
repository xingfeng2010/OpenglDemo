package opengl.xingfeng.com.opengldemo.beautycamera;

import java.nio.ByteBuffer;

public interface TakePictureCallback {
    void onTakePicture(ByteBuffer buffer, int width, int height);
}
