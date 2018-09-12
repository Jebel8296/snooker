package com.mstx.framwork.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class HttpConnectPostUtil {
    /**
     * post数据提交到服务器
     *
     * @param params
     * @param encode
     * @param connectUrl
     * @return
     * @throws IOException
     */
    public static HttpURLConnection submitPostData(Map<String, String> params,
                                                   String encode, String connectUrl) throws IOException {

        byte[] data = getRequestData(params,encode).toString().getBytes();// 获得请求体

        URL url = new URL(connectUrl);
        System.out.println("提交url:" + connectUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(30000); // 设置连接超时时间
        httpURLConnection.setReadTimeout(30000);
        httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
        httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
        httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
        httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
        // 设置请求体的类型是文本类型
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        // 设置请求体的长度
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
        // System.out.println("11111111111");
        // 获得输出流，向服务器写入数据
        OutputStream outputStream = httpURLConnection.getOutputStream();
        // System.out.println("222222222222222");
        outputStream.write(data);
        // System.out.println("3333333333333");
        return httpURLConnection;
    }

    public static HttpURLConnection submitPostData2(Map<String, String> params,
                                                    String encode, String connectUrl) throws IOException {

        byte[] data = getRequestData(params, encode).toString().getBytes();// 获得请求体

        URL url = new URL(connectUrl);
        System.out.println("提交url:" + connectUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url
                .openConnection();
        httpURLConnection.setConnectTimeout(30000); // 设置连接超时时间
        httpURLConnection.setReadTimeout(30000);
        httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
        httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
        httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
        httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
        // 设置请求体的类型是文本类型
        httpURLConnection.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        // 设置请求体的长度
        httpURLConnection.setRequestProperty("Content-Length",
                String.valueOf(data.length));
        // 获得输出流，向服务器写入数据
        OutputStream outputStream = httpURLConnection.getOutputStream();
        // ByteArrayOutputStream byteArrayOutputStream = new
        // ByteArrayInputStream(data);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        byte[] data2 = new byte[1024 * 20];
        int len = 0;
        try {
            while ((len = inputStream.read(data2)) != -1) {
                outputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // outputStream.write(data);

        return httpURLConnection;
    }

    /**
     * 对post提交方式参数处理
     *
     * @param params
     * @param encode
     * @return
     */
    public static StringBuffer getRequestData(Map<String, String> params,
                                              String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        try {
            for (Entry<String, String> entry : params.entrySet()) {
                stringBuffer
                        .append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode((String) entry.getValue()
                                .toString(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
            System.out.println("请求url:" + stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    public static StringBuffer getRequestData2(Map<String, String> params,
                                               String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        try {
            for (Entry<String, String> entry : params.entrySet()) {
                stringBuffer
                        .append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode((String) entry.getValue()
                                .toString(), encode)).append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
            System.out.println("请求url:" + stringBuffer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /**
     * 处理返回的结果
     *
     * @param inputStream
     * @return
     */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null; // 存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());

        return resultData;
    }

}
