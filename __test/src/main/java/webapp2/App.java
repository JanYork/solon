package webapp2;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.serialization.snack3.SnackActionExecutor;

/**
 * @author noear 2022/7/11 created
 */
public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.onEvent(SnackActionExecutor.class, executor -> {
                executor.config().addDecoder(String.class, (node, type) -> {
                    if (Utils.isEmpty(node.getString())) {
                        return null;
                    } else {
                        return node.getString();
                    }
                });
            });
        });
    }
}