package com.mstx.framwork.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

//http连接工具
public class HttpConnectUtil {

	private static final int READTIME = 30000;
	private static final int CONNECTTIMEOUT = 30000;
	private static HttpURLConnection httpURLConn;

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

		byte[] data = getRequestData(params, encode).toString().getBytes();// 获得请求�?

		URL url = new URL(connectUrl);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url
				.openConnection();
		httpURLConnection.setConnectTimeout(CONNECTTIMEOUT); // 设置连接超时时间
		httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
		httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
		httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
		httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存
		// 设置请求体的类型是文本类�?
		httpURLConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// 设置请求体的长度
		httpURLConnection.setRequestProperty("Content-Length",
				String.valueOf(data.length));
		// 获得输出流，向服务器写入数据
		OutputStream outputStream = httpURLConnection.getOutputStream();
		outputStream.write(data);
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
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信�?
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除�?��的一�?&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

	/**
	 * 处理返回的结�?
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

	/**
	 * 图片文件上传
	 * 
	 * @param actionUrl
	 * @param params
	 * @param files
	 * @return
	 * @throws IOException
	 */
	public static String imageFilePost(String actionUrl,
			Map<String, String> params, Map<String, File> files)
			throws IOException {

		String BOUNDARY = java.util.UUID.randomUUID().toString();
		String PREFIX = "--", LINEND = "\r\n";
		String MULTIPART_FROM_DATA = "multipart/form-data";
		String CHARSET = "UTF-8";
		URL uri = new URL(actionUrl);
		HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
		conn.setReadTimeout(READTIME);
		conn.setDoInput(true);// 允许输入
		conn.setDoOutput(true);// 允许输出
		conn.setUseCaches(false);
		conn.setRequestMethod("POST"); // Post方式
		conn.setRequestProperty("connection", "keep-alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
				+ ";boundary=" + BOUNDARY);

		// 首先组拼文本类型的参�?
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(entry.getValue());
			sb.append(LINEND);
		}

		DataOutputStream outStream = new DataOutputStream(
				conn.getOutputStream());
		outStream.write(sb.toString().getBytes());

		// 发�?文件数据
		if (files != null)
			for (Map.Entry<String, File> file : files.entrySet()) {
				StringBuilder sb1 = new StringBuilder();
				sb1.append(PREFIX);
				sb1.append(BOUNDARY);
				sb1.append(LINEND);
				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
						+ file.getKey() + "\"" + LINEND);
				sb1.append("Content-Type: application/octet-stream; charset="
						+ CHARSET + LINEND);
				sb1.append(LINEND);
				outStream.write(sb1.toString().getBytes());
				InputStream is = new FileInputStream(file.getValue());
				byte[] buffer = new byte[1024 * 1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}

				is.close();
				outStream.write(LINEND.getBytes());
			}

		// 请求结束标志
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
		outStream.write(end_data);
		outStream.flush();

		// 得到响应�?
		int res = conn.getResponseCode();
		InputStream in = conn.getInputStream();
		InputStreamReader isReader = new InputStreamReader(in);
		String message = HttpConnectUtil.getHttpStream(in);
		BufferedReader bufReader = new BufferedReader(isReader);
		String line = null;
		String data = "OK";

		// while ((line = bufReader.readLine()) != null)
		// data += line;

		// if (res == 200) {
		// int ch;
		// StringBuilder sb2 = new StringBuilder();
		// while ((ch = in.read()) != -1) {
		// sb2.append((char) ch);
		// }
		// }
		outStream.close();
		conn.disconnect();

		return message.toString();
	}

	// http获取json
	public static HttpURLConnection connect(StringBuffer loginUrl, String method)
			throws MalformedURLException, ProtocolException, IOException {

		URL loginURL = new URL(loginUrl.toString());
		httpURLConn = (HttpURLConnection) loginURL.openConnection();
		httpURLConn.setReadTimeout(READTIME);
		httpURLConn.setConnectTimeout(CONNECTTIMEOUT);
		httpURLConn.setRequestMethod(method);
		httpURLConn.setDoOutput(true);

		return httpURLConn;

	}

	// HTTP获取图片
	public static HttpURLConnection connect(String loginUrl, String method)
			throws MalformedURLException, ProtocolException, IOException {

		URL loginURL = new URL(loginUrl);
		httpURLConn = (HttpURLConnection) loginURL.openConnection();
		httpURLConn.setReadTimeout(READTIME);
		httpURLConn.setConnectTimeout(CONNECTTIMEOUT);
		httpURLConn.setRequestMethod(method);
		return httpURLConn;

	}

	// HTTP 移动巡查信息上传到服务器
	public static HttpURLConnection uploadConnect(StringBuffer loginUrl,
			String method) throws MalformedURLException, ProtocolException,
			IOException {

		URL loginURL = new URL(loginUrl.toString());
		httpURLConn = (HttpURLConnection) loginURL.openConnection();
		httpURLConn.setDoInput(true); // 允许输入�?
		httpURLConn.setDoOutput(true); // 允许输出�?
		httpURLConn.setUseCaches(false); // 不允许使用缓�?
		httpURLConn.setReadTimeout(READTIME);
		httpURLConn.setConnectTimeout(CONNECTTIMEOUT);
		httpURLConn.setRequestMethod(method);
		httpURLConn.setRequestProperty("connection", "Keep-Alive");
		httpURLConn.setRequestProperty("Charsert", "UTF-8");

		return httpURLConn;

	}

	// HTTP 上传经纬�?
	public static HttpURLConnection locationConnect(StringBuffer localUrl,
			String method) throws MalformedURLException, ProtocolException,
			IOException {
		URL url = new URL(localUrl.toString());
		httpURLConn = (HttpURLConnection) url.openConnection();
		httpURLConn.setConnectTimeout(CONNECTTIMEOUT);
		httpURLConn.setUseCaches(false); // 不允许使用缓�?
		httpURLConn.setRequestMethod(method); // 请求方式
		return httpURLConn;
	}

	// 保存网络图片到本�?

	public static void saveIntentImage(InputStream inputStream,
			String imageDir, String imageName) throws FileNotFoundException,
			IOException {

		File dir = new File(imageDir, "");

		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(imageDir, imageName);

		OutputStream outputStream = new FileOutputStream(dir);
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);

		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}

	// 下载文本文件到本�?
	public static void saveFile2Temp(InputStream inputStream, String path,
			String fileName) throws IOException {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(dir, fileName);
		if (dir.exists()) {
			dir.delete();
		}
		OutputStream outputStream = new FileOutputStream(dir);
		byte[] buffer = new byte[1024 * 10];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);

		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();
	}

	// 获取输入数据�?
	// public static String getHttpStream(InputStream inputStream)
	// throws UnsupportedEncodingException, IOException {
	// StringBuffer result = new StringBuffer();
	// byte[] buffer = new byte[1024];
	// int len = 0;
	// while ((len = inputStream.read(buffer)) != -1) {
	// result.append(new String(buffer, 0, len, "UTF-8"));
	//
	// }
	// inputStream.close();
	// return result.toString().trim();
	//
	// }

	// 获取输入数据流
	public static String getHttpStream(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 10];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inputStream.close();
		return new String(data, "UTF-8");
	}

	// 获取提交过来的字节流
	public static byte[] getByte(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 10];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		return outStream.toByteArray();
	}

	// 获取输入数据�?
	public static String getHttpStream(HttpURLConnection httpURLConnection)
			throws UnsupportedEncodingException, IOException {
		InputStream inputStream = httpURLConnection.getInputStream();
		StringBuffer result = new StringBuffer();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			result.append(new String(buffer, 0, len, "UTF-8"));
		}
		return result.toString().trim();

	}

	// 获取上传经纬度状�?
	public static String getUpLoadState(InputStream inputStream)
			throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		int state;
		while ((state = inputStream.read()) != -1) {
			stringBuffer.append((char) state);
		}
		return stringBuffer.toString();

	}

	// 读取通讯录json
	public static String getHttpStreamTxl(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		StringBuffer result = new StringBuffer();
		byte[] buffer = new byte[1024 * 10];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			// result.append(new String(buffer, 0, len));
			result.append(new String(buffer, 0, len, "UTF-8"));

		}

		return result.toString().trim();

	}

	// 读取企业信息json
	public static String getHttpStreamBig(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		StringBuffer result = new StringBuffer();
		byte[] buffer = new byte[1024 * 512];
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			// result.append(new String(buffer, 0, len));
			result.append(new String(buffer, 0, len, "UTF-8"));

		}

		return result.toString().trim();

	}

	// 向网络上传数据文�?
	/**
	 * 
	 * @param httpURLConnection
	 * @param filename
	 * @param filePath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void OutPutData2Server(HttpURLConnection httpURLConnection,
			String filename, String filePath) throws FileNotFoundException,
			IOException {
		httpURLConnection.setRequestProperty("Content-Type",
				"multipart/form-data;file=" + filename);
		httpURLConnection.setRequestProperty("filename", filename);
		OutputStream outputStream = httpURLConnection.getOutputStream();

		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		InputStream inputStream = new FileInputStream(filePath);
		byte[] bytes = new byte[1024 * 1024];
		int len = 0;
		while ((len = inputStream.read(bytes)) != -1) {
			dataOutputStream.write(bytes, 0, len);
		}
		dataOutputStream.flush();
		dataOutputStream.close();
		inputStream.close();
	}

	// 读取二进制流
	public static byte[] getByte(HttpURLConnection connection)
			throws IOException {
		InputStream iStream = connection.getInputStream();
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 100];
		int len = 0;
		while ((len = iStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		iStream.close();
		outstream.close();
		return outstream.toByteArray();

	}

}
