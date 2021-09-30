package com.zopnote.android.merchant.reports.subscription;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.managesubscription.SubscriptionUtil;
import com.zopnote.android.merchant.util.FormatUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.zopnote.android.merchant.util.PdfFonts.bold14;
import static com.zopnote.android.merchant.util.PdfFonts.bold16;
import static com.zopnote.android.merchant.util.PdfFonts.bold20;
import static com.zopnote.android.merchant.util.PdfFonts.italic11;
import static com.zopnote.android.merchant.util.PdfFonts.normal14;
import static com.zopnote.android.merchant.util.PdfFonts.normal16;


public class SubscriptionReportPDF {

    private static  Context context;
    private static SubscriptionsReportViewModel subscriptionsReportViewModel;

    public static void
    build(Context context, String name, SubscriptionsReportViewModel viewModelInstance, java.util.List<String> selectedItems) {
        context = context;
        subscriptionsReportViewModel =  viewModelInstance;
        try {
            Document document = new Document();

                    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Zopnote/SubscriptionReports/";
                    File file = new File(directory_path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Map<String, java.util.List<SubscriptionReportItem>> subscriptionListMap = populateSubscriptionListMap(subscriptionsReportViewModel.reportItems);

                    String fileName = getSubscriptionFileName(new ArrayList<>(subscriptionListMap.keySet()), selectedItems);
                    String targetPdf = directory_path + fileName + ".pdf";
                    PdfWriter.getInstance(document, new FileOutputStream(targetPdf));
                    document.open();

                    addMetaData(document);

                    //Logo Image add
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image logoImg = null;
                    byte[] byteArray = stream.toByteArray();
                    try {
                        logoImg = Image.getInstance(byteArray);

                        logoImg.setAlignment(Image.LEFT);
                        logoImg.scaleAbsoluteHeight(20);
                        logoImg.scaleAbsoluteWidth(20);
                        logoImg.scalePercent(20);

                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


            Bitmap bmNameLogo = BitmapFactory.decodeResource(context.getResources(), R.drawable.zopnote_logo);
            ByteArrayOutputStream streamNL = new ByteArrayOutputStream();
            bmNameLogo.compress(Bitmap.CompressFormat.PNG, 100, streamNL);
            Image nameLogoImg = null;
            byte[] byteArrayNl = streamNL.toByteArray();
            try {
                nameLogoImg = Image.getInstance(byteArrayNl);

                nameLogoImg.setAlignment(Image.LEFT);
                nameLogoImg.scaleAbsoluteHeight(10);
                nameLogoImg.scaleAbsoluteWidth(10);
                nameLogoImg.scalePercent(10);

            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

                    addHeader(document,name,logoImg,nameLogoImg);

                    addTimeStamp(document);

                    subscriptionReport(subscriptionListMap,document,selectedItems);

                    addBottomContent(document);

                    document.close();
            subscriptionsReportViewModel.reportPdfUri.postValue(targetPdf);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTimeStamp(Document document)throws DocumentException {
        Paragraph prefaceTwo = new Paragraph();
        prefaceTwo.setAlignment(Element.ALIGN_LEFT);
        prefaceTwo.setFont(bold14);
        prefaceTwo.add("Subscription Report: " + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY,new Date()));
        document.add(prefaceTwo);
    }

    private static void addHeader(Document document, String name, Image logoImg, Image nameLogoImg) throws DocumentException {
        Paragraph preface = new Paragraph();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{30,60});
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        preface.setAlignment(Element.ALIGN_LEFT);
        table.addCell(getCellHeaderLogo(logoImg,nameLogoImg));
        table.addCell(getCell(name, PdfPCell.LEFT,bold20));

        preface.add(table);
        document.add(preface);

        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));
    }

    private static void addBottomContent(Document document) throws DocumentException {
        LineSeparator lineBottom = new LineSeparator();
        document.add(new Chunk(lineBottom));

        Paragraph prefaceThree = new Paragraph();
        prefaceThree.setAlignment(Element.ALIGN_CENTER);
        prefaceThree.setFont(bold16);
        prefaceThree.add("Powered by Zopnote: Subscription Report" );
        document.add(prefaceThree);
    }


    private static PdfPCell getCellHeaderLogo(Image logo,Image nameLogoImg) {
        PdfPCell cell = new PdfPCell();

        Paragraph p = new Paragraph();
        p.add(new Chunk(logo, 10, 0, true));
        cell.addElement(p);

        Paragraph p1 = new Paragraph();
        p1.add(new Chunk(nameLogoImg, 0, 0, true));
        cell.addElement(p1);

        cell.setPadding(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
    private static void subscriptionReport(Map<String, java.util.List<SubscriptionReportItem>> subscriptionListMap, Document document, java.util.List<String> selectedItems) throws DocumentException{

        Paragraph preface = new Paragraph();
        int numberofHouses = 0;
        for (String routes: selectedItems){
            java.util.List<SubscriptionReportItem> subscriptionReportItemList = subscriptionListMap.get(routes);
            addEmptyLine(preface, 1);




            Chunk chunk = new Chunk("Route: "+routes);
            chunk.setFont(bold16);
            Paragraph preface1 = new Paragraph(chunk);
            preface1.setAlignment(Paragraph.ALIGN_CENTER);
            preface.add(preface1);
            addEmptyLine(preface, 1);



                for (SubscriptionReportItem reportItem: subscriptionReportItemList){
                    if (reportItem.getSubscriptions()!=null && !reportItem.getSubscriptions().isEmpty()){

                        String address = reportItem.getDoorNumber();
                        if (reportItem.getAddressLine1() != null && reportItem.getAddressLine1().trim().length() > 0) {
                            String addressLine1 = getAddressLine1(reportItem.getAddressLine1()).trim();
                            if (!addressLine1.isEmpty()) {
                                address += "-" + addressLine1;
                            }
                        }
                        numberofHouses++;

                        preface.setAlignment(Element.ALIGN_LEFT);
                        preface.add(new Paragraph(address, normal16));
                        writeSubscription(reportItem.getSubscriptions(), preface);
                    }

                }
            preface.add(new Paragraph("Total Houses: "+ numberofHouses, bold16));
        }
        document.add(preface);


    }


    private static String getSubscriptionFileName(ArrayList<String> subscriptionItems, java.util.List<String> selectedItems) {
        String fileName = "";
        java.util.List<String> commonElements = new ArrayList<>(selectedItems);
        commonElements.retainAll(selectedItems);
        if(commonElements.size() > 0){
            if (commonElements.size() == 1){
                fileName = commonElements.get(0) +"_subscription_"+ FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM,new Date());
            } else if (subscriptionItems.size() == selectedItems.size()){
                fileName = "All_subscription_" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM,new Date());
            } else{
                fileName = "Multi_subscription_" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM,new Date());
            }
        }
        return fileName;
    }

    private static Map<String, java.util.List<SubscriptionReportItem>> populateSubscriptionListMap(java.util.List subscriptionItem) {

        Map<String, java.util.List<SubscriptionReportItem>> map = new HashMap<>();
        RouteHeader routerHeader = null;
        java.util.List<SubscriptionReportItem> reportItemList = null;
        for (Object item : subscriptionItem){

            if (item instanceof RouteHeader){
                if (routerHeader != null){
                    map.put(routerHeader.getName(), reportItemList);
                }
                routerHeader = (RouteHeader) item;
                reportItemList = new ArrayList();
            }
            if (item instanceof SubscriptionReportItem){
                reportItemList.add((SubscriptionReportItem) item);
            }
        }
        map.put(routerHeader.getName(), reportItemList);

        return map;
    }

    private static void writeSubscription(java.util.List<SubscriptionInfo> subscriptions, Paragraph preface) {
        String strSubcription = "";
        for (int i=0; i< subscriptions.size(); i++) {
            SubscriptionInfo subscriptionInfo = subscriptions.get(i);


            if(subscriptionInfo.getPauseList() != null && ! subscriptionInfo.getPauseList().isEmpty()){
                writePauseList(subscriptionInfo.getPauseList(),preface,subscriptionInfo.getProductName());

            }else if (!isSubscriptionEnd(subscriptions.get(i).getEndDate())){
                strSubcription +=  subscriptionInfo.getProductName();


                Chunk chunk = new Chunk(subscriptionInfo.getProductName());
                chunk.setFont(normal14);
                Paragraph para1 = new Paragraph(chunk);
                para1.setAlignment(Paragraph.ALIGN_LEFT);
                para1.setSpacingAfter(0);
                para1.setIndentationLeft(50);

                preface.add(new Paragraph(para1));

            }

        }

    }

    private static boolean isSubscriptionEnd(Date endDate) {
        if (endDate !=null){
           if (endDate.before(new Date())){
               return true;
           }
        }
        return false;
    }

    private static void writePauseList(java.util.List<Pause> pauseList, Paragraph preface, String product) {
        Chunk chunk = new Chunk(product);
        chunk.setFont(normal14);
        Paragraph para1 = new Paragraph(chunk);
        para1.setAlignment(Paragraph.ALIGN_LEFT);
        para1.setSpacingAfter(0);
        para1.setIndentationLeft(50);
        preface.add(para1);

        // for (int i=0; i< pauseList.size(); i++) {
        //required only first element
        Pause pause = pauseList.get(pauseList.size() - 1);

        if (pause.getPauseEndDate()==null||
                SubscriptionUtil.isDateInCurrentMonth(pause.getPauseEndDate()) ||
                SubscriptionUtil.isDateInCurrentMonth(pause.getPauseStartDate())){

        String pauseStartDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, pause.getPauseStartDate());

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(50);
        preface.setAlignment(Element.ALIGN_LEFT);
        table.addCell(getCell(" pause", PdfPCell.ALIGN_LEFT, italic11));
        table.addCell(getCell("start date " + pauseStartDate, PdfPCell.ALIGN_CENTER, italic11));


        if (pause.getPauseEndDate() != null) {
            String pauseEndDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMM, pause.getPauseEndDate());
            table.addCell(getCell("end date " + pauseEndDate, PdfPCell.ALIGN_RIGHT, italic11));
        } else {
            table.addCell(getCell("end date -", PdfPCell.ALIGN_RIGHT, italic11));
        }
        preface.add(table);

     }


       // }
    }


    private static PdfPCell getCell(String text, int alignment,Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text,font));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private static void addMetaData(Document document) {
       // document.addTitle("Onboard Report");
        document.addSubject("Merchant");
        document.addKeywords("Java, PDF, Zopnote");
       // document.addAuthor("Lars Vogel");
        document.addCreator("Zopnote");
    }




    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public static String getAddressLine1(String addressLine1) {
        StringBuilder addressLine1Builder = new StringBuilder();
        try {
            JSONObject addressLine1Object = new JSONObject(addressLine1);
            Iterator<?> keys = addressLine1Object.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                if ( addressLine1Object.get(key) instanceof String ) {
                    addressLine1Builder.append( addressLine1Object.get(key));
                }

                if(keys.hasNext()){
                    addressLine1Builder.append(" ");
                    // addressLine1Builder.append(context.getResources().getString(R.string.bullet_char));
                    addressLine1Builder.append(".");
                    addressLine1Builder.append(" ");
                }
            }
        } catch (JSONException e) {
            //for legacy data: if not JSONArray then it's a String
            addressLine1Builder.append(addressLine1);
        }
        return addressLine1Builder.toString();
    }
}