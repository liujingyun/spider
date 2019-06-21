package com.xinyan.trust.controller;

import com.xinyan.trust.pipeline.SavePipeline;
import com.xinyan.trust.propessor.ZJtrustPropessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import us.codecraft.webmagic.Spider;

@Controller("/zijin")
@Api(value = "ZiJinController|一个用来测试爬虫")
public class ZiJinController {
    @Autowired
    private ZJtrustPropessor zJtrustPropessor;
    @Autowired
    private SavePipeline savePipeline;
    @ApiOperation(value="根据用户编号获取用户姓名", notes="test: 仅1和2有正确返回")
    @GetMapping("/cunxunzhong")
    public void zijin(){
        Spider.create(zJtrustPropessor).addUrl("https://www.zjtrust.com.cn/cn/page/37/1047.html").addPipeline(savePipeline).thread(1).run();

    }
}
