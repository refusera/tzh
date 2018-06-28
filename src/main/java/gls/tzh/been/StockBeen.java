package gls.tzh.been;

public class StockBeen {

    private int id; // id

    // stock 类型  0-深证A股,1-上证A股,2-中小板,4-创业板
    private int stockType;

    private String code = ""; // 代码

    private String name = "";  //名称

    private double currentPrice = 0.00; // 现价

    private double priceChangeRatio = 0.00; // 涨跌幅

    private double upsAndDowns = 0.00; //涨跌额

    private double turnoverRate = 0.00; //换手率

    private double amplitude = 0.00; // 振幅

    private double volumeOfTransactionSec = 0.00; // 分钟成交量

    private double volumeOfTransaction = 0.00; //成交额

    private double gpe = 0.00; // 市盈率

    private double gpb = 0.00; // 市净率

    private double circulationMarketValue = 0.00; // 流通市值

    private double totalMarkeyValue = 0.00; // 总市值

    private double retainedProfits = 0.00; //净利润

    private double primeOperatingRevenue = 0.00; // 主营收入

    private String timeMin; //获取记录的时间

    private double closePrice; //昨日收盘价格


    public double getGpb() {
        return gpb;
    }

    public void setGpb(double gpb) {
        this.gpb = gpb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public double getPriceChangeRatio() {
        return priceChangeRatio;
    }

    public void setPriceChangeRatio(double priceChangeRatio) {
        this.priceChangeRatio = priceChangeRatio;
    }

    public double getUpsAndDowns() {
        return upsAndDowns;
    }

    public void setUpsAndDowns(double upsAndDowns) {
        this.upsAndDowns = upsAndDowns;
    }

    public double getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(double turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public double getVolumeOfTransactionSec() {
        return volumeOfTransactionSec;
    }

    public void setVolumeOfTransactionSec(double volumeOfTransactionSec) {
        this.volumeOfTransactionSec = volumeOfTransactionSec;
    }

    public double getVolumeOfTransaction() {
        return volumeOfTransaction;
    }

    public void setVolumeOfTransaction(double volumeOfTransaction) {
        this.volumeOfTransaction = volumeOfTransaction;
    }

    public double getGpe() {
        return gpe;
    }

    public void setGpe(double gpe) {
        this.gpe = gpe;
    }

    public double getCirculationMarketValue() {
        return circulationMarketValue;
    }

    public void setCirculationMarketValue(double circulationMarketValue) {
        this.circulationMarketValue = circulationMarketValue;
    }

    public double getTotalMarkeyValue() {
        return totalMarkeyValue;
    }

    public void setTotalMarkeyValue(double totalMarkeyValue) {
        this.totalMarkeyValue = totalMarkeyValue;
    }

    public double getRetainedProfits() {
        return retainedProfits;
    }

    public void setRetainedProfits(double retainedProfits) {
        this.retainedProfits = retainedProfits;
    }

    public double getPrimeOperatingRevenue() {
        return primeOperatingRevenue;
    }

    public void setPrimeOperatingRevenue(double primeOperatingRevenue) {
        this.primeOperatingRevenue = primeOperatingRevenue;
    }

    public String getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(String timeMin) {
        this.timeMin = timeMin;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    @Override
    public String toString() {
        return "StockBeen{" +
             "id=" + id +
             ", stockType=" + stockType +
             ", code='" + code + '\'' +
             ", name='" + name + '\'' +
             ", currentPrice=" + currentPrice +
             ", priceChangeRatio=" + priceChangeRatio +
             ", upsAndDowns=" + upsAndDowns +
             ", turnoverRate=" + turnoverRate +
             ", amplitude=" + amplitude +
             ", volumeOfTransactionSec=" + volumeOfTransactionSec +
             ", volumeOfTransaction=" + volumeOfTransaction +
             ", gpe=" + gpe +
             ", gpb=" + gpb +
             ", circulationMarketValue=" + circulationMarketValue +
             ", totalMarkeyValue=" + totalMarkeyValue +
             ", retainedProfits=" + retainedProfits +
             ", primeOperatingRevenue=" + primeOperatingRevenue +
             ", timeMin='" + timeMin + '\'' +
             ", closePrice=" + closePrice +
             '}';
    }
}
