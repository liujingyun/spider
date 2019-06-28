package com.xinyan.trust.controller;

import com.xinyan.trust.service.ZIjinService;
import com.xinyan.trust.util.Status;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller("/zijin")
@Api(value = "ZiJinController|一个用来测试爬虫")
public class ZiJinController {
    @Autowired
    private ZIjinService zijinService;

    @ApiOperation(value="获取紫金信托存续中的产品")
    @GetMapping("/cunxunzhong")
    public String zijin(){
        //创建一个UUID当作token唯一标识
        String token = UUID.randomUUID().toString();
        this.zijinService.startSpider(token);
        return token;
    }

    @ApiOperation(value="获取爬取任务状态")
    @ApiImplicitParam(name = "token",value = "唯一标识",required = true,dataType = "String",paramType = "param")
    @GetMapping("/getStatus")
    public String getStatus(@RequestParam(value = "token") String token){
        return Status.getValue(this.zijinService.getStatus(token));
    }

    @ApiOperation(value="下载")
    @ApiImplicitParam(name = "token",value = "唯一标识",required = true,dataType = "String",paramType = "param")
    @GetMapping("/dowbLoadExcel")
    public String downLoadExcel(@RequestParam(value = "token") String token, HttpServletResponse response){
        return this.zijinService.download(token,response);
    }
}
