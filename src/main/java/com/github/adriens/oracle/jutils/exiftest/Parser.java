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
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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

            StringBuffer html = new StringBuffer("");
            html.append("<html><head><title>Exemple demande intervention cometière</title></head>");
            html.append("<body>");
            html.append("<a href=\"" + url + "\"><img src=\"cid:qrcode\"/></a>");
            html.append("<a href=\"" + url + "\"><img src=\"cid:photo\"/></a>");
            
            html.append("</body>");
            html.append("</html>");

            // create a dummy html
            PrintWriter writer = new PrintWriter("mail.html");
            writer.append(html);

            writer.flush();
            writer.close();

            // send mail
            // Recipient's email ID needs to be mentioned.
            String to = "adrien.sales@ville-noumea.nc";

            // Sender's email ID needs to be mentioned
            //String from = "shinigami-noreply@ville-noumea.nc";
            String from = "pinpin@ville-noumea.nc";

            // Assuming you are sending email from localhost
            String host = "svpcas.site-mairie.noumea.nc.";

            // Get system properties
            Properties properties = new Properties();

            // Setup mail server
            properties.put("mail.smtp.host", host);
            //properties.put("mail.smtp.auth", "false");
            //properties.put("mail.smtp.starttls.enable", "true");
            //properties.put("mail.smtp.host", "smtp.gmail.com");
            //properties.put("mail.smtp.port", "25");

            // Get the default Session object.
            Session session = Session.getInstance(properties);
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            MimeMultipart multipart = new MimeMultipart();

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            
            
            // bodypart : html
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            
            messageBodyPart.setContent(html.toString(), "text/html");
            multipart.addBodyPart(messageBodyPart);
            
            // second part (the image)
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.attachFile("QRCODE.png");
            imagePart.setContentID("<" + "qrcode" + ">");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            imagePart.setHeader("Content-Type", "image/png");
            multipart.addBodyPart(imagePart);
            
            MimeBodyPart photoPart = new MimeBodyPart();
            photoPart.attachFile("photo.jpg");
            photoPart.setContentID("<" + "photo" + ">");
            photoPart.setDisposition(MimeBodyPart.INLINE);
            photoPart.setHeader("Content-Type", "image/jpg");
            multipart.addBodyPart(photoPart);

            // add image to the multipart
            //multipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(multipart);
            message.setSubject("Mail de test");
            // Send message
            Transport.send(message);

            System.out.println("Sent message successfully....");

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
