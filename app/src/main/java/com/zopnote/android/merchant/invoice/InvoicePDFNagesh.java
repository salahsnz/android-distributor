package com.zopnote.android.merchant.invoice;

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
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.DateWiseBills;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.NumberToWordUtils;
import com.zopnote.android.merchant.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zopnote.android.merchant.util.PdfFonts.bold10;
import static com.zopnote.android.merchant.util.PdfFonts.bold14;
import static com.zopnote.android.merchant.util.PdfFonts.bold16;
import static com.zopnote.android.merchant.util.PdfFonts.bold20;
import static com.zopnote.android.merchant.util.PdfFonts.normal14;
import static com.zopnote.android.merchant.util.PdfFonts.normal16;


public class InvoicePDFNagesh {




    private static  Context context;
    private static InvoiceViewModel invoiceViewModel;

    public static void
    build(Context context, String businessAddress, InvoiceViewModel viewModelInstance) {
        invoiceViewModel = viewModelInstance;

        try {
            Document document = new Document();

            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Zopnote/Distributor/Invoice/";
            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }

            String targetPdf = directory_path+getInvoiceFileName() + ".pdf";
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

            addHeader(document,invoiceViewModel.merchantName,logoImg,nameLogoImg,businessAddress);

            addTimeStampAndNo(document);

            addInvoiceItems(document);

            addBottomContent(document);

            document.close();
            invoiceViewModel.invoicePdfUri.postValue(targetPdf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTimeStampAndNo(Document document)throws DocumentException {
        Paragraph preface = new Paragraph();

        addEmptyLine(preface,2);
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        preface.setAlignment(Element.ALIGN_LEFT);
        table.addCell(getCell("Invoice Number: " +invoiceViewModel.latestInvoice.getInvoiceNumber(), PdfPCell.ALIGN_LEFT,bold16));
        table.addCell(getCell("Date: "+ FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY,new Date()), PdfPCell.ALIGN_RIGHT,bold16));
        preface.add(table);
        document.add(preface);
    }

    private static void addHeader(Document document, String name, Image logoImg,Image nameLogoImg,String businessAddress) throws DocumentException {
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


        if (!businessAddress.equalsIgnoreCase("")) {
            Paragraph address2 = new Paragraph();
            address2.setAlignment(Element.ALIGN_CENTER);
            address2.setFont(normal14);
            Chunk beginning = new Chunk(businessAddress, normal14);
            address2.add(beginning);
            document.add(address2);
        }

        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));
    }

    private static void addAgencyName(Document document,String name,String address) throws DocumentException {
        //add agency
        Paragraph preface = new Paragraph();
        preface.setAlignment(Element.ALIGN_CENTER);
        preface.setFont(bold20);
        preface.add(name);
        document.add(preface);

        //  prepareAddressLine1(address,document);



        LineSeparator ls = new LineSeparator();
        document.add(new Chunk(ls));
    }

    private static void addBottomContent(Document document) throws DocumentException {
        LineSeparator lineBottom = new LineSeparator();
        document.add(new Chunk(lineBottom));

        Paragraph prefaceThree = new Paragraph();
        prefaceThree.setAlignment(Element.ALIGN_CENTER);
        prefaceThree.setFont(bold16);
        prefaceThree.add("Powered by Zopnote: Invoice" );
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
    private static void addInvoiceItems(Document document) throws DocumentException{

        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph("To:",normal16));
        addEmptyLine(preface, 1);
        // preface.setIndentationLeft(50);
        preface.setFont(normal16);
        preface.add(invoiceViewModel.customerName);

        Paragraph preface1 = new Paragraph();
        preface1.setFont(normal16);
        preface1.add(invoiceViewModel.customerAddress);
        preface.add(preface1);

        addEmptyLine(preface, 1);

        Paragraph preface2 = new Paragraph();
        preface2.setFont(normal16);

        String[] month = invoiceViewModel.latestInvoice.getInvoicePeriod().split(" to", 2);
        String year = "";
        if (month[1].length() > 3) {
            year= month[1].substring(month[1].length() - 4);
        } else {
            // whatever is appropriate in this case
            throw new IllegalArgumentException("word has less than 3 characters!");
        }

        preface2.add("Month: "+month[0]+ "1 "+year.trim()+" (Billing Period : "+ invoiceViewModel.latestInvoice.getInvoicePeriod()+")" );

        preface.add(preface2);

        Date startDate = new Date(invoiceViewModel.startDate);
        System.out.println("Date startDate" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_YYY_MM_DD_T_HH_MM_SS_SSS_Z,startDate));
        Date endDate = new Date(invoiceViewModel.endDate);
        System.out.println("Date endDate" + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_YYY_MM_DD_T_HH_MM_SS_SSS_Z,endDate));

        if (!invoiceViewModel.selectedPeriod.equalsIgnoreCase("This Month")) {

            addEmptyLine(preface, 1);

            Paragraph prefaceCurrentBilling = new Paragraph();
            prefaceCurrentBilling.setFont(normal16);

            // String strNowDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM, new Date());
            String strStartDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D, startDate);
            String strEndDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D, endDate);

            prefaceCurrentBilling.add("Current billing from "+ strStartDate+ " To "+ strEndDate);
            preface.add(prefaceCurrentBilling);
        }

        if(invoiceViewModel.latestInvoice.getInvoiceItems() != null){
            addEmptyLine(preface,2);
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            preface.setAlignment(Element.ALIGN_CENTER);
            table.addCell(getCell("Newspaper",PdfPCell.ALIGN_LEFT,bold16));
            table.addCell(getCell("Amount(Rs)", PdfPCell.ALIGN_RIGHT,bold16));
            preface.add(table);
            addEmptyLine(preface,1);

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            preface.setAlignment(Element.ALIGN_CENTER);
            List<DateWiseBills> sortedInvoiceItems = invoiceViewModel.dailyIndentInvoices.getDatewiseBills();
            Paragraph paraPreviousBalance = new Paragraph();

            Double totalAmount = 0.0D;
            Double totalAdvanceAmount = 0.0D;
            ArrayList<DateWiseBills> advancePaidList = new ArrayList<>();
            for (DateWiseBills invoiceItem:sortedInvoiceItems) {
                Date dwbDate = new Date(invoiceItem.getIndentDate());
                System.out.println("Date " + FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_YYY_MM_DD_T_HH_MM_SS_SSS_Z, dwbDate));

                if (invoiceViewModel.selectedPeriod.equalsIgnoreCase("This Month")) {
                    if (invoiceItem.getAdvancePaid() != 0) {
                        DateWiseBills advance = new DateWiseBills();
                        advance.setAdvancePaid(invoiceItem.getAdvancePaid());
                        advance.setIndentDate(invoiceItem.getIndentDate());
                        advancePaidList.add(advance);
                    }
                    totalAmount = invoiceItem.getDailyTotal() + totalAmount;
                    totalAdvanceAmount = invoiceItem.getAdvancePaid() + totalAdvanceAmount;

                    String itemTitle = "";
                    itemTitle = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D, dwbDate) + " " + subscriptionTxt(invoiceItem);

                    table2.addCell(getCell(itemTitle, PdfPCell.ALIGN_LEFT, normal16));
                    table2.addCell(getCell(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getDailyTotal()), PdfPCell.ALIGN_RIGHT, normal16));
                } else {

                    if (dwbDate.after(startDate) && dwbDate.before(endDate)) {

                        if (invoiceItem.getAdvancePaid() != 0) {
                            DateWiseBills advance = new DateWiseBills();
                            advance.setAdvancePaid(invoiceItem.getAdvancePaid());
                            advance.setIndentDate(invoiceItem.getIndentDate());
                            advancePaidList.add(advance);
                        }
                        totalAmount = invoiceItem.getDailyTotal() + totalAmount;
                        totalAdvanceAmount = invoiceItem.getAdvancePaid() + totalAdvanceAmount;

                        String itemTitle = "";
                        itemTitle = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D, dwbDate) + " " + subscriptionTxt(invoiceItem);

                        table2.addCell(getCell(itemTitle, PdfPCell.ALIGN_LEFT, normal16));
                        table2.addCell(getCell(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getDailyTotal()), PdfPCell.ALIGN_RIGHT, normal16));

                    } else if (dwbDate.equals(startDate) || dwbDate.equals(endDate)) {

                        if (invoiceItem.getAdvancePaid() != 0) {
                            DateWiseBills advance = new DateWiseBills();
                            advance.setAdvancePaid(invoiceItem.getAdvancePaid());
                            advance.setIndentDate(invoiceItem.getIndentDate());
                            advancePaidList.add(advance);
                        }

                        totalAmount = invoiceItem.getDailyTotal() + totalAmount;
                        totalAdvanceAmount = invoiceItem.getAdvancePaid() + totalAdvanceAmount;

                        String itemTitle = "";
                        itemTitle = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D, dwbDate) + " " + subscriptionTxt(invoiceItem);

                        table2.addCell(getCell(itemTitle, PdfPCell.ALIGN_LEFT, normal16));
                        table2.addCell(getCell(FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceItem.getDailyTotal()), PdfPCell.ALIGN_RIGHT, normal16));
                    }


                }
            }
            for (DateWiseBills advance :advancePaidList) {
                table2.addCell(getCell(FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMM_D,new Date(advance.getIndentDate()))+" ADVANCE PAID", PdfPCell.ALIGN_LEFT, normal16));
                table2.addCell(getCell("-"+FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(advance.getAdvancePaid()), PdfPCell.ALIGN_RIGHT, normal16));
            }
            preface.add(table2);
            preface.add(paraPreviousBalance);
            addEmptyLine(preface,1);


            if (invoiceViewModel.previousMonthUnpaid!=0) {
                Paragraph paragraphPrvMonth = new Paragraph();
                paragraphPrvMonth.setAlignment(Element.ALIGN_RIGHT);
                paragraphPrvMonth.setFont(normal16);
                paragraphPrvMonth.add("Previous month unpaid: " + FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceViewModel.previousMonthUnpaid));
                preface.add(paragraphPrvMonth);
                addEmptyLine(preface, 1);
            }

            if (invoiceViewModel.previousBalanceUnpaid!=0) {
                Paragraph paragraphTotalAdvPb = new Paragraph();
                paragraphTotalAdvPb.setAlignment(Element.ALIGN_RIGHT);
                paragraphTotalAdvPb.setFont(normal16);

                String titleAdvPb;
                if (invoiceViewModel.previousBalanceUnpaid>0)
                    titleAdvPb = "Previous balance unpaid: ";
                else
                    titleAdvPb = "Advance paid: ";
                paragraphTotalAdvPb.add(titleAdvPb + FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(invoiceViewModel.previousBalanceUnpaid));
                preface.add(paragraphTotalAdvPb);
                addEmptyLine(preface, 1);
            }




            Paragraph paragraphTotal = new Paragraph();
            paragraphTotal.setAlignment(Element.ALIGN_RIGHT);
            paragraphTotal.setFont(bold16);

            Double totalDue =  invoiceViewModel.previousMonthUnpaid +totalAmount+invoiceViewModel.previousBalanceUnpaid-totalAdvanceAmount;

            paragraphTotal.add("Total: "+ FormatUtil.AMOUNT_FORMAT_WITH_ZERO_DECIMALS.format(totalDue));
            preface.add(paragraphTotal);
            addEmptyLine(preface,1);

            Paragraph paragraphTotaInWords = new Paragraph();
            paragraphTotaInWords.setAlignment(Element.ALIGN_RIGHT);
            paragraphTotaInWords.setFont(bold16);
            paragraphTotaInWords.add("("+NumberToWordUtils.convertNumberToWords(totalDue.intValue())+" Rupees Only)");
            preface.add(paragraphTotaInWords);


            addEmptyLine(preface,2);
        }


        // String[] monthOnly = invoiceViewModel.latestInvoice.getInvoicePeriod().split("1 to", 2);
        String txt = "";
        if (invoiceViewModel.latestInvoice.getStatus().equals(InvoiceStatusEnum.PAID))
            txt = "Thank you for the payment";
        else
            txt = "Please pay the amount by 6th " +FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_MMMM, new Date()) +". Thank you";

        preface.add(new Paragraph(txt,bold16));
        document.add(preface);
           /* Chunk chunk = new Chunk("Route : "+routes);
            chunk.setFont(normal14);
            Paragraph preface1 = new Paragraph(chunk);
            preface1.setAlignment(Paragraph.ALIGN_CENTER);
            preface.add(preface1);
            addEmptyLine(preface, 1);*/




    }

    private static String subscriptionTxt(DateWiseBills invoiceItem) {
        StringBuilder subscriptionTxt = new StringBuilder();

        for (DailyIndentSubscription dis : invoiceItem.getIndents()) {
            if (dis.getSubscriptionQuantity() != -1)
                subscriptionTxt.append(dis.getProductShortCode()).append("(").append(dis.getSubscriptionQuantity()).append(")").append(" ");
        }
        return subscriptionTxt.toString();
    }

    private static String getInvoiceFileName() {
        String address = invoiceViewModel.customerAddress.replace("/","-");
        return "Invoice_" + address+"_"+FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_D_MMM,new Date());
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


    private static void prepareAddressLine1(String addressFieldsConfig,Document document) {
        try {
            JSONObject address1Fields = new JSONObject(addressFieldsConfig);
            JSONArray address1FieldsArray = address1Fields.getJSONArray("address1Fields");
            for (int i = 0; i< address1FieldsArray.length(); i++){
                JSONObject addressField  = (JSONObject) address1FieldsArray.get(i);

                String name = addressField.getString("name");
                String label = addressField.getString("label");

                List<String> values = new ArrayList<>();
                JSONArray valuesArray = addressField.getJSONArray("values");
                for (int j=0; j<valuesArray.length(); j++){
                    values.add(valuesArray.getString(j));
                }

                /*if (values.size() > 0) {
                    values.add(NOTHING_SELECTED);
                }*/

                try {
                    addAddressLine( document,values);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void addAddressLine(Document document, List<String> values) throws DocumentException{
        String address ="";
        for (String items : values){
            address += ","+items;
        }
        Paragraph preface1 = new Paragraph();
        preface1.setAlignment(Element.ALIGN_CENTER);
        preface1.setFont(normal14);
        preface1.add(address);
        document.add(preface1);
    }
}