/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adriens.oracle.jutils.exiftest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;

/**
 *
 * @author salad74
 */
public class Parser {

    public static void main(String[] args) {
        try {
            JpegGeoTagReader jpegGeoTagReader = new JpegGeoTagReader();
            File photoFile = new File("photo.jpg");
            GeoTag geoTag = jpegGeoTagReader.readMetadata(photoFile);
            double altitude = geoTag.getAltitude();
            double latitude = geoTag.getLatitude();
            double longitude = geoTag.getLongitude();

            System.out.println("latitude : " + latitude);
            System.out.println("longitude : " + longitude);
            System.out.println("altitude : " + altitude);
            // https://www.google.com/maps/search/?api=1&query=58.698017,-152.522067
            String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
            System.out.println(url);
            //QRCode.from("Hello World").file("QRCode");
            String qrCodeText = url;
            String filePath = "QRCODE.png";
            int size = 125;
            String fileType = "png";
            File qrFile = new File(filePath);
            createQRImage(qrFile, qrCodeText, size, fileType);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void createQRImage(File qrFile, String qrCodeText, int size,
            String fileType) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable hintMap = new Hashtable();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText,
                BarcodeFormat.QR_CODE.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        ImageIO.write(image, fileType, qrFile);
    }

}
