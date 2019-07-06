package opengl.xingfeng.com.opengldemo.machinestate.simplemachine;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import opengl.xingfeng.com.opengldemo.machinestate.StateListener;

public class SimpleDrinkMachine {
    String TAG = "SimpleDrinkMachine";
    private int mRestMoney = 0;   //这表示饮料机中还有多少钱
    private SimpleState mCurrentState = null;   //记录当前饮料机所处的状态
    //所有的状态实例
    private SimpleState freestate;
    private SimpleState hasmoneystate;
    private SimpleState givingstate;

    //===========一下是一批Message定义==============
    static final int CMD_BASE = 0;
    static final int CMD_ADDMONEY = CMD_BASE + 1;
    static final int CMD_RETURNMONEY = CMD_BASE + 2;
    static final int CMD_BUY = CMD_BASE + 3;
    static final int CMD_GIVEDRINK = CMD_BASE + 4; //该命令让饮料机出货

    //===========以下是一批状态类定义===============
    //饮料机无所事事的状态
    class FreeState extends SimpleState {

        @Override
        public void processMessage(int message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message) {
                case CMD_ADDMONEY: {
                    //把机器中的钱数给加上
                    mRestMoney++;
                    //切换到有钱状态
                    mCurrentState = hasmoneystate;
                    break;
                }
                case CMD_RETURNMONEY: {
                    Log.i(TAG,"别折腾，没用的，不会掉钱下来的");
                    Toast.makeText(mContext,"别折腾，没用的，不会掉钱下来的", Toast.LENGTH_SHORT).show();
                    break;
                }
                case CMD_BUY: {
                    Log.i(TAG,"不会掉饮料下来的，先投钱吧");
                    Toast.makeText(mContext,"不会掉饮料下来的，先投钱吧", Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {
                    break;
                }
            }
            log();
        }

    }

    //饮料机中有钱的状态
    class HasMoneyState extends SimpleState {

        @Override
        public void processMessage(int message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message) {
                case CMD_ADDMONEY: {
                    //除了变得更加有钱以外，没有其他的了
                    mRestMoney++;
                    break;
                }
                case CMD_RETURNMONEY: {
                    //退钱，并且回到空闲状态
                    mRestMoney = 0;
                    mCurrentState = freestate;
                    break;
                }
                case CMD_BUY: {
                    if (mRestMoney >= 2) {
                        //一瓶饮料两块钱，够了,可以进入出货状态了
                        mRestMoney -= 2;
                        mCurrentState = givingstate;
                        log();
                        Toast.makeText(mContext,"出货中", Toast.LENGTH_SHORT).show();
                        //发出出货命令
                        mCurrentState.processMessage(CMD_GIVEDRINK);
                        break;
                    } else {
                        Toast.makeText(mContext,"钱不够，请先投钱吧", Toast.LENGTH_SHORT).show();
                    }
                }
                default: {
                    break;
                }
            }
            log();
        }

    }

    //饮料机正在出货的状态
    class GivingState extends SimpleState {

        @Override
        public void processMessage(int message) {
            // TODO Auto-generated method stub
            //super.processMessage(message);
            switch (message) {
                case CMD_GIVEDRINK: {
                    try {
                        //机器在2秒钟里什么都不做，专心处理出货动作
                        Thread.sleep(2000);
                        //货已经出了，根据剩余的钱来看看切换到什么状态吧
                        if (mRestMoney > 0) {
                            //还有钱
                            mCurrentState = hasmoneystate;
                        } else {
                            //已没钱
                            mCurrentState = freestate;
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
            log();
        }

    }

    private StateListener mStateListener;
    private Context mContext;

    //构造器中完成state对象创建以及初始化当前状态的操作
    public SimpleDrinkMachine(Context context, StateListener listener) {
        //分别创建每个State的实例
        freestate = new FreeState();
        hasmoneystate = new HasMoneyState();
        givingstate = new GivingState();
        //设置初始状态
        mCurrentState = freestate;

        mStateListener = listener;
        mContext = context;
    }

    //开放方法接口，表示投入1块钱
    public void addMoney() {
        //让当前状态对象处理CMD_ADDMONEY消息
        Log.i(TAG,"用户投了1块钱");
        mCurrentState.processMessage(CMD_ADDMONEY);
    }

    //开放方法接口，表示摇晃退币手柄
    public void returnMoney() {
        //让当前状态对象处理CMD_RETURNMONEY消息
        Log.i(TAG,"用户摇了退币手柄");
        mCurrentState.processMessage(CMD_RETURNMONEY);
    }

    //开放方法接口，表示按下<购买>按钮
    public void clickBuy() {
        //让当前状态对象处理CMD_BUY消息
        Log.i(TAG,"用户按了<购买>按钮");
        mCurrentState.processMessage(CMD_BUY);
    }

    //输出当前状态以及剩余钱树的log方法
    private void log() {
        if (mCurrentState == null) {
            return;
        }
        String statename = "";
        if (mCurrentState instanceof FreeState) {
            statename = "FreeState";
        } else if (mCurrentState instanceof HasMoneyState) {
            statename = "HasMoneyState";
        } else if (mCurrentState instanceof GivingState) {
            statename = "GivingState";
        }
        System.out.print("处理完Message以后，状态：" + statename);
        mStateListener.stateCanged(statename, mRestMoney + "");
        Log.i(TAG,",余额：" + mRestMoney + "\n");
    }
}
