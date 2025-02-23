package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/3/27 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class PropDefTest {
    @Inject("${test.name:noear}")
    String name;


    @Inject("${test.url:http://solon.noear.org}")
    String url;

    @Test
    public void test(){
        assert "noear".equals(name);
        assert "http://solon.noear.org".equals(url);
    }
}
