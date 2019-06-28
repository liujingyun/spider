package com.xinyan.trust.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xinyan.trust.entity.ExcelBean;
import com.xinyan.trust.entity.ZiJinBean;
import com.xinyan.trust.pipeline.SavePipeline;
import com.xinyan.trust.propessor.ZJtrustPropessor;
import com.xinyan.trust.repository.ExcelRepository;
import com.xinyan.trust.util.ExcelUtil;
import com.xinyan.trust.util.Status;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ZIjinService {
    @Autowired
    private ZJtrustPropessor zJtrustPropessor;
    @Autowired
    private SavePipeline savePipeline;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ExcelRepository repository;
    private final Integer poolSize = 5;
    private final Integer maxPoolSize = 15;
    private final Integer maxWait = 100;
    private final Long aliveTime = 0L;
    private ThreadFactory asyncThreadFactory = new ThreadFactoryBuilder().setNameFormat("parser-pool-%d").build();
    private ThreadPoolExecutor asyncTaskThreadPool = new ThreadPoolExecutor(poolSize, maxPoolSize, aliveTime, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(maxWait), asyncThreadFactory);


    public ZIjinService() {
    }

    public void startSpider(String token) {
        try {
            this.stringRedisTemplate.opsForValue().set(token, Status.START.getCode());
            ExcelBean excelBean = new ExcelBean();
            excelBean.setToken(token);
            List<ZiJinBean> ziJinBeans = new CopyOnWriteArrayList<>();
            this.savePipeline.setZiJinBeans(ziJinBeans);
            asyncTaskThreadPool.execute(() -> {

                Spider.create(zJtrustPropessor)
                        .addUrl("https://www.zjtrust.com.cn/cn/page/115.html")
                        .addPipeline(savePipeline).thread(3).run();

                this.stringRedisTemplate.opsForValue().set(token, Status.END.getCode());
                excelBean.setMessage(ziJinBeans);
                System.out.println(excelBean);
                this.repository.save(excelBean);
            });

        } catch (Exception e) {
            this.stringRedisTemplate.opsForValue().set(token, Status.FAILED.getCode());
        }

    }

    public String getStatus(String token) {
        return this.stringRedisTemplate.opsForValue().get(token);
    }

    /**
     * 下载
     * @param token
     * @param response
     * @return
     */
    public String download(String token, HttpServletResponse response) {
        String status = getStatus(token);
        OutputStream output = null;
        try {
            if (Status.END.getCode().equals(status)) {
                ExcelBean bean = this.repository.findExcelBeanByToken(token);
                String flieName = "紫金信托" + System.currentTimeMillis();
                response.reset();// 清空输出流
                // 设定输出文件头
                response.setHeader("Content-disposition", "attachment; filename=" + new String(flieName.getBytes("UTF-8"), "ISO8859-1") + ".xls");
                // 定义输出类型
                response.setContentType("application/msexcel;charset=utf-8");
                HSSFWorkbook wb = new HSSFWorkbook();

                HSSFSheet sheet = wb.createSheet("紫金信托");
                ExcelUtil.setSheet(sheet,bean);
                output = response.getOutputStream();
                wb.write(output);
                output.flush();
                return "ok";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "任务未完成";
        }finally {
            if(output !=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "任务未完成";
    }
}
