package net.ahwater.ahwaterCloud.util;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;


/**
 * Created by TECHMAN on 2018/3/5.
 */
public class HttpUtils {

    private static final Logger logger = LogManager.getLogger(HttpUtils.class);

    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * 发送get请求并返回字符串形式的响应内容，注意，如果响应内容非常多可能不适合使用此方法
     *
     * @param url
     * @param params 请求参数
     * @return
     * @throws Exception
     */
    public static String get(String url, String params) throws Exception {

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            //if (params != null) {
            //    //添加请求头
            //    Set<Entry<String, String>> entrySet = params.entrySet();
            //    for (Entry<String, String> entry : entrySet) {
            //       httpGet.setHeader(entry.getKey(), entry.getValue());
            //    }
            //}
            httpGet.setHeader("X-Auth-Token", params);
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.setHeader("charset", "UTF-8");
            //发送请求并接收响应
            httpResponse = httpClient.execute(httpGet);

            //以字符串形式返回响应内容
            String result = EntityUtils.toString(httpResponse.getEntity());
            logger.info("发送http get请求，url：{}，请求参数：{}，响应结果：{}", url, params, result);
            return result;

        } finally {
            closeQuietly(httpResponse);
            closeQuietly(httpClient);
        }
    }

    public static String post(String url) throws IOException {
        return post(url, null,null);
    }

    //发送post请求并返回字符串形式的响应内容
    public static String post(String url, String tokenId, String params) throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("X-Auth-Token", tokenId);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("charset", "UTF-8");
            StringEntity entity = new StringEntity(params, "UTF-8");
            httpPost.setEntity(entity);
            httpResponse = httpClient.execute(httpPost);

            String result = EntityUtils.toString(httpResponse.getEntity());
            logger.info("发送http post请求，url：{}，请求参数：{}，响应结果：{}", url, params, result);
            return result;
        } finally {
            closeQuietly(httpResponse);
            closeQuietly(httpClient);
        }
    }

    //发送put请求并返回字符串形式的响应内容
    public static String put(String url, String tokenId, String params) throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPut httpPut = new HttpPut(url);
            httpPut.setHeader("X-Auth-Token", tokenId);
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.setHeader("charset", "UTF-8");
            StringEntity entity = new StringEntity(params, "UTF-8");
            httpPut.setEntity(entity);
            httpResponse = httpClient.execute(httpPut);

            String result = EntityUtils.toString(httpResponse.getEntity());
            logger.info("发送http put请求，url：{}，请求参数：{}，响应结果：{}", url, params, result);
            return result;
        } finally {
            closeQuietly(httpResponse);
            closeQuietly(httpClient);
        }
    }

    //发送patch请求并返回字符串形式的响应内容
    public static String patch(String url, String tokenId, String params) throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpPatch httpPatch = new HttpPatch(url);
            httpPatch.setHeader("Content-type", "application/openstack-images-v2.1-json-patch");
            httpPatch.setHeader("Charset", "UTF-8");
            httpPatch.setHeader("Accept", "application/json");
            httpPatch.setHeader("Accept-Charset", "UTF-8");
            httpPatch.setHeader("X-Auth-Token", tokenId);
            StringEntity entity = new StringEntity(params, "UTF-8");
            httpPatch.setEntity(entity);
            httpResponse = httpClient.execute(httpPatch);

            String result = EntityUtils.toString(httpResponse.getEntity());
            logger.info("发送http patch请求，url：{}，请求参数：{}，响应结果：{}", url, params, result);
            return result;
        } finally {
            closeQuietly(httpResponse);
            closeQuietly(httpClient);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.warn("关闭closeable时出错误", e);
            }
        }
    }

    public static String readUrlStream(HttpURLConnection connection) throws IOException {
        int len = connection.getContentLength();
        InputStream is = connection.getInputStream();
        String result = null;
        if (len != -1) {
            byte[] data = new byte[len];
            byte[] temp = new byte[512];
            int readLen = 0;
            int destPos = 0;
            while ((readLen = is.read(temp)) > 0) {
                System.arraycopy(temp, 0, data, destPos, readLen);
                destPos += readLen;
            }
            return new String(data, "UTF-8");
        }
        return result;
    }

    //public static String getResultForComputeService(String tokenId, String strURL, String param, String result) {
    //    try {
    //        URL url = new URL(strURL);
    //        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //        connection.setDoOutput(true);
    //        connection.setDoInput(true);
    //        connection.setUseCaches(false);
    //        connection.setInstanceFollowRedirects(true);
    //        connection.setRequestMethod("PUT");
    //        connection.setRequestProperty("X-Auth-Token", tokenId);
    //        connection.setRequestProperty("Content-Type", "application/json");
    //        connection.connect();
    //        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
    //        out.append(param);
    //        out.flush();
    //        out.close();
    //
    //        result = HttpUtils.readUrlStream(connection);
    //
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //    return result;
    //}

    //public static String getResultForZoneAndImage(Token token, String urlst) throws IOException {
    //    URL url;
    //    String result;
    //    url = new URL(urlst);
    //    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    //    connection.setDoOutput(true);
    //    connection.setDoInput(true);
    //    connection.setInstanceFollowRedirects(true);
    //    connection.setRequestMethod("GET");
    //    connection.setRequestProperty("X-Auth-Token", token.getId());
    //    connection.setRequestProperty("Content-Type", "application/json");
    //    connection.setRequestProperty("charset", "UTF-8");
    //    connection.connect();
    //
    //    result = HttpUtils.readUrlStream(connection);
    //
    //    return result;
    //}
}
