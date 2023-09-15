package com.atguigu.yygh.order.utils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * HttpClient类用于发送HTTP请求
 */
public class HttpClient {

    private String url; // 请求的URL
    private Map<String, String> param; // 请求参数
    private int statusCode; // 响应状态码
    private String content; // 响应内容
    private String xmlParam; // XML格式的请求参数
    private boolean isHttps; // 是否使用HTTPS协议
    private boolean isCert = false; // 是否使用证书进行身份验证
    private String certPassword; // 证书密码 微信商户号（mch_id）

    public boolean isHttps() {
        return isHttps;
    }
    public void setHttps(boolean isHttps) {
        this.isHttps = isHttps;
    }
    public boolean isCert() {
        return isCert;
    }
    public void setCert(boolean cert) {
        isCert = cert;
    }
    public String getXmlParam() {
        return xmlParam;
    }
    public void setXmlParam(String xmlParam) {
        this.xmlParam = xmlParam;
    }
    public HttpClient(String url, Map<String, String> param) {
        this.url = url;
        this.param = param;
    }
    public HttpClient(String url) {
        this.url = url;
    }
    public String getCertPassword() {
        return certPassword;
    }
    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }
    public void setParameter(Map<String, String> map) {
        param = map;
    }


    /**
     * 添加请求参数的键值对
     *
     * @param key   参数的键
     * @param value 参数的值
     */
    public void addParameter(String key, String value) {
        if (param == null)
            param = new HashMap<String, String>();
        param.put(key, value);
    }

    /**
     * 发送 HTTP POST 请求
     *
     * @throws ClientProtocolException 如果发生协议异常
     * @throws IOException             如果发生 IO 异常
     */
    public void post() throws ClientProtocolException, IOException {
        HttpPost http = new HttpPost(url);
        setEntity(http);
        execute(http);
    }

    /**
     * 发送 HTTP PUT 请求
     *
     * @throws ClientProtocolException 如果发生协议异常
     * @throws IOException             如果发生 IO 异常
     */
    public void put() throws ClientProtocolException, IOException {
        HttpPut http = new HttpPut(url);
        setEntity(http);
        execute(http);
    }

    /**
     * 发送 HTTP GET 请求
     *
     * @throws ClientProtocolException 如果发生协议异常
     * @throws IOException             如果发生 IO 异常
     */
    public void get() throws ClientProtocolException, IOException {
        if (param != null) {
            StringBuilder url = new StringBuilder(this.url);
            boolean isFirst = true;
            for (String key : param.keySet()) {
                if (isFirst)
                    url.append("?");
                else
                    url.append("&");
                url.append(key).append("=").append(param.get(key));
            }
            this.url = url.toString();
        }
        HttpGet http = new HttpGet(url);
        execute(http);
    }

    /**
     * 设置 HTTP 请求实体
     *
     * @param http HTTP 请求对象
     */
    private void setEntity(HttpEntityEnclosingRequestBase http) {
        if (param != null) {
            List<NameValuePair> nvps = new LinkedList<NameValuePair>();
            for (String key : param.keySet())
                nvps.add(new BasicNameValuePair(key, param.get(key))); // 参数
            http.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8)); // 设置参数
        }
        if (xmlParam != null) {
            http.setEntity(new StringEntity(xmlParam, Consts.UTF_8));
        }
    }

    /**
     * 执行HTTP请求
     *
     * @param http HTTP请求对象
     * @throws ClientProtocolException 如果在HTTP协议上发生错误
     * @throws IOException            如果发生I/O错误
     */
    private void execute(HttpUriRequest http) throws ClientProtocolException,
            IOException {
        CloseableHttpClient httpClient = null;
        try {
            if (isHttps) {
                if (isCert) {
                    // 使用证书进行身份验证
                    FileInputStream inputStream = new FileInputStream(new File(ConstantPropertiesUtils.CERT));
                    KeyStore keystore = KeyStore.getInstance("PKCS12");
                    char[] partnerId2charArray = certPassword.toCharArray();
                    keystore.load(inputStream, partnerId2charArray);

                    // 创建 SSL 上下文并加载证书
                    SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                            new String[] { "TLSv1" },
                            null,
                            SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                } else {
                    // 不使用证书进行身份验证
                    SSLContext sslContext = new SSLContextBuilder()
                            .loadTrustMaterial(null, new TrustStrategy() {
                                // 信任所有证书
                                public boolean isTrusted(X509Certificate[] chain,
                                                         String authType)
                                        throws CertificateException {
                                    return true;
                                }
                            }).build();
                    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
                    httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
                }
            } else {
                // 非HTTPS请求
                httpClient = HttpClients.createDefault();
            }

            CloseableHttpResponse response = httpClient.execute(http);
            try {
                if (response != null) {
                    if (response.getStatusLine() != null) {
                        // 获取响应状态码
                        statusCode = response.getStatusLine().getStatusCode();
                    }
                    HttpEntity entity = response.getEntity();
                    // 响应内容
                    content = EntityUtils.toString(entity, Consts.UTF_8);
                }
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }

        /**
         * execute()方法的作用，即执行HTTP请求。
         * 它接受一个HttpUriRequest对象作为参数，并且可能抛出ClientProtocolException和IOException异常。
         * 方法内部的代码根据条件判断处理HTTPS请求和非HTTPS请求，进行身份验证，发送请求并获取响应。最终关闭HTTP客户端。
         */
    }

    /**
     * 获取响应状态码
     *
     * @return 响应状态码
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * 获取响应内容
     *
     * @return 响应内容
     * @throws ParseException 如果解析响应内容时发生异常
     * @throws IOException    如果读取响应内容时发生异常
     */
    public String getContent() throws ParseException, IOException {
        return content;
    }

}
