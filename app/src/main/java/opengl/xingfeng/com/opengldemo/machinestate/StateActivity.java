package opengl.xingfeng.com.opengldemo.machinestate;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import opengl.xingfeng.com.opengldemo.R;

public class StateActivity extends AppCompatActivity {

    private MyTestStateMachine mWpsStateMachine;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mWpsStateMachine = new MyTestStateMachine(mHandler);
    }

    public void init(View view) {
        mWpsStateMachine.sendMessage(mHandler.obtainMessage());
    }

    public void active(View view) {
        mWpsStateMachine.sendMessage(mHandler.obtainMessage());
    }

    public void inactive(View view) {
        mWpsStateMachine.sendMessage(mHandler.obtainMessage());
    }
}
