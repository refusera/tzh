package gls.tzh.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import gls.tzh.been.StockBeen;
import gls.tzh.utils.FindContent;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ThreadSpider implements Runnable {

    private int i;
    private Set<String> pageSet;
    private Connection conn;
    private String tableName;
    private Jedis jedis;

    public ThreadSpider(int i, Set<String> pageSet, Connection conn, String tableName, Jedis jedis) {
        this.i = i;
        this.pageSet = pageSet;
        this.conn = conn;
        this.tableName = tableName;
        this.jedis = jedis;
    }

    @Override
    public void run() {

        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int j = 0;
        for (String pageUrl : pageSet) {
            j++;
            //分配任务
            if (j % 4 == i) {
                try {
                    String content = FindContent.findContent(pageUrl);
                    if (!StringUtils.isEmpty(content)) {
                        JSONArray jsonArray = JSONObject.parseObject(content).getJSONArray("list");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            StockBeen stockBeen = dataFromatStock(jsonArray.getJSONObject(i), sim.format(new Date()));

                            if (!ObjectUtils.isEmpty(stockBeen)) {
                                String[] beforeArr = stockBeen.getTimeMin().split(" ");
                                String[] timeArr = beforeArr[1].split(":");
                                Integer timeMin = Integer.parseInt(timeArr[0]) * 60 + Integer.parseInt(timeArr[1]);
                                jedis.set(stockBeen.getCode() + "_" + timeMin, JSONObject.toJSONString(stockBeen));
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }
    }

    private static StockBeen dataFromatStock(JSONObject jso, String createTime) {

        Integer stockType = 0;
        StockBeen stockBeen = new StockBeen();
        stockBeen.setTimeMin(createTime);

        String code = jso.getString("CODE") == null ? "" : jso.getString("CODE").substring(1, jso.getString("CODE").length());  // code

        if (code.substring(0, 2).equals("60")) { // 上证A股
            stockType = 2;
        } else if (code.substring(0, 2).equals("00")) { //深证A股
            stockType = 1;
        } else if (code.substring(0, 2).equals("30")) { //创业板
            stockType = 3;
        }
        String name = jso.getString("NAME") == null ? "" : jso.getString("NAME").replace(" ", ""); //name
        double currentPrice = jso.getDouble("PRICE") == null ? 0.00 : Double.parseDouble(String.format("%.2f", jso.getDouble("PRICE"))); // 价格
        double priceChangeRatio = jso.getString("PERCENT") == null ? 0.00 : (Double.parseDouble(String.format("%.2f", jso.getDouble("PERCENT") * 100))); //涨跌幅
        double upsAndDowns = jso.getString("UPDOWN") == null ? 0.00 : (Double.parseDouble(String.format("%.2f", jso.getDouble("UPDOWN")))); // 涨跌额
        double turnoverRate = jso.getString("HS") == null ? 0.00 : Double.parseDouble(String.format("%.2f", jso.getDouble("HS") * 100)); // 换手率
        double amplitude = jso.getString("ZF") == null ? 0.00 : Double.parseDouble(String.format("%.2f", jso.getDouble("ZF") * 100)); //振幅
        //double volumeOfTransactionSec = jso.getDouble("VOLUME")==null?0.00:jso.getDouble("VOLUME"); //分钟成交量
        double volumeOfTransaction = jso.getDouble("TURNOVER") == null ? 0.00 : jso.getDouble("TURNOVER"); // 成交额
        double volumeOfTransactionSec = 0.00;
        double gpe = jso.getString("PE") == null ? 0.00 : Double.parseDouble(String.format("%.2f", jso.getDouble("PE"))); //市盈率
        double circulationMarketValue = jso.getDouble("MCAP") == null ? 0.00 : jso.getDouble("MCAP"); // 流通市值
        double totalMarketValue = jso.getDouble("TCAP") == null ? 0.00 : jso.getDouble("TCAP"); // 总市值
        double retainedProfits = jso.getJSONObject("MFRATIO").getDouble("MFRATIO2") == null ? 0.00 : jso.getJSONObject("MFRATIO").getDouble("MFRATIO2"); // 净利润
        double primeOperatingRevenue = jso.getJSONObject("MFRATIO").getDouble("MFRATIO10") == null ? 0.00 : jso.getJSONObject("MFRATIO").getDouble("MFRATIO10"); //主营收入
        double closePrice = jso.getDouble("YESTCLOSE") == null ? 0.00 : jso.getDouble("YESTCLOSE"); //昨天的收盘价格
        stockBeen.setCode(code);
        stockBeen.setStockType(stockType);
        stockBeen.setName(name);
        stockBeen.setCurrentPrice(currentPrice);
        stockBeen.setPriceChangeRatio(priceChangeRatio);
        stockBeen.setUpsAndDowns(upsAndDowns);
        stockBeen.setTurnoverRate(turnoverRate);
        stockBeen.setAmplitude(amplitude);
        stockBeen.setVolumeOfTransactionSec(volumeOfTransactionSec);
        stockBeen.setVolumeOfTransaction(volumeOfTransaction);
        stockBeen.setGpe(gpe);
        stockBeen.setCirculationMarketValue(circulationMarketValue);
        stockBeen.setTotalMarkeyValue(totalMarketValue);
        stockBeen.setRetainedProfits(retainedProfits);
        stockBeen.setPrimeOperatingRevenue(primeOperatingRevenue);
        stockBeen.setClosePrice(closePrice);

        return stockBeen;
    }
}
