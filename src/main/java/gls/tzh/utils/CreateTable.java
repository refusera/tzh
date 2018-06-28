package gls.tzh.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class CreateTable {

    public static void createTable(Connection connection, String tableName) {

        PreparedStatement preparedStatement = null;
        String sql = "CREATE TABLE if not EXISTS `" + tableName + "` (\n" +
             "`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',\n" +
             "  `stockType` int(11) NOT NULL DEFAULT '0' COMMENT '股票类型，1-深证A股,2-上证A股,3-创业板',\n" +
             "  `code` varchar(11) NOT NULL DEFAULT '' COMMENT '股票代码',\n" +
             "  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '公司名称',\n" +
             "  `currentPrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '现价',\n" +
             "  `priceChangeRatio` varchar(10) NOT NULL DEFAULT '' COMMENT '涨跌幅',\n" +
             "  `upsAndDowns` varchar(10) NOT NULL DEFAULT '' COMMENT '涨跌额',\n" +
             "  `turnoverRate` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '换手率',\n" +
             "  `amplitude` varchar(20) NOT NULL DEFAULT '' COMMENT '振幅',\n" +
             "  `volumeOfTransactionSec` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '分钟成交量',\n" +
             "  `volumeOfTransaction` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '成交额',\n" +
             "  `gpe` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '市盈率',\n" +
             "  `circulationMarketValue` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '流通市值',\n" +
             "  `totalMarkeyValue` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '总市值',\n" +
             "  `retainedProfits` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '净利润',\n" +
             "  `primeOperatingRevenue` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '主营收入',\n" +
             "  `timeMin` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
             "`onePrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '一分钟前的价格',\n" +
             "  `fivePrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '五分钟前的价格',\n" +
             "  `fifteenPrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '十五分钟前的价格',\n" +
             "  `thirtyPrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '30分钟前的价格',\n" +
             "  `sixtyPrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '60分钟前的价格'," +
             "  `closePrice` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '昨日收盘价格'," +
             "  `gpb` decimal(20,2) NOT NULL DEFAULT '0.00' COMMENT '市净率（新东方财务网）'," +
             "  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
             "  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
             "  `is_deleted` int(5) NOT NULL DEFAULT '0' COMMENT '默认为0  已删除为1',\n" +
             "  PRIMARY KEY (`id`),\n" +
             "  KEY `key_code` (`code`) USING BTREE,\n" +
             "  UNIQUE KEY `code` (`code`,`timeMin`),\n" +
             "  KEY `key_value` (`gpe`,`circulationMarketValue`,`retainedProfits`,`create_time`) USING BTREE,\n" +
             "  FULLTEXT KEY `key_name` (`name`)\n" +
             "  ) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='数据来自网易财经（市净率字段来自新东方财富网）';";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            System.out.println("Create Table " + tableName + " Success !");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
