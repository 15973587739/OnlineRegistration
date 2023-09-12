package com.atguigu.yygh.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 一个 HTTP 请求工具类 HttpUtil，用于发送 HTTP 请求并获取响应数据。
 * @author SIYU
 */
@Slf4j //使用 Lombok 注解，生成日志记录器。
public final class HttpUtil {

	static final String POST = "POST";
	static final String GET = "GET";
	static final int CONN_TIMEOUT = 30000; // 连接超时时间，单位：毫秒
	static final int READ_TIMEOUT = 30000; // 读取超时时间，单位：毫秒

	/**
	 * 使用 POST 方法发送 HTTP 请求
	 *
	 * @param strUrl  请求的 URL
	 * @param reqData 请求的数据
	 * @return 响应的数据
	 */
	public static byte[] doPost(String strUrl, byte[] reqData) {
		return send(strUrl, POST, reqData);
	}

	/**
	 * 使用 GET 方法发送 HTTP 请求
	 *
	 * @param strUrl 请求的 URL
	 * @return 响应的数据
	 */
	public static byte[] doGet(String strUrl) {
		return send(strUrl, GET, null);
	}

	/**
	 * 发送 HTTP 请求
	 *
	 * @param strUrl     请求的 URL
	 * @param reqMethod  请求的方法（GET/POST）
	 * @param reqData    请求的数据
	 * @return 响应的数据
	 */
	public static byte[] send(String strUrl, String reqMethod, byte[] reqData) {
		try {
			URL url = new URL(strUrl);
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setDoInput(true);
			httpConnection.setUseCaches(false);
			httpConnection.setInstanceFollowRedirects(true);
			httpConnection.setConnectTimeout(CONN_TIMEOUT);
			httpConnection.setReadTimeout(READ_TIMEOUT);
			httpConnection.setRequestMethod(reqMethod);
			httpConnection.connect();

			if (reqMethod.equalsIgnoreCase(POST)) {
				OutputStream os = httpConnection.getOutputStream();
				os.write(reqData);
				os.flush();
				os.close();
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(httpConnection.getInputStream(), "utf-8"));
			String inputLine;
			StringBuilder responseData = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				responseData.append(inputLine);
			}
			in.close();
			httpConnection.disconnect();

			return responseData.toString().getBytes();
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return null;
		}
	}

	/**
	 * 从输入流中读取数据
	 *
	 * @param inStream 输入流
	 * @return 读取到的数据
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray(); // 网页的二进制数据
		outStream.close();
		inStream.close();
		return data;
	}
}
