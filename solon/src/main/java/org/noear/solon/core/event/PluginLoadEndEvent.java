package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Plugin load end 事件
 *
 * @author noear
 * @since 1.1
 * */
@Deprecated
public class PluginLoadEndEvent extends AppEvent {
    public PluginLoadEndEvent(SolonApp app) {
        super(app);
    }
}
