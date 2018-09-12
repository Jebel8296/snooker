package com.mstx.framwork.common.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SHA {

    public static byte[] getSHA1(String str) {
        byte cResult[] = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] content = null;
        try {
            content = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        md.update(content);
        cResult = md.digest();
        return cResult;
    }

    public static String getSHA(String str) {
        String result = "";
        byte cResult[] = null;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] content = null;
        try {
            content = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        md.update(content);
        cResult = md.digest();
        result = byteto10(cResult);
        return result;
    }

    private static String byteto10(byte tyds[]) // 字节转10进制
    {
        String result = "";
        for (int i = 0; i < tyds.length; i++) {
            int low = (tyds[i] & 0xf0) >> 4;// 取低4位
            int high = tyds[i] & 0xf;// 取高4位
            result = tiany10tohex(low, result); // 调用10进制转16进制
            result = tiany10tohex(high, result);// 调用10进制转16进制
        }
        return result;
    }

    private static String tiany10tohex(int a, String result)// 10进制转16进制
    {
        String str = new String("0123456789ABCDEF");
        result += str.toLowerCase().charAt((a));
        return result;
    }

}
