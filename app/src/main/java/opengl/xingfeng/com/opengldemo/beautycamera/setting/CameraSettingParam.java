package opengl.xingfeng.com.opengldemo.beautycamera.setting;

import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar;
import opengl.xingfeng.com.opengldemo.view.RecordSpeedLevelBar.RecordSpeed;

public class CameraSettingParam {
    //5中类型 极慢、慢、标准、快、极快
    private RecordSpeed speed;

    //拍照、拍60秒、拍15秒、影集
    private String recodeType;

    //正在拍照
    private boolean takePicture;

    public RecordSpeed getSpeed() {
        return speed;
    }

    public void setSpeed(RecordSpeed speed) {
        this.speed = speed;
    }

    public String getRecodeType() {
        return recodeType;
    }

    public void setRecodeType(String recodeType) {
        this.recodeType = recodeType;
    }

    public boolean isTakePicture() {
        return takePicture;
    }

    public void setTakePicture(boolean takePicture) {
        this.takePicture = takePicture;
    }
}
