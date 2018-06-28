package com.yiibai.springmvc.task;

import com.alibaba.fastjson.JSON;
import gls.tzh.been.StockBeen;
import gls.tzh.utils.Constants;
import gls.tzh.utils.FindContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GpbSpider implements Runnable {

    private List<StockBeen> list;
    private Connection connection;
    private int i;

    public GpbSpider(List<StockBeen> list, Connection connection, int i) {
        this.list = list;
        this.connection = connection;
        this.i = i;
    }

    /**
     * 创建redis客户端连接
     */
    private static Jedis createReids() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), Constants.REDIS_HOST, 6379);
        return pool.getResource();
    }

    @Override
    public void run() {

        int j = 0;
        try {
            for (StockBeen stockBeen : list) {
                j++;
                if (j % 10 == i) {
                    Jedis jedis = createReids();
                    try {
                        String url = "";
                        String code = stockBeen.getCode();
                        if (code.substring(0, 2).equals("00") || code.substring(0, 2).equals("30")) {
                            url = "http://quote.eastmoney.com/sz" + code + ".html";
                        } else if (code.substring(0, 2).equals("60")) {
                            url = "http://quote.eastmoney.com/sh" + code + ".html";
                        }
                        Element element = Jsoup.parse(FindContent.findContent(url)).getElementById("gt13_2");
                        if (!ObjectUtils.isEmpty(element)) {
                            stockBeen.setGpb(Double.parseDouble(element.text().contains("-") ? "0.00" : element.text()));
                        }
                        jedis.set("GPB" + stockBeen.getCode(), JSON.toJSONString(stockBeen));
                        jedis.close();
                    } catch (Exception ex) {
                        System.out.println(ex.getLocalizedMessage());
                        jedis.close();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
