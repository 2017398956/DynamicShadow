package com.wwq.self_shadow.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.wwq.pluginlibrary.GeneratedShadowActivityDelegate;
import com.wwq.pluginlibrary.shadow.interfaces.HostActivityDelegate;
import com.wwq.pluginlibrary.host.interfaces.HostActivityDelegator;
import com.wwq.pluginlibrary.MixResources;
import com.wwq.pluginlibrary.shadow.activity.ShadowActivity;
import com.wwq.self_shadow.Constant;
import com.wwq.self_shadow.PackageResManager;

import java.io.FileInputStream;
import java.io.IOException;

import static com.wwq.self_shadow.PPService.baseDexClassLoader;

/**
 * 为启动插件提供相关资源，理论上每个插件都有一个实例，
 * TODO 但是 mPluginResources 参数是全局的了，这里要注意一下（但是一般不会出现插件嵌套插件的情况，所以正常情况下也不会有问题）
 */
public class ShadowActivityDelegate extends GeneratedShadowActivityDelegate implements HostActivityDelegate {
    private HostActivityDelegator mHostActivityDelegator;
    public static Resources mPluginResources;
    private boolean mDependenciesInjected = false;

    @Override
    public void setDelegator(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    @Override
    public Object getPluginActivity() {
        return pluginActivity;
    }

    @Override
    public String getLoaderVersion() {
        return null;
    }

    @Override
    public boolean isChangingConfigurations() {
        return false;
    }

    @Override
    public void finish() {
        mHostActivityDelegator.superFinish();
    }

    @Override
    public ClassLoader getClassLoader() {
        return baseDexClassLoader;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return null;
    }

    @Override
    public Resources getResources() {
        if (mDependenciesInjected) {
            return mMixResources;
        } else {
            //预期只有android.view.Window.getDefaultFeatures会调用到这个分支，此时我们还无法确定插件资源
            //而getDefaultFeatures只需要访问系统资源
            return Resources.getSystem();
        }
    }

    @Override
    public boolean onNavigateUpFromChild(Activity arg0) {
        return false;
    }

    private MixResources mMixResources;

    @Override
    public void onCreate(Bundle arg0, Object arg1) {
        mMixResources = new MixResources(mHostActivityDelegator.superGetResources(), mPluginResources);
        mDependenciesInjected = true;
    }

    @Override
    public void onChildTitleChanged(Activity arg0, CharSequence arg1) {

    }


    private ShadowActivity shadowActivity;

    @Override
    public void onCreate(Bundle arg0) {

        Log.d("shadow_ca", "onCreate..."+mPluginResources);
        Log.e(Constant.TAG,"currentProcess activity 1: "+getCurrentProcessName());
//        try {
            mMixResources = new MixResources(mHostActivityDelegator.superGetResources(), mPluginResources);
            mDependenciesInjected=true;
        Object o1 = null;
        try {
            String className = arg0.getString("className");
            Log.d(Constant.TAG,"className = "+className);
            // "com.wwq.shadow_demo.MainActivity"
            o1 = baseDexClassLoader.loadClass(className).newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//            Object o1 = baseDexClassLoader.loadClass("com.wwq.shadow_demo.TestService").newInstance();
//            final Service service = (Service) o1;
//            Log.d("shadow_ca", "onCreate...shadowActivity ="+o);
//            service.onCreate();

            shadowActivity = (ShadowActivity) o1;

            Log.d("shadow_ca", "onCreate...1 ="+shadowActivity);
            shadowActivity.setHostActivityDelegator(mHostActivityDelegator);
            shadowActivity.setPluginResources(mPluginResources);
            shadowActivity.setPluginClassLoader(baseDexClassLoader);
            shadowActivity.setApplicationInfo(PackageResManager.applicationInfo);
            shadowActivity.setHostContextAsBase((Context) mHostActivityDelegator.getHostActivity());
            shadowActivity.setPluginComponentLauncher(new PluginComponentLauncherImpl());
            super.pluginActivity = shadowActivity;
            Log.e(Constant.TAG,"pluginActivity 2: "+pluginActivity);
            shadowActivity.setTheme(PackageResManager.applicationInfo.theme);
            shadowActivity.onCreate(arg0);
            Log.e(Constant.TAG,"currentProcess activity 2: "+getCurrentProcessName());
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("shadow_ca", "onCreate...shadowActivity ="+e.toString());
//        }
    }
    public static String getCurrentProcessName() {
        FileInputStream in = null;
        try {
            String fn = "/proc/self/cmdline";
            in = new FileInputStream(fn);
            byte[] buffer = new byte[256];
            int len = 0;
            int b;
            while ((b = in.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }
            if (len > 0) {
                String s = new String(buffer, 0, len, "UTF-8");
                return s;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
