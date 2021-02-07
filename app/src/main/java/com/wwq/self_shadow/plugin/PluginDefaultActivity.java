package com.wwq.self_shadow.plugin;

import com.wwq.pluginlibrary.DelegateProvider;
import com.wwq.pluginlibrary.host.PluginContainerActivity;

/**
 * 用于代理插件中的 activity
 */
public class PluginDefaultActivity extends PluginContainerActivity {
    public PluginDefaultActivity() {

    }

    /**
     * 为容器 activity 添加代理提供者的关键字，让容器 activity 可以根据该关键字
     * 获取 {@link DelegateProvider}
     * 不覆写的话会使用默认的关键字
     * @return
     */
    @Override
    protected String getDelegateProviderKey() {
        return "test";
    }
}
