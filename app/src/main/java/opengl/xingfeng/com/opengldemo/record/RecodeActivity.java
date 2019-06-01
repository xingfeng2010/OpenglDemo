package opengl.xingfeng.com.opengldemo.record;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import opengl.xingfeng.com.opengldemo.R;

import static opengl.xingfeng.com.opengldemo.record.BaseVideoEncoder.TAG;

public class RecodeActivity extends AppCompatActivity {

    private CameraEglSurfaceView cameraEglSurfaceView;
    private Button button;

    private VideoEncodeRecode videoEncodeRecode;
    private PutPcmThread putPcmThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recode);

        cameraEglSurfaceView = findViewById(R.id.camera);
        button = findViewById(R.id.recode);
    }

    public void recode1(View view) {
        if (videoEncodeRecode == null) {
            startRecode(44100, 16, 2);
            button.setText("正在录制");
        } else {
            videoEncodeRecode.stopRecode();
            button.setText("开始录制");
            videoEncodeRecode = null;
        }
    }

    /**
     * @param samplerate 采样率
     * @param bit        位深
     * @param channels   通道
     */
    private void startRecode(int samplerate, int bit, int channels) {
        videoEncodeRecode = new VideoEncodeRecode(this, cameraEglSurfaceView.getTextureId());

        videoEncodeRecode.initEncoder(cameraEglSurfaceView.getEglContext(),
                Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        "testRecode.mp4",
                cameraEglSurfaceView.getCameraPrivewHeight(),//相机的宽和高是相反的
                cameraEglSurfaceView.getCameraPrivewWidth(),
                samplerate, channels, bit
        );

        videoEncodeRecode.setOnMediaInfoListener(new BaseVideoEncoder.OnMediaInfoListener() {
            @Override
            public void onMediaTime(int times) {
                Log.e("zzz", "time = " + times);
            }
        });

        videoEncodeRecode.setOnStatusChangeListener(new BaseVideoEncoder.OnStatusChangeListener() {
            @Override
            public void onStatusChange(STATUS status) {
                if (status == STATUS.START) {
                    putPcmThread = new PutPcmThread(new WeakReference<RecodeActivity>(RecodeActivity.this));
                    putPcmThread.start();
                }
            }
        });

        videoEncodeRecode.startRecode();
    }

    private static class PutPcmThread extends Thread {
        private boolean isExit;
        private WeakReference<RecodeActivity> reference;

        public PutPcmThread(WeakReference<RecodeActivity> reference) {
            this.reference = reference;
        }


        @Override
        public void run() {
            super.run();
            isExit = false;

            Log.i(TAG,"PutPcmThread begin run:");
            InputStream inputStream = null;
            try {
                int s_ = 44100 * 2 * (16 / 2);
                int bufferSize = s_ / 100;


                inputStream = reference.get().getAssets().open("mydream.pcm");
                byte[] buffer = new byte[bufferSize];
                int size = 0;
                Log.i(TAG,"PutPcmThread inputStream:" + inputStream);
                while ((size = inputStream.read(buffer, 0, bufferSize)) != -1) {
                    try {
                        //10毫秒写入一次
                        Thread.sleep(1000 / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (reference.get().videoEncodeRecode == null || isExit) {
                        break;
                    }
                    reference.get().videoEncodeRecode.putPcmData(buffer, size);
                }
            } catch (IOException e) {
                Log.i(TAG,"PutPcmThread exception:" + e);
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
