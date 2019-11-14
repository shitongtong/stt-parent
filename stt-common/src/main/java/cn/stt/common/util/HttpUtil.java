package cn.stt.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/11/28.
 */
public class HttpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @return
     */
    public static String sendGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(httpGet);
    }

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @param headerMap 请求头
     * @return
     */
    public static String sendGetWithHeader(String url, Map<String, String> headerMap) {
        HttpGet httpGet = new HttpGet(url);
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(httpGet);
    }

    /**
     * get请求
     *
     * @param url
     * @param paramMap 请求参数
     * @return
     */
    public static String sendGet(String url, Map<String, String> paramMap) {
        HttpGet httpGet = new HttpGet(jointParam(url, paramMap));
        return execute(httpGet);
    }

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @param headerMap 请求头
     * @param paramMap  请求参数
     * @return
     */
    public static String sendGet(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        HttpGet httpGet = new HttpGet(jointParam(url, paramMap));
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(httpGet);
    }

    /**
     * 拼接get请求的url和请求参数
     *
     * @param url
     * @param paramMap
     * @return
     */
    private static String jointParam(String url, Map<String, String> paramMap) {
        if (paramMap == null || paramMap.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        Set<Map.Entry<String, String>> entrySet = paramMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }


    /**
     * 发送不带参数的HttpPost请求
     *
     * @param url
     * @return
     */
    public static String sendPost(String url) {
        HttpPost httpPost = new HttpPost(url);
        return execute(httpPost);
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param headerMap 请求头
     * @return
     */
    public static String sendPostWithHeader(String url, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        return execute(httpPost);
    }

    /**
     * 发送HttpPost请求，参数为paramMap
     *
     * @param url
     * @param paramMap 请求参数
     * @return
     */
    public static String sendPost(String url, Map<String, String> paramMap) {
        return sendPost(url, paramMap, null);
    }

    /**
     * 发送HttpPost请求，参数为map,header
     *
     * @param url
     * @param paramMap
     * @param headerMap
     * @return
     */
    public static String sendPost(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(url);
        if (paramMap != null) {
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            List<NameValuePair> formparams = new ArrayList<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
            httpPost.setEntity(entity);
        }
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(httpPost);
    }


    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param jsonParam
     * @return
     */
    public static String sendPost(String url, String jsonParam) {
        return sendPost(url, jsonParam, null);
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param jsonParam
     * @param headerMap
     * @return
     */
    public static String sendPost(String url, String jsonParam, Map<String, String> headerMap) {
        HttpPost httpPost = new HttpPost(url);
        if (StringUtils.isNotBlank(jsonParam)) {
            httpPost.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            // 设置请求的参数
            StringEntity entity = new StringEntity(jsonParam, Consts.UTF_8);
            httpPost.setEntity(entity);
        }
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return execute(httpPost);
    }

    /**
     * 执行http方法
     *
     * @param httpRequestBase
     * @return
     */
    private static String execute(HttpRequestBase httpRequestBase) {
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = HTTP_CLIENT.execute(httpRequestBase);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            LOGGER.error("", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return result;
    }
}
