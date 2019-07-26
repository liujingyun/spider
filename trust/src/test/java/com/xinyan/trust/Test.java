package com.xinyan.trust;

import com.xinyan.trust.propessor.WeiBoHotProcessor;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;

/**
 * @ClassName Test
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/24 14:45
 * @Version V1.0
 **/
@SpringBootTest
public class Test {
    public void test1(){
        Spider.create(new WeiBoHotProcessor()).addUrl("https://s.weibo.com/top/summary?cate=realtimehot").thread(1).run();

    }
}
