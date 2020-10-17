package org.noear.solon.core;

import org.noear.solon.annotation.XMapping;

/**
 * 通用处理接口接收槽
 *
 * @author noear
 * @since 1.0
 * */
public interface XHandlerSlots {
    default void before(String expr, XMethod method, int index, XHandler handler){}
    default void after(String expr, XMethod method, int index, XHandler handler){};
    void add(String expr, XMethod method, XHandler handler);

    default void add(XMapping mapping, XHandler handler){
        for (XMethod m1 : mapping.method()) {
            if (mapping.after() || mapping.before()) {
                if (mapping.after()) {
                    after(mapping.value(), m1, mapping.index(), handler);
                } else {
                    before(mapping.value(), m1, mapping.index(), handler);
                }
            } else {
                add(mapping.value(), m1, handler);
            }
        }
    };
}
