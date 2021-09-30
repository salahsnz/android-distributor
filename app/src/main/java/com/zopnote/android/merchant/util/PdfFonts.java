package com.zopnote.android.merchant.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import java.io.IOException;

public class PdfFonts {

    private static BaseFont base;

    {
        try {
            base = BaseFont.createFont("sans", BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private  static Font fontSansBold = new Font(base, 20f, Font.BOLD);
    public static Font bold20 = new Font(fontSansBold);

    private  static  Font fontSans14 = new Font(base, 14f, Font.BOLD);
    public static Font bold14 = new Font(fontSans14);

    private  static  Font fontSans14Normal = new Font(base, 14f, Font.NORMAL);
    public static Font normal14 = new Font(fontSans14Normal);

    private  static  Font fontSans10Normal = new Font(base, 10f, Font.NORMAL);
    public static Font normal10 = new Font(fontSans10Normal);

    private  static  Font fontSans10Bold = new Font(base, 10f, Font.BOLD);
    public static Font bold10 = new Font(fontSans10Bold);

    private  static  Font fontSans11Normal = new Font(base, 11f, Font.NORMAL);
    public static Font normal11 = new Font(fontSans11Normal);

    private  static  Font fontSans16Bold = new Font(base, 16f, Font.BOLD);
    public static Font bold16 = new Font(fontSans16Bold);

    private  static  Font fontSans16Normal = new Font(base, 16f, Font.NORMAL);
    public static Font normal16 = new Font(fontSans16Normal);


    private  static  Font fontSans11Italic = new Font(base, 11f, Font.ITALIC, BaseColor.RED);
    public static Font italic11 = new Font(fontSans11Italic);



}
