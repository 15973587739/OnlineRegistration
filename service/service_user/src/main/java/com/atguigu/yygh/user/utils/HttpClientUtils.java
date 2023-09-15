package com.atguigu.yygh.user.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HttpClientUtils {

    public static final int connTimeout=10000; // 连接超时时间，单位：毫秒
    public static final int readTimeout=10000; // 响应超时时间，单位：毫秒
    public static final String charset="UTF-8"; // 字符编码
    private static HttpClient client = null; // HttpClient 实例


    static {
        // 创建 HttpClient 对象并配置连接池
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128); // 设置连接池的最大连接数
        cm.setDefaultMaxPerRoute(128); // 设置每个路由的最大连接数
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 发送带参数的 POST 请求
     *
     * @param url 请求的URL
     * @param parameterStr 参数字符串
     * @param charset 字符编码
     * @param connTimeout 建立连接超时时间，单位：毫秒
     * @param readTimeout 响应超时时间，单位：毫秒
     * @return 响应结果
     * @throws ConnectTimeoutException 建立连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 异常
     */
    public static String postParameters(String url, String parameterStr,String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }
    public static String postParameters(String url, Map<String, String> params, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {
        return postForm(url, params, null, connTimeout, readTimeout);
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求的URL
     * @return 响应结果
     * @throws Exception 异常
     */
    public static String get(String url) throws Exception {
        return get(url, charset, null, null);
    }
    public static String get(String url, String charset) throws Exception {
        return get(url, charset, connTimeout, readTimeout);
    }

    /**
     * 发送一个 Post 请求, 使用指定的字符集编码.
     *
     * @param url
     * @param body RequestBody
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset 编码
     * @param connTimeout 建立链接超时时间,毫秒.
     * @param readTimeout 响应超时时间,毫秒.
     * @return ResponseBody, 使用指定的字符集编码.
     * @throws ConnectTimeoutException 建立链接超时异常
     * @throws SocketTimeoutException  响应超时
     * @throws Exception
     */
    public static String post(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout)
            throws ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }


    /**
     * 提交 form 表单的 POST 请求
     *
     * @param url 请求的 URL
     * @param params 表单参数键值对
     * @param headers 请求头参数键值对
     * @param connTimeout 建立连接超时时间，单位：毫秒
     * @param readTimeout 响应超时时间，单位：毫秒
     * @return 响应结果
     * @throws ConnectTimeoutException 建立连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 异常
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,
            SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        try {
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<NameValuePair>();
                Set<Entry<String, String>> entrySet = params.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);
                post.setEntity(entity);
            }

            if (headers != null && !headers.isEmpty()) {
                for (Entry<String, String> entry : headers.entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
    }
    /**
     * 上述代码段中的方法用于提交 form 表单的 POST 请求。
     * 相关方法的注释提供了详细的参数说明和异常说明，方便了解方法的作用和使用方式。
     * 方法会根据参数构建 HTTP 请求，并发送到指定的 URL，然后返回响应结果。
     * 其中，还包括建立连接超时时间和响应超时时间的设置，以及对 HTTPS 请求的处理。
     * 最后，使用 IOUtils 将响应内容转换为字符串并返回。
     */


    /**
     * 发送一个 GET 请求
     *
     * @param url 请求的 URL
     * @param charset 字符编码
     * @param connTimeout 建立连接超时时间，单位：毫秒
     * @param readTimeout 响应超时时间，单位：毫秒
     * @return 响应结果
     * @throws ConnectTimeoutException 建立连接超时异常
     * @throws SocketTimeoutException 响应超时异常
     * @throws Exception 异常
     */
    public static String get(String url, String charset, Integer connTimeout,Integer readTimeout)
            throws ConnectTimeoutException,SocketTimeoutException, Exception {

        HttpClient client = null;
        HttpGet get = new HttpGet(url);
        String result = "";
        try {
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            get.setConfig(customReqConf.build());

            HttpResponse res = null;

            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(get);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(get);
            }

            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            get.releaseConnection();
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }
    /**
     * 上述代码段中的方法用于发送一个 GET 请求。
     * 相关方法的注释提供了详细的参数说明和异常说明，方便了解方法的作用和使用方式。
     * 方法会根据参数构建 HTTP 请求，并发送到指定的 URL，然后返回响应结果。
     * 其中，还包括建立连接超时时间和响应超时时间的设置，以及对 HTTPS 请求的处理。
     * 最后，使用 IOUtils 将响应内容转换为字符串并返回。
     */

    /**
     * 从 response 里获取 charset
     *
     * @param response HTTP 响应对象
     * @return 字符编码
     */
    @SuppressWarnings("unused") //是 Java 中的一个注解，用于告知编译器忽略未使用的变量、方法、类等的警告信息。在给定的代码中，
    // @SuppressWarnings("unused") 注解被用于修饰一个方法，表示告知编译器忽略该方法未被使用的警告。
    private static String getCharsetFromResponse(HttpResponse response) {
        // Content-Type:text/html; charset=GBK
        if (response.getEntity() != null && response.getEntity().getContentType() != null && response.getEntity().getContentType().getValue() != null) {
            String contentType = response.getEntity().getContentType().getValue();
            if (contentType.contains("charset=")) {
                return contentType.substring(contentType.indexOf("charset=") + 8);
            }
        }
        return null;
    }
    /**
     * 上述代码段中的方法用于从 HTTP 响应中获取字符编码。
     * 相关方法的注释提供了参数说明，表示此方法接受一个 HTTP 响应对象作为输入，并尝试从响应的内容类型中提取字符编码。
     * 方法会提取出字符编码，
     * 并返回。如果无法找到字符编码，则返回 null。在方法中，通过判断内容类型是否包含 charset= 字段，并使用字符串截取的方式提取出字符编码部分。
     * 最后，将提取出来的字符编码返回。
     */

    /**
     * 创建 SSL 连接的不安全客户端
     *
     * @return 创建的不安全 CloseableHttpClient 对象
     * @throws GeneralSecurityException 生成 SSLContext 时的安全异常
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            // 创建不验证证书的信任策略
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();

            // 创建不验证主机名的 SSL 连接套接字工厂
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl)
                        throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert)
                        throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns,
                                   String[] subjectAlts) throws SSLException {
                }
            });

            // 使用自定义 SSL 连接套接字工厂创建自定义的 CloseableHttpClient 客户端
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }

    /**
     * 上述代码段中的方法用于创建一个 SSL 连接的不安全客户端 CloseableHttpClient。
     * 相关方法的注释提供了方法的目的和用法说明。
     * 该方法使用 SSLContextBuilder 创建了一个不验证证书的信任策略，并通过 SSLConnectionSocketFactory 创建了一个不验证主机名的 SSL 连接套接字工厂。
     * 最后，使用自定义的 SSL 连接套接字工厂创建了一个自定义的 CloseableHttpClient 客户端，并返回该客户端对象。
     *
     * 需要注意的是，此方法创建的客户端存在安全风险，因为它不验证服务器的证书和主机名。
     * 一般情况下，为了保证通信的安全性，应该使用验证证书和主机名的 SSL 连接。
     */

}

