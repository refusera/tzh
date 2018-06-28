package com.yiibai.springmvc.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Service
public class StockSpider {

    /**
     * 爬取网易财经的股票信息至redis
     */
    @Async
    public void wangYiSpiderToRedis() {

        ExecuteJob1.run();
    }

    /**
     * 解析redis数据，并整理入库(mysql)
     */
    @Async
    public void processRedisToSql() {

        ExecuteJob2.run();
    }

    /**
     * 获取新东方财富网的市净率数据（redis） 每天一次
     */
    @Async
    public void newOrientalWealth() {

        ExecuteJob3.run();
    }
}
