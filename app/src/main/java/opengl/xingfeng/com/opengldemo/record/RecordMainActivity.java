package opengl.xingfeng.com.opengldemo.record;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.PermissionUtil;

public class RecordMainActivity extends AppCompatActivity {
    private String[] permissoins = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
    };

    private static final int REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_main);

        if (!PermissionUtil.checkSelfPermission("Manifest.permission.CAMERA")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                this.requestPermissions(permissoins, REQUEST_CODE);
            }
        }
    }

    public void camera(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    public void recode(View view) {
        startActivity(new Intent(this, RecodeActivity.class));
    }
}
