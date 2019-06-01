package opengl.xingfeng.com.opengldemo;

import android.app.Application;

import opengl.xingfeng.com.opengldemo.util.ContextProvider;

public class OpenglApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextProvider.initIfNotInited(getApplicationContext());
    }
}
