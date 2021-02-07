package com.wwq.self_shadow;

public class Constant {
    public static String TAG ="shadow_ca";
    public static UserInfo userInfo;
    public static String apk_max = "shadow_demo-debug.apk";
    public static String apk_min = "shadow_demo-debug-min.apk";
    public static String apk = apk_max;

    // 插件的关键字，用于对应各个插件，注意：由于会根据不同的关键字为插件创建不同的目录，所以插件关键字应符合文件名的格式
    public static final String PLUGIN_KEY_MAX = "max" ;
    public static final String PLUGIN_KEY_MIN = "min" ;
}
