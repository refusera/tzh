package com.yiibai.springmvc.task;

import gls.tzh.been.StockBeen;
import gls.tzh.utils.SqlManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExecuteJob3 {

    public static void run() {

        Connection conn = SqlManager.getConn();
        try {
            List<StockBeen> list = findList(conn);
            for (int i = 0; i < 10; i++) {
                GpbSpider gpbSpider = new GpbSpider(list, conn, i);
                Thread thread = new Thread(gpbSpider);
                thread.start();
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<StockBeen> findList(Connection conn) {

        List<StockBeen> list = new ArrayList<StockBeen>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        String sql = "select id,code,name from stock_base;";

        try {
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockBeen stockBeen = new StockBeen();
                stockBeen.setId(resultSet.getInt("id"));
                stockBeen.setCode(resultSet.getString("code"));
                stockBeen.setName(resultSet.getString("name"));
                list.add(stockBeen);
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
