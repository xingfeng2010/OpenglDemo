package opengl.xingfeng.com.opengldemo.texturecompress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.ETC1Util;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import opengl.xingfeng.com.opengldemo.R;

public class CompressedTextureActivity extends AppCompatActivity {
    private final static String TAG = "CompressedTexture";
    /**
     * Choose between creating a compressed texture on the fly or
     * loading a compressed texture from a resource.
     */
    private final static boolean TEST_CREATE_TEXTURE = false;
    /**
     * When creating a compressed texture on the fly, choose
     * whether or not to use the i/o stream APIs.
     */
    private final static boolean USE_STREAM_IO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new GLSurfaceView(this);
        mGLView.setEGLConfigChooser(false);
        StaticTriangleRenderer.TextureLoader loader;
        if (TEST_CREATE_TEXTURE) {
            loader = new SyntheticCompressedTextureLoader();
        } else {
            loader = new CompressedTextureLoader();
        }
        mGLView.setRenderer(new StaticTriangleRenderer(this, loader));
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    /**
     * Demonstrate how to load a compressed texture from an APK resource.
     */
    private class CompressedTextureLoader implements StaticTriangleRenderer.TextureLoader {
        public void load(GL10 gl) {
            Log.w(TAG, "ETC1 texture support: " + ETC1Util.isETC1Supported());
            InputStream input = getResources().openRawResource(R.raw.jellyfish);
            try {
                ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0,
                        GLES10.GL_RGB, GLES10.GL_UNSIGNED_SHORT_5_6_5, input);
            } catch (IOException e) {
                Log.w(TAG, "Could not load texture: " + e);
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    // ignore exception thrown from close.
                }
            }
        }
    }

    /**
     * Demonstrate how to create a compressed texture on the fly.
     */
    private class SyntheticCompressedTextureLoader implements StaticTriangleRenderer.TextureLoader {
        public void load(GL10 gl) {
            int width = 128;
            int height = 128;
            Buffer image = createImage(width, height);
            ETC1Util.ETC1Texture etc1Texture = ETC1Util.compressTexture(image, width, height, 3, 3 * width);
            if (USE_STREAM_IO) {
                // Test the ETC1Util APIs for reading and writing compressed textures to I/O streams.
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ETC1Util.writeTexture(etc1Texture, bos);
                    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                    ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0,
                            GLES10.GL_RGB, GLES10.GL_UNSIGNED_SHORT_5_6_5, bis);
                } catch (IOException e) {
                    Log.w(TAG, "Could not load texture: " + e);
                }
            } else {
                ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0,
                        GLES10.GL_RGB, GLES10.GL_UNSIGNED_SHORT_5_6_5, etc1Texture);
            }
        }

        private Buffer createImage(int width, int height) {
            int stride = 3 * width;
            ByteBuffer image = ByteBuffer.allocateDirect(height * stride)
                    .order(ByteOrder.nativeOrder());

            // Fill with a pretty "munching squares" pattern:
            for (int t = 0; t < height; t++) {
                byte red = (byte) (255 - 2 * t);
                byte green = (byte) (2 * t);
                byte blue = 0;
                for (int x = 0; x < width; x++) {
                    int y = x ^ t;
                    image.position(stride * y + x * 3);
                    image.put(red);
                    image.put(green);
                    image.put(blue);
                }
            }
            image.position(0);
            return image;
        }
    }

    private GLSurfaceView mGLView;
}
