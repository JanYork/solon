package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;
        JsonProps jsonProps = JsonProps.create(context);

        //绑定属性
        applyProps(FastjsonRenderFactory.global, jsonProps);

        //事件扩展
        EventBus.push(FastjsonRenderFactory.global);

        RenderManager.mapping("@json", FastjsonRenderFactory.global.create());
        RenderManager.mapping("@type_json", FastjsonRenderTypedFactory.global.create());

        //支持 json 内容类型执行
        FastjsonActionExecutor executor = new FastjsonActionExecutor();
        EventBus.push(executor);

        Bridge.actionExecutorAdd(executor);
    }

    private void applyProps(FastjsonRenderFactory factory, JsonProps jsonProps) {
        if (JsonPropsUtil.apply(factory, jsonProps)) {
            if (jsonProps.nullStringAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                factory.addFeatures(SerializerFeature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                factory.addFeatures(SerializerFeature.WriteNullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                factory.addFeatures(SerializerFeature.WriteNullListAsEmpty);
            }
        }
    }
}
