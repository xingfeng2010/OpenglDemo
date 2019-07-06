package opengl.xingfeng.com.opengldemo.machinestate;

import android.os.Message;
import android.os.ParcelUuid;

public class TankStateMachine extends StateMachine{
    static final int CMD_BASE = 0;
    static final int CMD_ATTACK = CMD_BASE+1;    //有武器就发射；没武器就什么都不干
    static final int CMD_SETUP_BASE = CMD_BASE+2;    //安装基座
    static final int CMD_REMOVE_BASE = CMD_BASE+3;    //卸载基座
    static final int CMD_SETUP_BARREL = CMD_BASE+4;    //安装炮筒
    static final int CMD_REMOVE_BARREL = CMD_BASE+5;    //卸载炮筒
    static final int CMD_SETUP_BIG_BARREL = CMD_BASE+6;    //安装大口径炮筒
    static final int CMD_REMOVE_BIG_BARREL = CMD_BASE+7;    //卸载大口径炮筒
    static final int CMD_ADD_MISSILE = CMD_BASE+8;    //装填导弹
    static final int CMD_LAUNCH_MISSILE = CMD_BASE+9;    //发射导弹
    static final int CMD_REMOVE_MISSLE = CMD_BASE+10;    //移除导弹
    static final int CMD_ADD_ROCKET = CMD_BASE+11;    //装填火箭
    static final int CMD_LAUNCH_ROCKET = CMD_BASE+12;    //发射火箭
    static final int CMD_REMOVE_ROCKET = CMD_BASE+13;    //移除火箭
    private static final int CMD_SETUP_BIGBARREL = CMD_BASE+14;
    private static final int CMD_REMOVE_BIGBARREL = CMD_BASE+15;
    private static final int CMD_BASE_SETUP_SUCCESSED = CMD_BASE+16;

    //这里先创建出一大波Concrete State的实例来
    State mDefaultState;
    State mBaseSetupStartingState;
    State mBaseSetupFinishedState ;
    State mBarrelSetupStartingState ;
    State mBarrelSetupFinishedState ;
    State mBigBarrelSetupStartingState ;
    State mBigBarrelSetupFinishedState ;
    State mAddMissileSuccessedState ;
    State mAddRocketSuccessedState ;

    //构造器里要做的工作：State
    public TankStateMachine(String name) {
        super(null);
        //这里先创建出一大波Concrete State的实例来
        mDefaultState = new DefaultState();
        mBaseSetupStartingState = new BaseSetupStartingState();
        mBaseSetupFinishedState = new BaseSetupFinishedState();
        mBarrelSetupStartingState = new BarrelSetupStartingState();
        mBarrelSetupFinishedState = new BarrelSetupFinishedState();
        mBigBarrelSetupStartingState = new BigBarrelSetupStartingState();
        mBigBarrelSetupFinishedState = new BigBarrelSetupFinishedState();
        mAddMissileSuccessedState = new AddMissileSuccessedState();
        mAddRocketSuccessedState = new AddRocketSuccessedState();

       //按照层级关系将状态添加到状态树中，注意，下面的缩进空格是用于表达树的层次关系的
        addState(mDefaultState);
        addState(mBaseSetupStartingState, mDefaultState);
        addState(mBaseSetupFinishedState, mDefaultState);
        addState(mBarrelSetupStartingState, mBaseSetupStartingState);
        addState(mBarrelSetupFinishedState, mBaseSetupFinishedState);
        addState(mAddMissileSuccessedState, mBarrelSetupFinishedState);
        addState(mBigBarrelSetupStartingState, mBaseSetupFinishedState);
        addState(mBigBarrelSetupFinishedState, mBaseSetupFinishedState);
        addState(mAddRocketSuccessedState, mBarrelSetupFinishedState);

       //设置tank的初始状态为DefaultState，表示tank上还没有安装底座
        setInitialState(mDefaultState);
    }

    //======================下面依次定义所有的状态类============================
    //并没有每一个都实现，实现其中的3个
    //tank什么都没安装时的状态
    class DefaultState extends State {

        //状态被进入时（添加到状态栈）被调用
        @Override
        public void enter() {
            log("进入DefaultState！");
        }

        //状态退出时该方法会被调用
        @Override
        public void exit() {
            log("退出DefaultState！");
        }

        //这里定义状态如何处理message，这里的message不是State模式中的int类型，而是android //framework中的Message类型，可以表达更多的信息
        //这个方法会由SmHandler来负责调用，你只需定义当前状态如何处理Message即可。
        //关于SmHandler会在<step2>中进行介绍
        @Override
        public boolean processMessage(Message message) {
            switch(message.what) {
                case CMD_SETUP_BASE:
                    //直接切换到BaseSetupStartingState进行基座安装工作
                    transitionTo(mBaseSetupStartingState);
                    return HANDLED;

                case CMD_SETUP_BARREL:
                case CMD_SETUP_BIG_BARREL:
                case CMD_ADD_MISSILE:
                case CMD_LAUNCH_MISSILE:
                case CMD_LAUNCH_ROCKET:
                case CMD_ADD_ROCKET:
//想做上面几件事，都得先去装基座
                    transitionTo(mBaseSetupStartingState);
//然后再让新状态去处理这个Message吧
                    deferMessage(message);
                    return HANDLED;
//底下这些状态都没法处理
                case CMD_REMOVE_BASE:
                case CMD_REMOVE_BARREL:
                case CMD_REMOVE_BIG_BARREL:
                case CMD_REMOVE_MISSLE:
                case CMD_REMOVE_ROCKET:
                case CMD_ATTACK:
                default:
                    return NOT_HANDLED;
            }
        }

    }

    //基座安装中的状态
    class BaseSetupStartingState extends State {

        @Override
        public void enter() {
            log("进入BaseSetupStartingState！");
            //Ok,这里要调用其他真正进行基座安装的类进行基座的实际安装动作
            //比如说调用Tank类的旋转基座接口到合适角度的方法，
            //发送个消息给其他的类，告诉他们，干活吧，去安装基座
            //...
            //然后就等待基座安装成功的消息：CMD_BASE_SETUP_SUCCESSED发送过来即可
        }

        @Override
        public void exit() {
            //停下手头做的一切，
            //同时拆除已经安装完成的部分
            log("退出BaseSetupStartingState！");
        }

        @Override
        public boolean processMessage(Message message) {
            switch(message.what) {
                case CMD_BASE_SETUP_SUCCESSED:
                //该条命令表明，其他真正干活的类已经把基座给安装好了，
                //TankStateMachine需要切换状态
                    transitionTo(mBaseSetupFinishedState);
                    return HANDLED;
                case CMD_REMOVE_BASE:
                    //立刻做一些停止基座安装的操作，再进行已经完成部分的拆除工作

                    //最后切换到DefaultState
                    transitionTo(mDefaultState);
                    return HANDLED;
                    //即使返回false也是交给DefaultState处理，并没有什么用，
                    //直接true结束这几个cmd
                case CMD_ADD_MISSILE:
                case CMD_LAUNCH_MISSILE:
                case CMD_ADD_ROCKET:
                case CMD_LAUNCH_ROCKET:
                case CMD_ATTACK:
                case CMD_SETUP_BASE:
                case CMD_SETUP_BARREL:
                case CMD_SETUP_BIG_BARREL:
                    //这需要接下来转换到的状态去解决些Message
                    deferMessage(message);
                    break;
                case CMD_REMOVE_BARREL:
                case CMD_REMOVE_BIG_BARREL:
                case CMD_REMOVE_MISSLE:
                case CMD_REMOVE_ROCKET:
                default:
                    //返回false代表当前状态无法处理，
                    //看看父状态能不能处理吧
                    return NOT_HANDLED;
            }
                //返回true代表Message已经被正确地处理了
            return HANDLED;
        }

    }

    //基座安装完毕的状态
    class BaseSetupFinishedState extends State {

    }

    //炮筒安装中的状态，必须要在基座安装完毕才能进入该状态
    class BarrelSetupStartingState extends State {

    }

    //炮筒安装完毕的状态，必须要在基座安装完毕才能进入该状态
    class BarrelSetupFinishedState extends State {

    }

    //大口径炮筒安装中的状态，必须要在基座安装完毕才能进入该状态
    class BigBarrelSetupStartingState extends State {

    }

    //大口径炮筒安装完毕的状态，必须要在基座安装完毕才能进入该状态
    class BigBarrelSetupFinishedState extends State {

    }

    //成功装填普通导弹的状态，必须要在炮筒安装完毕才能进入该状态
    class AddMissileSuccessedState extends State {

        @Override
        public void enter() {
            //通知状态显示仪表，已经安装好了导弹
            log("进入AddMissileSuccessedState！");
        }

        @Override
        public void exit() {
            //通知状态显示仪表，导弹被移除
            //把导弹移除掉！
            log("退出AddMissileSuccessedState！");
        }

        @Override
        public boolean processMessage(Message message) {
            switch(message.what) {
                case CMD_ATTACK:
                    //调用各种发射炮弹需要的方法，例如按下控制台的“发射”按钮
                    //但是又没有子弹了，需要转换到BarrelSetupFinishedState
                    transitionTo(mBarrelSetupFinishedState);
                    return HANDLED;
                case CMD_REMOVE_MISSLE:
                    //调用小兵把导弹拿出来
                    transitionTo(mBarrelSetupFinishedState);
                    return HANDLED;
                case CMD_LAUNCH_MISSILE:
                    //调用各种发射炮弹需要的方法，例如按下控制台的“发射”按钮
                    //但是又没有子弹了，需要转换到BarrelSetupFinishedState
                    transitionTo(mBarrelSetupFinishedState);
                    return HANDLED;
                    //这些问题显然应该交给父状态去看看怎么处理，
                    //并不是当前状态能够处理的
                case CMD_SETUP_BASE:
                case CMD_REMOVE_BASE:
                case CMD_SETUP_BARREL:
                case CMD_REMOVE_BARREL:
                case CMD_SETUP_BIG_BARREL:
                case CMD_REMOVE_BIG_BARREL:
                case CMD_ADD_MISSILE:
                   //log(已经有子弹了！)
                case CMD_ADD_ROCKET:
                case CMD_LAUNCH_ROCKET:
                case CMD_REMOVE_ROCKET:
                default:
                    return NOT_HANDLED;
            }
        }

    }

    //成功装填普通火箭的状态，必须要在炮筒安装完毕才能进入该状态
    class AddRocketSuccessedState extends State {

    }

    public void setupBase() {
        sendMessage(CMD_SETUP_BASE);
    }

    //安装普通口径炮筒
    public void setupBarrel() {
        sendMessage(CMD_SETUP_BARREL);
    }

    //装填导弹（普通口径炮筒才能装填）
    public void addMissile() {
        sendMessage(CMD_ADD_MISSILE);
    }

    //装填火箭（大口径炮筒才能装填）
    public void addRocket() {
        sendMessage(CMD_ADD_ROCKET);
    }

    //卸载底座
    public void removeBase() {
        sendMessage(CMD_REMOVE_BASE);
    }

    //安装大口径炮筒
    public void setupBigBarrel() {
        sendMessage(CMD_SETUP_BIGBARREL);
    }

    //卸载普通炮筒
    public void removeBarrel() {
        sendMessage(CMD_REMOVE_BARREL);
    }

    //卸载大口径炮筒
    public void removeBigBarrel() {
        sendMessage(CMD_REMOVE_BIGBARREL);
    }

    //发射，如果已经装填了就发射，无论是装填的是什么；否则就不发射
    public void launch() {
        sendMessage(CMD_ATTACK);
    }

    //发射导弹
    public void launchMissile() {
        sendMessage(CMD_LAUNCH_MISSILE);
    }

    //发射火箭
    public void launchRocket() {
        sendMessage(CMD_LAUNCH_ROCKET);
    }
}
