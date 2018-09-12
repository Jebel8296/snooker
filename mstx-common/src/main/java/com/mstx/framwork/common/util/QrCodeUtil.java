package com.mstx.framwork.common.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtil {


    private static int WIDTH = 300;
    private static int HEIGHT = 300;
    private static String FORMAT = "png";//二维码格式


    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int w = 300, h = 300;
        QrCodeUtil.outputImage(w, h, output, "https://qr.alipay.com/bax08539jjzibiwcisvn6050 \n");
        System.out.println(Base64Util.encode(output.toByteArray()));
    }

    public static void outputImage(int width, int height, OutputStream os, String url) {
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置容错等级
        hints.put(EncodeHintType.MARGIN, 2);//设置边距默认是5
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToStream(bitMatrix, FORMAT, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createZxingqrCode(String content) {
        Map hints = new HashMap();

        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//设置容错等级
        hints.put(EncodeHintType.MARGIN, 2);//设置边距默认是5

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH, HEIGHT, hints);
            Path path = new File("/Users/binger/qr.png").toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, path);//写到指定路径下

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //读取二维码
    public static void readZxingQrCode() {
        MultiFormatReader reader = new MultiFormatReader();
        File file = new File("/Users/binger/qr.png");
        try {
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//设置编码
            Result result = reader.decode(binaryBitmap, hints);
            System.out.println("解析结果:" + result.toString());
            System.out.println("二维码格式:" + result.getBarcodeFormat());
            System.out.println("二维码文本内容:" + result.getText());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
