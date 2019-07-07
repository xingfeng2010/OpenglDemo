package opengl.xingfeng.com.opengldemo.machinestate.simplemachine;

import android.util.Log;

public class NormalStateMachine {

    private static final String TAG = "NormalStateMachine";

    //电梯的四个状态
    static final int FREE_STATE = 1;  //空闲状态
    static final int HASMONEY_STATE = 2;  //有钱状态
    static final int GIVING_STATE = 3;  //出货状态

     int currentState;
     int curretnMoney;

    //开放方法接口，表示投入1块钱
    public void addMoney() {
        //让当前状态对象处理CMD_ADDMONEY消息
        switch (currentState) {
            case FREE_STATE:
                Log.i(TAG,"用户投了1块钱,转换状态");
                curretnMoney ++;
                currentState = HASMONEY_STATE;
                break;
            case HASMONEY_STATE:
                Log.i(TAG,"用户投了1块钱");
                curretnMoney ++;
                break;
            case GIVING_STATE:
                Log.i(TAG,"正在出货，不能投钱");
                break;
        }
        Log.i(TAG,"用户投了1块钱");
       // mCurrentState.processMessage(CMD_ADDMONEY);
    }

    //开放方法接口，表示摇晃退币手柄
    public void returnMoney() {
        //让当前状态对象处理CMD_RETURNMONEY消息
        switch (currentState) {
            case FREE_STATE:
                Log.i(TAG,"你没有钱");
                curretnMoney --;
                break;
            case HASMONEY_STATE:
                curretnMoney --;
                if (curretnMoney <= 0) {
                    curretnMoney = 0;
                    currentState = FREE_STATE;
                 }
                break;
            case GIVING_STATE:
                Log.i(TAG,"正在出货，不能退钱");
                break;
        }
        //mCurrentState.processMessage(CMD_RETURNMONEY);
    }

    //开放方法接口，表示按下<购买>按钮
    public void clickBuy() {
        //让当前状态对象处理CMD_BUY消息
        switch (currentState) {
            case FREE_STATE:
                Log.i(TAG,"你没有钱，请先投钱");
                break;
            case HASMONEY_STATE:
                curretnMoney --;
                currentState = GIVING_STATE;
                if (curretnMoney <= 0) {
                    curretnMoney = 0;
                }
                break;
            case GIVING_STATE:
                Log.i(TAG,"正在出货，不能购买");
                break;
        }
       // mCurrentState.processMessage(CMD_BUY);
    }
}
