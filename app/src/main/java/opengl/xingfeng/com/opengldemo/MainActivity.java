package opengl.xingfeng.com.opengldemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import opengl.xingfeng.com.opengldemo.view.CustomGlSurfaceView;

public class MainActivity extends AppCompatActivity {

    private CustomGlSurfaceView customGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customGlSurfaceView = new CustomGlSurfaceView(this);
        setContentView(customGlSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        customGlSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        customGlSurfaceView.onResume();
    }
}
