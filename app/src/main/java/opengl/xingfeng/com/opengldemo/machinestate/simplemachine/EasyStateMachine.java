package opengl.xingfeng.com.opengldemo.machinestate.simplemachine;

import android.util.Log;

public class EasyStateMachine {
    private static final String TAG = "EasyStateMachine";

    IEasyState freestate;
    IEasyState hasmoneystate;
    IEasyState givingstate;


    IEasyState currentState;
    int curretnMoney;

    public void EasyStateMachine() {
        freestate = new FreeState();
        hasmoneystate = new HasMoneyState();
        givingstate = new GivingState();
    }


    class FreeState implements IEasyState {

        @Override
        public void addMoney() {
            Log.i(TAG,"用户投了1块钱,转换状态");
            curretnMoney ++;
            currentState = hasmoneystate;
        }

        @Override
        public void clickBuy() {
            Log.i(TAG,"你没有钱，请先投钱");
        }

        @Override
        public void returnMoney() {
            Log.i(TAG,"你没有钱");
            curretnMoney --;
        }
    }

    class HasMoneyState implements IEasyState {

        @Override
        public void addMoney() {
            Log.i(TAG,"用户投了1块钱");
            curretnMoney ++;
        }

        @Override
        public void clickBuy() {
            curretnMoney --;
            currentState = givingstate;
            if (curretnMoney <= 0) {
                curretnMoney = 0;
            }
        }

        @Override
        public void returnMoney() {
            curretnMoney --;
            if (curretnMoney <= 0) {
                curretnMoney = 0;
                currentState = freestate;
            }
        }
    }

    class GivingState implements IEasyState {

        @Override
        public void addMoney() {
            Log.i(TAG,"正在出货，不能投钱");
        }

        @Override
        public void clickBuy() {
            Log.i(TAG,"正在出货，不能购买");
        }

        @Override
        public void returnMoney() {
            Log.i(TAG,"正在出货，不能退钱");
        }
    }   
}
