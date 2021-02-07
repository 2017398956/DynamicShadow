package com.wwq.self_shadow;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.wwq.pluginlibrary.DelegateProviderHolder;
import com.wwq.pluginlibrary.GlobalContext;
import com.wwq.self_shadow.plugin.ShadowProvider;
import com.wwq.self_shadow.utils.CopyFileFromAssets;

import java.io.File;
import java.lang.reflect.Method;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.setApplication(this.getApplicationContext());
        DelegateProviderHolder.setDelegateProvider("test", new ShadowProvider());

        /**
         * 这是个测试方法，还未完善（应该在非主线程中运行）
         */
        try {
            if (true) {
                return;
            }
            Resources resources;
            // 将 assets 目录下的 apk 文件复制到 app 私有目录下
            File file = new File(getFilesDir(), "resource.apk");
            Log.i(Constant.TAG, "复制到 " + file.getAbsolutePath());
            CopyFileFromAssets.copy(this, "resource.apk", file);
            // 取得包管理器
            PackageManager mPm = getPackageManager();
            // 获取 apk 文件的包信息
            PackageInfo mInfo = mPm.getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            // 利用反射的方式生产 apk 包的 AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, file.getAbsolutePath());
            // 根据宿主的 resources 信息生成插件的 resources
            Resources superRes = getResources();
            resources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        }catch (Exception E){

        }
    }
}
