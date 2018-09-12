package com.mstx.framwork.common.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public final class CodecUtil {

	private CodecUtil() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 获取自动签名后的sign
	 * 
	 * @param paramValues
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public static String autoSign(Map<String, String> paramValues, String secret)
			throws Exception {
		// 检测是否是TreeMap
		Map<String, String> params;
		if (!(paramValues instanceof TreeMap)) {
			params = new TreeMap<String, String>();
			params.putAll(paramValues);
		} else {
			params = paramValues;
		}

		return sign(params, secret);
	}

	/**
	 * 对paramValues进行签名
	 * 
	 * @param paramValues
	 * @param secret
	 * @return
	 * @throws Exception
	 */
	public static String sign(Map<String, String> paramValues, String secret)
			throws Exception {

		StringBuilder sb = new StringBuilder(secret);

		for (Map.Entry entry : paramValues.entrySet()) {
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();

			if (StringUtil.areNotEmpty(new String[] { name, value })) {
				sb.append(name).append(value);
			}
		}

		sb.append(secret);
		byte[] sha1Digest = getSHA1Digest(sb.toString());

		return byte2hex(sha1Digest);

	}

	private static byte[] getSHA1Digest(String data) throws IOException {
		byte[] bytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			bytes = md.digest(data.getBytes("utf-8"));
		} catch (GeneralSecurityException gse) {
			throw new IOException(gse);
		}
		return bytes;
	}

	/**
	 * 二进制转十六进制字符串
	 * 
	 * @param bytes
	 * @return
	 */
	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
}
