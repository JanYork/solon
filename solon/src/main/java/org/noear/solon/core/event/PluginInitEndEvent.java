package org.noear.solon.core.event;

import org.noear.solon.SolonApp;

/**
 * Plugin init end 事件
 *
 * @author noear
 * @since 1.10
 * */
@Deprecated
public class PluginInitEndEvent extends AppEvent {
    public PluginInitEndEvent(SolonApp app) {
        super(app);
    }
}
