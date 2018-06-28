package com.yiibai.springmvc.task;

import gls.tzh.process.ThreadSpider;
import gls.tzh.utils.Constants;
import gls.tzh.utils.CreateTable;
import gls.tzh.utils.FindPage;
import gls.tzh.utils.SqlManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ExecuteJob1 {

    public static void run() {

        String[] timeArr = new SimpleDateFormat("HH:mm").format(new Date()).split(":");
        int time = Integer.parseInt(timeArr[0]) * 60 + Integer.parseInt(timeArr[1]);
        System.out.println("job1 : " + time);

        try {
            String tableName = "stock_" + new SimpleDateFormat("yyyyMMdd").format(new Date());

            if (time == 510) {
                Connection conn = SqlManager.getConn();
                CreateTable.createTable(conn, tableName);
                conn.close();
            }

            if ((time >= 570 && time <= 690) || (time >= 780 && time <= 900)) {
                Jedis jedis = createReids();
                Connection conn = SqlManager.getConn();
                Set<String> pageSet = FindPage.findPage(conn);
                for (int i = 0; i < 4; i++) {
                    ThreadSpider threadSpider = new ThreadSpider(i, pageSet, conn, tableName, jedis);
                    Thread thread = new Thread(threadSpider);
                    thread.start();
                }
                jedis.close();
            }
            //每日清空redis
            if (time == 1000) {
                Jedis jedis = createReids();
                jedis.flushAll();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建redis客户端连接
     */
    private static Jedis createReids() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), Constants.REDIS_HOST, 6379);
        return pool.getResource();
    }
}
