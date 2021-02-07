package com.wwq.self_shadow.plugin;

import com.wwq.pluginlibrary.DelegateProvider;
import com.wwq.pluginlibrary.shadow.interfaces.HostActivityDelegate;
import com.wwq.pluginlibrary.host.interfaces.HostActivityDelegator;

/**
 * 为宿主程序提供插件的资源信息及一些操作类
 */
public class ShadowProvider implements DelegateProvider {

    @Override
    public HostActivityDelegate getHostActivityDelegate(Class<? extends HostActivityDelegator> delegator) {
        return new ShadowActivityDelegate();
    }
}
