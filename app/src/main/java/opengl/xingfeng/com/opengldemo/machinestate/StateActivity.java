package opengl.xingfeng.com.opengldemo.machinestate;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import opengl.xingfeng.com.opengldemo.machinestate.simplemachine.*;

import opengl.xingfeng.com.opengldemo.R;

public class StateActivity extends AppCompatActivity implements StateListener{

    private DrinkMachine mWpsStateMachine;
    private Handler mHandler = new Handler();
    private Button mPushButton;
    private Button mBuyButton;
    private TextView mStateTV;

    SimpleDrinkMachine mSimpleDrinkMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        mPushButton = (Button) findViewById(R.id.push_btn);
        mBuyButton = (Button) findViewById(R.id.buy_btn);
        mStateTV = (TextView) findViewById(R.id.curretn_tips);

        //mWpsStateMachine = new DrinkMachine(mHandler);
        mSimpleDrinkMachine = new SimpleDrinkMachine(this, this);
        mStateTV.setText("当前状态：" + "FreeState" + "\t" + " 当前金币：" + 0);
    }

    public void pushMoney(View view) {
        mSimpleDrinkMachine.addMoney();
    }

    public void buyDring(View view) {
        mSimpleDrinkMachine.clickBuy();
    }

    public void tuiBi(View view) {
        mSimpleDrinkMachine.returnMoney();
    }

    @Override
    public void stateCanged(String currentState, String leftMoney) {
        mStateTV.setText("当前状态：" + currentState + "\t" + " 当前金币：" + leftMoney);
    }
}
