package opengl.xingfeng.com.opengldemo.recorder;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaMuxerWrapper {
    private static final boolean DEBUG = true;
    private static final String TAG = "MediaMuxerWrapper";

    private MediaMuxer mMediaMuxer;
    private int mEncoderCount, mStatredCount;
    private volatile boolean mIsPaused;
    private boolean mIsStarted;

    public MediaMuxerWrapper(String path) {
        try {
            mMediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mEncoderCount = mStatredCount = 0;
        mIsStarted = false;
    }

    public synchronized void addEncoder() {
        mEncoderCount ++;
    }

    public synchronized void stop() {
        mStatredCount--;
        if ((mEncoderCount > 0) && (mStatredCount <= 0)) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mIsStarted = false;
            if (DEBUG) Log.v(TAG,  "MediaMuxer stopped:");
        }

    }

    public synchronized void writeSampleData(int track, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        if (mStatredCount > 0) {
            mMediaMuxer.writeSampleData(track, byteBuffer, bufferInfo);
        }
    }

    public synchronized boolean isStarted() {
        return mIsStarted;
    }

    public synchronized int addTrack(MediaFormat mediaFormat) {
        if (mIsStarted)
            throw new IllegalStateException("muxer already started");

        int trackIndex = mMediaMuxer.addTrack(mediaFormat);

        Log.i(TAG,"addTrack format:" + mediaFormat.toString());
        Log.i(TAG,"addTrack trackIndex:" + trackIndex);
        return trackIndex;
    }

    public synchronized boolean start() {
        mStatredCount++;
        if ((mEncoderCount > 0) && (mStatredCount == mEncoderCount)) {
            mMediaMuxer.start();
            mIsStarted = true;
            notifyAll();
            if (DEBUG) Log.v(TAG,  "MediaMuxer started:");
        }
        return mIsStarted;
    }
}
