package com.wwq.self_shadow;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.wwq.pluginlibrary.PluginClassLoader;
import com.wwq.pluginlibrary.ShadowService;
import com.wwq.self_shadow.plugin.PluginDefaultActivity;
import com.wwq.self_shadow.plugin.ShadowActivityDelegate;
import com.wwq.self_shadow.pps.PPSBinder;
import com.wwq.self_shadow.pps.PpsController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;

import static com.wwq.self_shadow.plugin.ShadowActivityDelegate.getCurrentProcessName;

/**
 * 用于加载插件的服务
 */
public class PPService extends Service {
    private Handler mUiHandler = new Handler(Looper.getMainLooper());

    private static Object sSingleInstanceFlag = null;
    // 用于接收宿主通过 IBinder 发送的信息
    private PPSBinder binder = new PPSBinder(this);

    @Override
    public void onCreate() {
        super.onCreate();
        if (sSingleInstanceFlag == null) {
            Log.d(Constant.TAG, "首次创建service");
        } else {
            Log.e(Constant.TAG, "service 创建了多实例");
        }
        Log.d(Constant.TAG, "service onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constant.TAG, "service onStartCommand and startID = " + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(Constant.TAG, "service onRebind");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(Constant.TAG, "service onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(Constant.TAG, "service onBind : " + binder);
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(Constant.TAG, "service onUnbind : " + intent);
        return super.onUnbind(intent);
    }

    /**
     * 为插件加载服务提供一个可以操作插件行为的控制器
     *
     * @param ppsBinder 用于和这个服务通讯的 IBinder
     * @return
     */
    public static PpsController wrapBinder(IBinder ppsBinder) {
        return new PpsController(ppsBinder);
    }

    public void startService() throws Exception {
        File file = new File(getFilesDir(), Constant.apk);
        if (file.exists()) {
            Log.d(Constant.TAG, "apkPath " + file.getAbsolutePath());
        } else {
            Log.d(Constant.TAG, "apkPath 不存在，" + file.getAbsolutePath());
        }
        Log.e(Constant.TAG, "currentProcess service: " + getCurrentProcessName());

        Object o = baseDexClassLoader.loadClass("com.wwq.shadow_demo.TestService").newInstance();
        final ShadowService service = (ShadowService) o;
//        if (isUiThread()) {
        service.onCreate();
        service.onStartCommand(new Intent(), 0, 10);
//        } else {
//            final CountDownLatch waitUiLock = new CountDownLatch(1);
//            mUiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    service.onCreate();
//                    service.onStartCommand(new Intent(), 0, 10);
//                    waitUiLock.countDown();
//                }
//            });
//            waitUiLock.await();
//        }
    }

    private boolean isUiThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private UserInfo userInfo;

    public void setUserName(String arg0) {
        Log.e(Constant.TAG, "currentProcess service 1 : " + getCurrentProcessName());
        userInfo = new UserInfo();
        userInfo.name = arg0;
        Constant.userInfo = userInfo;
        Log.d(Constant.TAG, "setUserName = " + arg0);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void exit() {
        System.exit(0);
        try {
            wait();
            Log.e(Constant.TAG, "exit...");
        } catch (InterruptedException ignored) {
        }
    }

    public static PluginClassLoader baseDexClassLoader;

    /**
     * 由于插件中假的 activity 都在插件 apk 文件中，所以宿主 app 不知道类名，这里只能用字符串了
     * @param shadowActivity
     */
    public void starPluginActivity(String shadowActivity) {
        // 启动容器  activity
        Intent intent = new Intent(this, PluginDefaultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        // 为容器 activity 添加要启动的插件中假的 activity
        bundle.putString("className", shadowActivity);
        intent.putExtras(bundle);
        startActivity(intent);
        Log.e(Constant.TAG, "currentProcess service 2 : " + getCurrentProcessName());
    }

    public void starPluginActivityForResult() {
//        Intent intent = new Intent(this, PluginDefaultActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        Log.e(Constant.TAG,"currentProcess service 2 : "+getCurrentProcessName());
    }

    Map<String, PluginClassLoader> classLoaderMap = new HashMap<>();
    Map<String, Resources> resourcesMap = new HashMap<>();

    /**
     * 加载插件并准备插件的相关资源
     * TODO 注意：这里没有加载插件的 so 资源
     * @param pluginKey
     */
    public void loadPlugin(String pluginKey) {
        if (pluginKey.equalsIgnoreCase(Constant.PLUGIN_KEY_MIN)) {
            Constant.apk = Constant.apk_min;
        } else {
            Constant.apk = Constant.apk_max;
        }
        Log.e(Constant.TAG, "start load plugin : " + pluginKey);
        // 如果已经加载过这个插件，那么直接使用内存中的插件
        if (classLoaderMap.containsKey(pluginKey) && classLoaderMap.get(pluginKey) != null) {
            // 1.获取插件的 class 信息
            baseDexClassLoader = classLoaderMap.get(pluginKey);
            // 2.获取插件的 resources 资源
            ShadowActivityDelegate.mPluginResources = resourcesMap.get(pluginKey);
            Log.d(Constant.TAG, "baseDexClassLoader 1 =" + baseDexClassLoader);
            return;
        }
        // 如果没加载过这个插件，则重新加载
        // 1.找到该插件
        File file = new File(getFilesDir(), Constant.apk);
        // 2.获取插件的 resources 资源
        Resources resource = PackageResManager.createResource(this, file, pluginKey);
        Log.d(Constant.TAG, "service onCreate,resource=" + resource);
        ShadowActivityDelegate.mPluginResources = resource;
        resourcesMap.put(pluginKey, resource);
        // 3.获取插件的 class 信息
        ClassLoader classLoader = PPService.class.getClassLoader();
        File odexFile = new File(getCacheDir(), pluginKey);
        if (!odexFile.exists()) {
            odexFile.mkdirs();
        }
        baseDexClassLoader = new PluginClassLoader(file.getAbsolutePath(), odexFile, null, classLoader);
        Log.d(Constant.TAG, "baseDexClassLoader 2 =" + baseDexClassLoader);
        classLoaderMap.put(pluginKey, baseDexClassLoader);
    }
}
