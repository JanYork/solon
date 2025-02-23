package com.fujieid.jap.solon.integration;

import com.fujieid.jap.solon.JapInitializer;
import com.fujieid.jap.solon.JapProps;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.6
 */
public class XPluginImpl implements Plugin {

    @Override
    public void start(AopContext context) {
        context.beanMake(JapProps.class);
        context.beanMake(JapInitializer.class);
    }

}
