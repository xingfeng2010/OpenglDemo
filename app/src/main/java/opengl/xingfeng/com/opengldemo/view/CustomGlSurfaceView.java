package opengl.xingfeng.com.opengldemo.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.webkit.RenderProcessGoneDetail;

import opengl.xingfeng.com.opengldemo.render.CustomRender;

public class CustomGlSurfaceView extends GLSurfaceView {
    private CustomRender customRender;
    private final float TOUCH_SCAL_FACTOR = 180.0f / 320f;
    private float mPreviousX;
    private float mPreviousY;

    public CustomGlSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(3);
        customRender = new CustomRender(context);
        setRenderer(customRender);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (y > getHeight() / 2) {
                    dx = dx * -1;
                }

                if (x < getWidth() /2 ) {
                    dy = dy * -1;
                }

                customRender.setAngle(customRender.getAngle() + ((dx + dy) * TOUCH_SCAL_FACTOR));
                requestRender();
        }

        mPreviousY = y;
        mPreviousX = x;

        return true;
    }

}
