package org.vnpt.watermarkengine;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.codec.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

public class WaterMarkEngine {
    public static WaterMark generateWaterMarkText(String base64OrUrl, int divx, int divy, int rotate, int security, String secret_key, String content, String pathFont) throws Exception {
        //Declare variable
        int isUrlFile = 0;
        String data = content;

        // Set default value if variable is null
        if (divx == 0) divx = 2;
        if (divy == 0) divy = 2;
        if (security == 1) {
            for (int i = 0; i < 30; i++) {
                data += content + " ";
            }
        }
        if (base64OrUrl.contains("http")) isUrlFile = 1;
        if (security == 1) rotate = 45;

        PdfReader reader = null;
        if (isUrlFile == 1) {
            URL fromUrl = new File(base64OrUrl).toURI().toURL();
            reader = new PdfReader(fromUrl);

        } else {
            reader = new PdfReader(Base64.decode(base64OrUrl));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        BaseFont bf = BaseFont.createFont(pathFont + "/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font FONT = new Font(bf, 34, Font.NORMAL, new GrayColor(0.5f));
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

        String base64 = Base64.encodeBytes(baos.toByteArray());
        baos.close();


        WaterMark wt = new WaterMark();
        wt.setBase64File(base64);
        if(security == 1)
            wt.setSecurityData(AES256.encrypt(content, secret_key));

        return wt;
    }


    public static WaterMark generateWaterMarkImage(String base64OrUrl, int divx, int divy, int rotate, String base64OrUrlImage, int security, String secret_key, String content, String pathFont) throws Exception {
        //Declare variable
        int isUrlFile = 0;
        int isUrlImage = 0;
        String data = content;

        // Set default value if variable is null
        if (divx == 0) divx = 2;
        if (divy == 0) divy = 2;
        if (security == 1) {
            for (int i = 0; i < 30; i++) {
                data += content + " ";
            }
        }
        if (base64OrUrl.contains("http")) isUrlFile = 1;
        if (base64OrUrlImage.contains("http")) isUrlImage = 1;


        PdfReader reader = null;
        if (isUrlFile == 1) {
            URL fromUrl = new File(base64OrUrl).toURI().toURL();
            reader = new PdfReader(fromUrl);

        } else {
            reader = new PdfReader(Base64.decode(base64OrUrl));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        BaseFont bf = BaseFont.createFont(pathFont + "/Helvetica.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font FONT = new Font(bf, 34, Font.NORMAL, new GrayColor(0.5f));
        Phrase p = new Phrase(data, FONT);

        // image watermark
        Image img = null;
        if (isUrlImage == 1)
            img = Image.getInstance(base64OrUrlImage);
        else
            img = Image.getInstance(Base64.decode(base64OrUrlImage));

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

        String base64 = Base64.encodeBytes(baos.toByteArray());
        baos.close();

        WaterMark wt = new WaterMark();
        wt.setBase64File(base64);
        if(security == 1)
            wt.setSecurityData(AES256.encrypt(content, secret_key));

        return wt;
    }

    public static String decryptSecurityData(String data, String secret_key){
        String decryptedData = AES256.decrypt(data, secret_key);
        return decryptedData;
    }

}
