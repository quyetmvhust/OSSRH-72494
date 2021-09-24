package org.watermark.engine;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.ByteArrayOutputStream;

public class WaterMarkEngine {
    public static String generateWaterMarkText(String base64, int divx, int divy, int rotate, int security, String content, int fontsize, String pathFont) throws Exception {
        //Declare variable
        String data = content;

        // Set default value if variable is null
        if (divx == 0) divx = 2;
        if (divy == 0) divy = 2;
        if (security == 1) {
            for (int i = 0; i < 30; i++) {
                data += content + " ";
            }
        }
        if (security == 1) rotate = 40;

        PdfReader reader = new PdfReader(Base64.decode(base64));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        BaseFont bf = BaseFont.createFont(pathFont + "/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font FONT = new Font(bf, fontsize, Font.NORMAL, new GrayColor(0.5f));
        Phrase p = new Phrase(data, FONT);

        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;

        // loop over every page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / divx;
            y = (pagesize.getTop() + pagesize.getBottom()) / divy;
            over = stamper.getOverContent(i);
            over.saveState();

            // set transparency
            PdfGState state = new PdfGState();
            state.setFillOpacity(0.2f);
            over.setGState(state);

            // add watermark text
            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, rotate);

            over.restoreState();
        }
        stamper.close();
        reader.close();

        String base64pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
        baos.close();

        return base64pdf;

    }


    public static String generateWaterMarkImage(String base64, int divx, int divy, int rotate, String base64Image, int security, String content, int fontsize, String pathFont) throws Exception {
        //Declare variable
        String data = content;

        // Set default value if variable is null
        if (divx == 0) divx = 2;
        if (divy == 0) divy = 2;
        if (security == 1) {
            for (int i = 0; i < 30; i++) {
                data += content + " ";
            }
        }
        if (security == 1) rotate = 40;


        PdfReader reader = new PdfReader(Base64.decode(base64));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        BaseFont bf = BaseFont.createFont(pathFont + "/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font FONT = new Font(bf, fontsize, Font.NORMAL, new GrayColor(0.5f));
        Phrase p = new Phrase(data, FONT);

        // image watermark
        Image img = Image.getInstance(Base64.decode(base64Image));

        float w = img.getScaledWidth();
        float h = img.getScaledHeight();

        // properties
        PdfContentByte over;
        Rectangle pagesize;
        float x, y;

        // loop over every page
        int n = reader.getNumberOfPages();
        for (int i = 1; i <= n; i++) {

            // get page size and position
            pagesize = reader.getPageSizeWithRotation(i);
            x = (pagesize.getLeft() + pagesize.getRight()) / divx;
            y = (pagesize.getTop() + pagesize.getBottom()) / divy;
            over = stamper.getOverContent(i);
            over.saveState();

            // set transparency
            PdfGState state = new PdfGState();
            state.setFillOpacity(0.2f);
            over.setGState(state);

            // add watermark text
            ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, x, y, rotate);
            over.addImage(img, w, 0, 0, h, x - (w / divx), y - (h / divx));

            over.restoreState();
        }
        stamper.close();
        reader.close();

        String base64pdf = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
        baos.close();

        return base64pdf;
    }

    public static String decryptSecurityData(String data, String secret_key) {
        String decryptedData = AES256.decrypt(data, secret_key);
        return decryptedData;
    }

    public static String encryptSecurityData(String data, String secret_key) {
        String encryptData = AES256.encrypt(data, secret_key);
        return encryptData;
    }


}
