package opengl.xingfeng.com.opengldemo.beautycamera;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import opengl.xingfeng.com.opengldemo.R;
import opengl.xingfeng.com.opengldemo.util.PermissionUtils;

public class BeautyPermissionActivity extends AppCompatActivity implements Runnable{

    private static final int REQUEST_CODE = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_permission);

        PermissionUtils.askPermission(this,new String[]{Manifest.permission.CAMERA, Manifest
                .permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionUtils.onRequestPermissionsResult(requestCode == REQUEST_CODE, grantResults, this, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BeautyPermissionActivity.this, "授权失败！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void run() {
        Intent intent = new Intent(this, BeautyCamera.class);
        startActivity(intent);
        this.finish();
    }
}
