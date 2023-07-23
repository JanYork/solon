package org.noear.solon.core.event;

import org.noear.solon.Solon;
import org.noear.solon.core.exception.EventException;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.util.RunUtil;

import java.util.*;

/**
 * 监听器（内部类，外部不要使用）
 *
 * @see org.noear.solon.core.AopContext#start()
 * @see org.noear.solon.SolonApp#onEvent(Class, EventListener)
 * */
public final class EventBus {
    //异常订阅者
    private static List<HH> sThrow = new ArrayList<>();
    //其它订阅者
    private static List<HH> sOther = new ArrayList<>();
    //订阅管道
    private static Map<Class<?>, EventPipeline<?>> sPipeline = new HashMap<>();

    /**
     * 异步推送事件（一般不推荐）；
     *
     * @param event 事件（可以是任何对象）
     */
    public static void pushAsync(Object event) {
        if (event != null) {
            RunUtil.async(() -> {
                try {
                    push0(event);
                } catch (Throwable e) {
                    push(e);
                }
            });
        }
    }

    /**
     * 同步推送事件（会抛异常，可传导事务回滚）
     *
     * @param event 事件（可以是任何对象）
     */
    public static void push(Object event) throws RuntimeException {
        if (event != null) {
            try {
                push0(event);
            } catch (Throwable e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new EventException("Event execution failed: " + event.getClass().getName(), e);
                }
            }
        }
    }

    /**
     * 同步推送事件（不抛异常，不具有事务回滚传导性）
     *
     * @param event 事件（可以是任何对象）
     */
    public static void pushTry(Object event) {
        if (event != null) {
            try {
                push0(event);
            } catch (Throwable e) {
                //不再转发异常，免得死循环
            }
        }
    }

    private static void push0(Object event) throws Throwable {
        if (event instanceof Throwable) {

            if (Solon.app() == null || Solon.app().enableErrorAutoprint()) {
                ((Throwable) event).printStackTrace();
            }

            //异常分发
            push1(sThrow, event, false);
        } else {
            //其它事件分发
            push1(sOther, event, true);
        }
    }

    private static void push1(Collection<HH> hhs, Object event, boolean thrown) throws Throwable {
        for (HH h1 : hhs) {
            if (h1.t.isInstance(event)) {
                try {
                    h1.l.onEvent(event);
                } catch (Throwable e) {
                    if (thrown) {
                        throw e;
                    } else {
                        //此处不能再转发异常 //不然会死循环
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param listener  事件监听者
     */
    public synchronized static <T> void subscribe(Class<T> eventType, EventListener<T> listener) {
        pipelineDo(eventType).add(listener);
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param index     顺序位
     * @param listener  事件监听者
     */
    public synchronized static <T> void subscribe(Class<T> eventType, int index, EventListener<T> listener) {
        pipelineDo(eventType).add(index, listener);
    }

    /**
     * 建立订阅管道
     *
     * @param eventType 事件类型
     */
    private static <T> EventPipeline<T> pipelineDo(Class<T> eventType) {
        EventPipeline<T> pipeline = (EventPipeline<T>) sPipeline.get(eventType);

        if (pipeline == null) {
            pipeline = new EventPipeline<>();
            sPipeline.put(eventType, pipeline);
            subscribeDo(eventType, pipeline);
        }

        return pipeline;
    }

    private static <T> void subscribeDo(Class<T> eventType, EventListener<T> listener) {
        if (Throwable.class.isAssignableFrom(eventType)) {
            sThrow.add(new HH(eventType, listener));

            if (Solon.app() != null) {
                Solon.app().enableErrorAutoprint(false);
            }
        } else {
            sOther.add(new HH(eventType, listener));
        }
    }

    /**
     * 取消事件订阅
     *
     * @param listener 事件监听者
     */
    public synchronized static <T> void unsubscribe(EventListener<T> listener) {
        Class<?>[] ets = GenericUtil.resolveTypeArguments(listener.getClass(), EventListener.class);
        if (ets != null && ets.length > 0) {
            pipelineDo((Class<T>) ets[0]).remove(listener);
        }
    }

    /**
     * Handler Holder
     */
    static class HH {
        protected Class<?> t;
        protected EventListener l;

        public HH(Class<?> type, EventListener listener) {
            this.t = type;
            this.l = listener;
        }
    }
}