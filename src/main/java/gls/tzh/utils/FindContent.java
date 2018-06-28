package gls.tzh.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

public class FindContent {

    final static String ProxyUser = "H71LRT46SWC4724P";
    final static String ProxyPass = "CA0C820D7F2A1025";

    final static String ProxyHost = "http-pro.abuyun.com";
    final static Integer ProxyPort = 9010;

    final static String ProxyHeadKey = "Proxy-Switch-Ip";
    final static String ProxyHeadVal = "yes";

    public static String findContent(String url) {

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ProxyUser, ProxyPass.toCharArray());
            }
        });
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyHost, ProxyPort));

        String content = "";
        try {
            Connection.Response response = Jsoup.connect(url)
                 .ignoreContentType(true)
                 .ignoreHttpErrors(true)
                 .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                 .timeout(10 * 1000)
                 .header(ProxyHeadKey, ProxyHeadVal)
                 .proxy(proxy)
                 .headers(headMap())
                 .execute();
            if (503 == response.statusCode()) {
                response = Jsoup.connect(url)
                     .ignoreContentType(true)
                     .ignoreHttpErrors(true)
                     .timeout(10 * 1000)
                     .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                     .header(ProxyHeadKey, ProxyHeadVal)
                     .proxy(proxy)
                     .headers(headMap())
                     .execute();
            }
            content = response.body();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return content;
    }

    private static Map<String, String> headMap() {

        Map<String, String> headMap = new HashMap<String, String>();

        headMap.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headMap.put("Accept-Encoding", "gzip, deflate");
        headMap.put("Accept-Language", "zh-CN,zh;q=0.9");
        return headMap;
    }
}
