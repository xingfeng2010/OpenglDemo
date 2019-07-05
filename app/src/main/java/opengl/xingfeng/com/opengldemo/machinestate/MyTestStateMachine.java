package opengl.xingfeng.com.opengldemo.machinestate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyTestStateMachine extends StateMachine {
    private static final String TAG = "MyTestStateMachine";
    private static final boolean DBG = true;

    private State mDefaultState = new DefaultState();
    private State mInactiveState = new InactiveState();
    private State mActiveState = new ActiveState();

    public MyTestStateMachine(Handler handler) {
        super(TAG, handler.getLooper());

        addState(mDefaultState);
        addState(mInactiveState, mDefaultState);
        addState(mActiveState, mDefaultState);

        setInitialState(mInactiveState);

        //start the state machine
        start();
    }

    class DefaultState extends State {
        @Override
        public void enter() {
            if (DBG) Log.d(TAG, "enter"  + getName() + "\n");
        }
        @Override
        public boolean processMessage(Message message) {
            if (DBG) Log.d(TAG, "processMessage"  + getName() + message.toString() + "\n");
            transitionTo(mActiveState);
            return HANDLED;
        }
    }

    class ActiveState extends State {
        @Override
        public void enter() {
            if (DBG) Log.d(TAG, "enter"  + getName() + "\n");
        }

        @Override
        public boolean processMessage(Message message) {
            boolean retValue = HANDLED;
            if (DBG) Log.d(TAG, "processMessage" + getName() + message.toString() + "\n");

            transitionTo(mInactiveState);
            return retValue;
        }
    }

    class InactiveState extends State {
        @Override
        public void enter() {
            if (DBG) Log.d(TAG, "enter" + getName() + "\n");
        }

        @Override
        public boolean processMessage(Message message) {
            boolean retValue = NOT_HANDLED;
            if (DBG) Log.d(TAG, "processMessage" + getName() + message.toString() + "\n");
            return retValue;
        }
    }
}
