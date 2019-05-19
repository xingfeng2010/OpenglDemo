package opengl.xingfeng.com.opengldemo.util;

import android.media.MediaCodecList;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class VideoSupportUtil {
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void listSupportType() {
        int count = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++) {
            String[] types = MediaCodecList.getCodecInfoAt(i).getSupportedTypes();
            for (String str : types) {
                Log.i("listSupportType", "type:" + str);
            }
        }
    }
}
