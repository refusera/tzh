package com.yiibai.springmvc.task;

import com.alibaba.fastjson.JSONObject;
import gls.tzh.utils.Constants;
import gls.tzh.utils.SqlManager;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ExecuteJob2 {

    public static void run() {

        String[] timeArr = new SimpleDateFormat("HH:mm").format(new Date()).split(":");
        int time = Integer.parseInt(timeArr[0]) * 60 + Integer.parseInt(timeArr[1]);
        System.out.println("job2 : " + time);
        try {

            if ((time >= 569 && time <= 691) || (time >= 779 && time <= 901)) {
                Jedis jedis = createReids();
                Thread.sleep(10 * 1000); //休眠20秒，等待job4处理完毕上一分钟数据之后，这边开始执行处理数据并入库操作（mysql）
                String tableName = "stock_" + new SimpleDateFormat("yyyyMMdd").format(new Date());
                Connection connection = SqlManager.getConn();
                Set<JSONObject> dataSet = new HashSet<JSONObject>();
                if (time == 571) {
                    Set<String> set = jedis.keys("*_" + (time - 1));//每次处理1分钟前的数据
                    for (String key : set) {
                        JSONObject jsonObject = JSONObject.parseObject(jedis.get(key));
                        jsonObject.put("volumeOfTransactionSec", jsonObject.getDouble("volumeOfTransaction"));
                        jsonObject.put("gpb", jedis.get("GPB" + key.substring(0, 6)) == null ? 0.00 : JSONObject.parseObject(jedis.get("GPB" + key.substring(0, 6))).getString("gpb"));
                        dataSet.add(jsonObject);
                    }
                } else {
                    Set<String> set = jedis.keys("*_" + (time - 1));//每次处理1分钟前的数据
                    Set<String> setOld = jedis.keys("*_" + (time - 2)); //取出2分钟前的数据，跟1分钟前的进行对比，获取1分钟前的当前分钟的分钟交易额
                    if (time == 781) {
                        setOld = jedis.keys("*_" + 690);
                    }

                    for (String key : set) {
                        String code = key.substring(0, 6);
                        for (String keyOld : setOld) {
                            String codeOld = keyOld.substring(0, 6);
                            if (code.equals(codeOld)) {
                                JSONObject jsonObject = JSONObject.parseObject(jedis.get(key));
                                JSONObject jsonObjectOld = JSONObject.parseObject(jedis.get(keyOld));

                                //分别取出该只股票的1分钟前，五分钟前，15分钟前，30分钟前和60分钟前的价格信息
                                int keyLast = Integer.parseInt(key.substring(7, key.length()));

                                //应业务场景需求，把当前分钟之前的几个时间点的价格信息存储起来
                                double onePrice = 0.00, fivePrice = 0.00, fifteenPrice = 0.00, thirtyPrice = 0.00, sixtyPrice = 0.00;
                                try {
                                    onePrice = jedis.get(code + "_" + (keyLast - 1)) == null ? (keyLast - 1) > 690 && (keyLast - 1) < 780 ? JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 90))).getDouble("currentPrice") : 0.00 : JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 1))).getDouble("currentPrice");
                                    fivePrice = jedis.get(code + "_" + (keyLast - 5)) == null ? (keyLast - 5) > 690 && (keyLast - 5) < 780 ? JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 94))).getDouble("currentPrice") : 0.00 : JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 5))).getDouble("currentPrice");
                                    fifteenPrice = jedis.get(code + "_" + (keyLast - 15)) == null ? (keyLast - 15) > 690 && (keyLast - 15) < 780 ? JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 104))).getDouble("currentPrice") : 0.00 : JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 15))).getDouble("currentPrice");
                                    thirtyPrice = jedis.get(code + "_" + (keyLast - 30)) == null ? (keyLast - 30) > 690 && (keyLast - 30) < 780 ? JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 119))).getDouble("currentPrice") : 0.00 : JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 30))).getDouble("currentPrice");
                                    sixtyPrice = jedis.get(code + "_" + (keyLast - 60)) == null ? (keyLast - 60) > 690 && (keyLast - 60) < 780 ? JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 149))).getDouble("currentPrice") : 0.00 : JSONObject.parseObject(jedis.get(code + "_" + (keyLast - 60))).getDouble("currentPrice");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                jsonObject.put("onePrice", onePrice);
                                jsonObject.put("fivePrice", fivePrice);
                                jsonObject.put("fifteenPrice", fifteenPrice);
                                jsonObject.put("thirtyPrice", thirtyPrice);
                                jsonObject.put("sixtyPrice", sixtyPrice);
                                jsonObject.put("volumeOfTransactionSec", jsonObject.getDouble("volumeOfTransaction") - jsonObjectOld.getDouble("volumeOfTransaction"));
                                jsonObject.put("gpb", jedis.get("GPB" + codeOld) == null ? 0.00 : JSONObject.parseObject(jedis.get("GPB" + codeOld)).getString("gpb"));
                                dataSet.add(jsonObject);
                                break;
                            }
                        }
                    }
                }
                if (!ObjectUtils.isEmpty(dataSet)) {
                    save(connection, dataSet, tableName);
                }
                jedis.close();
            }
            //sql 把当日最后一条记录的换手率和总交易额放入中间表（业务需要，需要计算几日内的平均值）
            //sql2 把当日的新增的股票加入基础表
            if (time == 905) {
                System.out.println("程序启动 : " + time);
                SimpleDateFormat si = new SimpleDateFormat("yyyyMMdd");
                String stockTable = "stock_" + si.format(new java.util.Date());
                String sql = "INSERT INTO stock.stockavg (code,name,turnoverRate,volumeOfTransaction) SELECT code,name,turnoverRate,volumeOfTransaction FROM(SELECT CODE,name,turnoverRate,volumeOfTransaction,create_time FROM " + stockTable + " ORDER BY create_time DESC) tmp GROUP BY CODE;";
                String sql2 = "INSERT INTO stock_base (code,origin_code,`name`) SELECT DISTINCT(base.`code`) code,IF(SUBSTR(code,1,2)='00',1,IF(SUBSTR(code,1,2)='30',3,IF(SUBSTR(code,1,2)='60',2,0))) origin_code,base.`name` FROM " + stockTable + " base WHERE base.`code` NOT IN (SELECT CODE FROM stock_base);";
                try {
                    Connection connection = SqlManager.getConn();
                    executeSql(connection, sql);
                    Thread.sleep(2000);
                    executeSql(connection, sql2);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void executeSql(Connection connection, String sql) {

        PreparedStatement pre = null;
        try {
            pre = connection.prepareCall(sql);
            pre.execute();
            System.out.println("中间表数据插入成功！");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (pre != null) {
                try {
                    pre.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析完毕，保存数据库
     */
    private static void save(Connection connection, Set<JSONObject> set, String tableName) {

        PreparedStatement preparedStatement = null;
        String sql = "INSERT INTO " + tableName + " (`stockType`, `code`, `name`, `currentPrice`, `priceChangeRatio`," +
             " `upsAndDowns`, `turnoverRate`, `amplitude`, `volumeOfTransactionSec`, `volumeOfTransaction`, `gpe`," +
             " `circulationMarketValue`, `totalMarkeyValue`, `retainedProfits`, `primeOperatingRevenue`, `timeMin`," +
             " onePrice,fivePrice,fifteenPrice,thirtyPrice,sixtyPrice, closePrice, gpb) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

        try {

            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);

            for (JSONObject json : set) {

                preparedStatement.setInt(1, json.getInteger("stockType"));
                preparedStatement.setString(2, json.getString("code"));
                preparedStatement.setString(3, json.getString("name"));
                preparedStatement.setDouble(4, json.getDouble("currentPrice"));
                preparedStatement.setDouble(5, json.getDouble("priceChangeRatio"));
                preparedStatement.setDouble(6, json.getDouble("upsAndDowns"));
                preparedStatement.setDouble(7, json.getDouble("turnoverRate"));
                preparedStatement.setDouble(8, json.getDouble("amplitude"));
                preparedStatement.setDouble(9, json.getDouble("volumeOfTransactionSec") == null ? 0.00 : json.getDouble("volumeOfTransactionSec"));
                preparedStatement.setDouble(10, json.getDouble("volumeOfTransaction"));
                preparedStatement.setDouble(11, json.getDouble("gpe"));
                preparedStatement.setDouble(12, json.getDouble("circulationMarketValue"));
                preparedStatement.setDouble(13, json.getDouble("totalMarkeyValue"));
                preparedStatement.setDouble(14, json.getDouble("retainedProfits"));
                preparedStatement.setDouble(15, json.getDouble("primeOperatingRevenue"));
                preparedStatement.setString(16, json.getString("timeMin"));
                preparedStatement.setDouble(17, json.getDouble("onePrice") == null ? 0.00 : json.getDouble("onePrice"));
                preparedStatement.setDouble(18, json.getDouble("fivePrice") == null ? 0.00 : json.getDouble("fivePrice"));
                preparedStatement.setDouble(19, json.getDouble("fifteenPrice") == null ? 0.00 : json.getDouble("fifteenPrice"));
                preparedStatement.setDouble(20, json.getDouble("thirtyPrice") == null ? 0.00 : json.getDouble("thirtyPrice"));
                preparedStatement.setDouble(21, json.getDouble("sixtyPrice") == null ? 0.00 : json.getDouble("sixtyPrice"));
                preparedStatement.setDouble(22, json.getDouble("closePrice"));
                preparedStatement.setDouble(23, json.getDouble("gpb"));

                preparedStatement.executeUpdate();
            }
            connection.commit();
            System.out.println(" Process Success !! ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取redis客户端连接
     */
    private static Jedis createReids() {

        JedisPool pool = new JedisPool(new JedisPoolConfig(), Constants.REDIS_HOST, 6379);

        return pool.getResource();
    }
}
