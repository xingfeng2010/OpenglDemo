package opengl.xingfeng.com.opengldemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import opengl.xingfeng.com.opengldemo.firework.FireworkActivity;
import opengl.xingfeng.com.opengldemo.heightmap.HeightmapActivity;
import opengl.xingfeng.com.opengldemo.machinestate.StateActivity;
import opengl.xingfeng.com.opengldemo.particles.ParticlesActivity;
import opengl.xingfeng.com.opengldemo.record.RecordMainActivity;
import opengl.xingfeng.com.opengldemo.texturecompress.CompressedTextureActivity;
import opengl.xingfeng.com.opengldemo.water.ESWaterActivity;

public class StartMainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private ListView mListView;
    private LayoutInflater mLayoutInflator;
    private Class[] classes = new Class[] {
            MainActivity.class,
            Coordinate.class,
            EglDemoActivity.class,
            FBOActivity.class,
            ESWaterActivity.class,
            RecordMainActivity.class,
            ParticlesActivity.class,
            FireworkActivity.class,
            HeightmapActivity.class,
            CompressedTextureActivity.class,
            StateActivity.class
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);

        mListView = findViewById(R.id.list_view);

        mListView.setAdapter(new MyBaseAdapter());
        mListView.setOnItemClickListener(this);

        mLayoutInflator = LayoutInflater.from(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
         Intent intent = new Intent(StartMainActivity.this, classes[i]);
         StartMainActivity.this.startActivity(intent);
    }

    private class MyBaseAdapter extends BaseAdapter {
        private String[] classDescription = new String[] {
                "CustomRender",
                "CoordinateRender and TextureRender",
                "EGLDemo",
                "FBODemo",
                "OPENGLES 添加水印",
                "音频视频合成",
                "粒子动画",
                "烟花效果",
                "地图效果",
                "纹理压缩",
                "状态机示例"
        };

        @Override
        public int getCount() {
            return classDescription.length;
        }

        @Override
        public String getItem(int i) {
            return classDescription[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mLayoutInflator.inflate(R.layout.class_item,parent, false);
                viewHolder.textView = (TextView) view.findViewById(R.id.item_tv);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)view.getTag();
            }

            viewHolder.textView.setText(classDescription[position]);

            return view;
        }
    }

    private static class ViewHolder {
        private TextView textView;
    }
}
