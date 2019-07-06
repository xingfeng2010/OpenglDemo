package opengl.xingfeng.com.opengldemo.machinestate;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DrinkMachine extends StateMachine {
    //===========一下是一批Message定义==============
    static final int CMD_BASE = 0;
    static final int CMD_ADDMONEY = CMD_BASE + 1;
    static final int CMD_RETURNMONEY = CMD_BASE + 2;
    static final int CMD_BUY = CMD_BASE + 3;
    static final int CMD_GIVEDRINK = CMD_BASE + 4; //该命令让饮料机出货
    private static final String TAG = "MyTestStateMachine";
    private static final boolean DBG = true;
    private State mFreeState = new FreeState();
    private State mHasMoneyState = new HasMoneyState();
    private State mGivingState = new GivingState();
    private int mRestMoney = 0;   //这表示饮料机中还有多少钱


    public DrinkMachine(Handler handler) {
        super(TAG, handler.getLooper());

        addState(mFreeState);
        addState(mHasMoneyState, mFreeState);
        addState(mGivingState, mHasMoneyState);

        setInitialState(mFreeState);

        //start the state machine
        start();
    }


    //===========以下是一批状态类定义===============
    //饮料机无所事事的状态
    class FreeState extends State {

        @Override
        public boolean processMessage(Message message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message.what) {
                case CMD_ADDMONEY: {
                    //把机器中的钱数给加上
                    mRestMoney++;
                    //切换到有钱状态
                    transitionTo(mHasMoneyState);
                    break;
                }
                case CMD_RETURNMONEY: {
                    Log.i(TAG,"别折腾，没用的，不会掉钱下来的");
                    break;
                }
                case CMD_BUY: {
                    Log.i(TAG,"不会掉饮料下来的，先投钱吧");
                    break;
                }
                default: {
                    break;
                }
            }

            return true;
        }

    }

    //饮料机中有钱的状态
    class HasMoneyState extends State {

        @Override
        public boolean processMessage(Message message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message.what) {
                case CMD_ADDMONEY: {
                    //除了变得更加有钱以外，没有其他的了
                    mRestMoney++;
                    break;
                }
                case CMD_RETURNMONEY: {
                    //退钱，并且回到空闲状态
                    mRestMoney = 0;
                    transitionTo(mFreeState);
                    break;
                }
                case CMD_BUY: {
                    if (mRestMoney >= 2) {
                        //一瓶饮料两块钱，够了,可以进入出货状态了
                        mRestMoney -= 2;
                        transitionTo(mGivingState);
                        //发出出货命令
                        sendMessage(obtainMessage(CMD_GIVEDRINK));
                        break;
                    }
                }
                default:
                    break;
                }

                return true;
            }

        }

    //饮料机正在出货的状态
    class GivingState extends State {

        @Override
        public boolean processMessage(Message message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message.what) {
                case CMD_GIVEDRINK: {
                    try {
                        //机器在2秒钟里什么都不做，专心处理出货动作
                        Thread.sleep(2000);
                        //货已经出了，根据剩余的钱来看看切换到什么状态吧
                        if (mRestMoney > 0) {
                            //还有钱
                            transitionTo(mHasMoneyState);
                        } else {
                            //已没钱
                            transitionTo(mFreeState);
                        }
                        break;
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                default: {
                    break;
                }
            }

            return true;
        }

    }
}
