package com.wwq.self_shadow.plugin;

import com.wwq.pluginlibrary.host.PluginContainerActivity;

/**
 * 用于代理插件中的 activity
 */
public class PluginDefaultActivity extends PluginContainerActivity {
    public PluginDefaultActivity() {

    }

    @Override
    protected String getDelegateProviderKey() {
        return "test";
    }
}
