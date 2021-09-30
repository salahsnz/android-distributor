package com.zopnote.android.merchant.indent;

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
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DailySubscription;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.managesubscription.SubscriptionUtil;
import com.zopnote.android.merchant.reports.subscription.RouteHeader;
import com.zopnote.android.merchant.reports.subscription.SubscriptionReportItem;
import com.zopnote.android.merchant.util.FormatUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zopnote.android.merchant.util.PdfFonts.bold10;
import static com.zopnote.android.merchant.util.PdfFonts.bold14;
import static com.zopnote.android.merchant.util.PdfFonts.bold16;
import static com.zopnote.android.merchant.util.PdfFonts.bold20;
import static com.zopnote.android.merchant.util.PdfFonts.italic11;
import static com.zopnote.android.merchant.util.PdfFonts.normal14;
import static com.zopnote.android.merchant.util.PdfFonts.normal16;


public class IndentPDF {



    private static  Context context;
    private static IndentViewModel viewModel;

    public static void
    build(Context context, java.util.List<String> selectedItems, IndentViewModel viewModelInstance) {
        context = context;
        viewModel =  viewModelInstance;
        try {
            Document document = new Document();

                    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Zopnote/Indent/";
                    File file = new File(directory_path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Map<String,java.util.List<DailySubscription>> dailySubscriptionListMap = populateDailySubscriptionListMap(selectedItems);

                    String fileName = getDailySubscriptionFileName(new ArrayList<>(dailySubscriptionListMap.keySet()), selectedItems,viewModel.purchaseCalender.getTime());
                    String targetPdf = directory_path + fileName + ".pdf";
                    PdfWriter.getInstance(document, new FileOutputStream(targetPdf));
                    document.open();

                    addMetaData(document);

                    //Logo Image add
                    Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.zopnote_logo);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image img = null;
                    byte[] byteArray = stream.toByteArray();
                    try {
                        img = Image.getInstance(byteArray);

                        img.setAlignment(Image.LEFT);
                        img.scaleAbsoluteHeight(20);
                        img.scaleAbsoluteWidth(20);
                        img.scalePercent(20);

                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Paragraph preface = new Paragraph();
                    preface.add(img);
                    document.add(preface);

                    addAgencyName(document,viewModel.merchant.getValue().getName());
                    addTimeStamp(document);

                    dailySubscriptionReport(dailySubscriptionListMap,document,selectedItems);

                    addBottomContent(document);

            viewModel.reportPdfUri.postValue(targetPdf);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTimeStamp(Document document)throws DocumentException {
        Paragraph prefaceTwo = new Paragraph();
        prefaceTwo.setAlignment(Element.ALIGN_LEFT);
        prefaceTwo.setFont(bold14);
        prefaceTwo.add("Indent Report : " + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, viewModel.purchaseCalender.getTime()));
        document.add(prefaceTwo);
    }

    private static void addAgencyName(Document document,String name) throws DocumentException {
        //add agency
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.setFont(bold20);
        preface.add(name);

        document.add(preface);
        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));
    }

    private static void addBottomContent(Document document) throws DocumentException {
        LineSeparator lineBottom = new LineSeparator();
        document.add(new Chunk(lineBottom));

        Paragraph prefaceThree = new Paragraph();
        prefaceThree.setAlignment(Element.ALIGN_CENTER);
        prefaceThree.setFont(bold14);
        prefaceThree.add("Zopnote : Indent" );
        document.add(prefaceThree);
    }



    private static void dailySubscriptionReport(Map<String, java.util.List<DailySubscription>> dailySubscriptionListMap, Document document, java.util.List<String> selectedItems) throws DocumentException{

        Paragraph preface = new Paragraph();
        boolean titleForTable = true;
        for (String routes: selectedItems){

            addEmptyLine(preface, 1);



            Chunk chunk = new Chunk(routes);
            chunk.setFont(bold16);
            Paragraph preface1 = new Paragraph(chunk);
            preface1.setAlignment(Paragraph.ALIGN_CENTER);
            preface.add(preface1);
            addEmptyLine(preface, 1);

            java.util.List<DailySubscription> intendList = dailySubscriptionListMap.get(routes);





            for (Object obj : intendList){

                if (obj instanceof RouteHeader) {
                    RouteHeader header = (RouteHeader) obj;
                    Chunk chunk2 = new Chunk(header.getName());
                    chunk2.setFont(bold14);
                    Paragraph para1 = new Paragraph(chunk2);
                    para1.setAlignment(Paragraph.ALIGN_LEFT);
                    para1.setSpacingAfter(0);
                    para1.setIndentationLeft(100);
                    preface.add(para1);
                    addEmptyLine(preface, 1);
                }else {
                    if (titleForTable) {
                        addTitleForTable(preface);
                        titleForTable = false;
                    }
                    DailySubscription content = (DailySubscription) obj;
                    PdfPTable table = new PdfPTable(3);
                    table.setWidthPercentage(100);
                    preface.setAlignment(Element.ALIGN_LEFT);
                    table.addCell(getCell(content.getName(), PdfPCell.ALIGN_LEFT, normal14));
                    if(viewModel.indentType.equalsIgnoreCase("changes")) {
                        table.addCell(getCell(String.valueOf(content.getPauseCount()), PdfPCell.ALIGN_CENTER, normal14));
                    }else {
                        table.addCell(getCell("", PdfPCell.ALIGN_CENTER, normal14));
                    }


                    table.addCell(getCell(String.valueOf(content.getProcureCount()), PdfPCell.ALIGN_RIGHT, normal14));

                    preface.add(table);
                }



                }




        }
        document.add(preface);


    }

    private static void addTitleForTable(Paragraph preface) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        preface.setAlignment(Element.ALIGN_LEFT);
            table.addCell(getCell("Item Name", PdfPCell.ALIGN_LEFT, bold10));
            table.addCell(getCell("Paused", PdfPCell.ALIGN_CENTER, bold10));
            table.addCell(getCell("Total", PdfPCell.ALIGN_RIGHT, bold10));

        preface.add(table);
        addEmptyLine(preface, 1);
    }

    private static String getDailySubscriptionFileName(ArrayList<String> dailySubscriptionItems, java.util.List<String> selectedItems,Date time) {
        String fileName = "";
        java.util.List<String> commonElements = new ArrayList<>(selectedItems);
        commonElements.retainAll(selectedItems);
        if(commonElements.size() > 0){
            if (commonElements.size() == 1){
                fileName = commonElements.get(0) +"_dailySubscription_"+ FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, time);
            } else if (dailySubscriptionItems.size() == selectedItems.size()){
                fileName = "All_dailySubscription_" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, time);
            } else{
                fileName = "Multi_dailySubscription_" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMMY, time);
            }
        }
        return fileName;
    }

    private static Map<String, java.util.List<DailySubscription>> populateDailySubscriptionListMap(java.util.List<String> selectedItems) {
        Map<String, java.util.List<DailySubscription>> map = new HashMap<>();


        for (String item : selectedItems){
            java.util.List<DailySubscription> indent = getIndentForRoute(item);
            map.put(item, indent);
        }

        return map;
    }


    private static java.util.List<DailySubscription> getIndentForRoute(String route) {
        java.util.List indentList = new ArrayList();

        if( ! viewModel.indentReport.isEmpty()){

            java.util.List summaryAtTopList = new ArrayList();
            java.util.List itemsList = new ArrayList();

            String[] previousAddressLine2Header = new String[1];

            for (DailySubscription dailySubscription: viewModel.indentReport) {

                if(dailySubscription.getRoute().equalsIgnoreCase(route)){
                    filterListForIndentTypea(dailySubscription, previousAddressLine2Header, itemsList, summaryAtTopList);
                }
            }
            indentList.addAll(summaryAtTopList);
            indentList.addAll(itemsList);
        }
        return indentList;
    }

    private static void filterListForIndentTypea(DailySubscription dailySubscription, String[] previousAddressLine2Header, java.util.List itemsList, java.util.List summaryAtTopList) {
        if(viewModel.indentType.equalsIgnoreCase("changes")) {
            //indent type "changes" -> paused
            if(dailySubscription.getPauseCount() > 0){
                if(dailySubscription.getAddressLine2().equalsIgnoreCase("All")){
                    addToList(dailySubscription, previousAddressLine2Header, summaryAtTopList);
                }else{
                    addToList(dailySubscription, previousAddressLine2Header, itemsList);
                }
            }
        }else{
            //indent type "all"
            if(dailySubscription.getAddressLine2().equalsIgnoreCase("All")){
                addToList(dailySubscription, previousAddressLine2Header, summaryAtTopList);
            }else{
                addToList(dailySubscription, previousAddressLine2Header, itemsList);
            }
        }
    }

    private static void addToList(DailySubscription dailySubscription, String[] previousAddressLine2Header, java.util.List itemsList) {

        String currentAddressLine2 = dailySubscription.getAddressLine2();
        if( ! currentAddressLine2.equalsIgnoreCase(previousAddressLine2Header[0])){
            addHeader(currentAddressLine2, itemsList);
            previousAddressLine2Header[0] = currentAddressLine2;
        }

        itemsList.add(dailySubscription);
    }

    private static void addHeader(String currentAddressLine2, java.util.List summaryList) {
        RouteHeader routeHeader = new RouteHeader();
        routeHeader.setName(currentAddressLine2);
        summaryList.add(routeHeader);
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
        //zopnote_logo
    }



    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


}