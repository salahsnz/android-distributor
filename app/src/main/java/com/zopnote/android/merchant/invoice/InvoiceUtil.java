package com.zopnote.android.merchant.invoice;


import com.zopnote.android.merchant.data.model.DraftInvoiceItem;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceItem;
import com.zopnote.android.merchant.util.FormatUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

public class InvoiceUtil {

    public static ArrayList<InvoiceItem> getSortedInvoiceItems(Invoice invoice) {

        InvoiceItem balanceItem = null;
        TreeSet<InvoiceItem> sortedItems = new TreeSet<InvoiceItem>();
        for (String invoiceKey : invoice.getInvoiceItems().keySet()) {
            InvoiceItem item = invoice.getInvoiceItems().get(invoiceKey);

            if (item.getItem().equals("Previous Balance"))
                balanceItem = item;
            else
                sortedItems.add(item);
        }

        ArrayList<InvoiceItem> itemList = new ArrayList<InvoiceItem>();
        itemList.addAll(sortedItems);

        if (balanceItem != null)
            itemList.add(balanceItem);

        Collections.sort(itemList, new Comparator<InvoiceItem>() {
            @Override
            public int compare(InvoiceItem o1, InvoiceItem o2) {
                Date date1 = new Date(o2.getDate());
                Date date2 = new Date(o1.getDate());
                return date1.compareTo(date2);
            }
        });

        return itemList;
    }

    public static Invoice getLatestInvoice(List<Invoice> invoices) {
        Collections.sort(invoices, new Comparator<Invoice>() {
            @Override
            public int compare(Invoice o1, Invoice o2) {
                return o2.getInvoiceDate().compareTo(o1.getInvoiceDate());
            }
        });
        Invoice latestInvoice = invoices.get(0);
        return latestInvoice;
    }

    public static Invoice getSelectedInvoice(List<Invoice> invoices, String inVoiceId) {
        Invoice selectedInvoice = null;
        for (Invoice invoice: invoices){

            if (invoice.getId().equalsIgnoreCase(inVoiceId)){
                selectedInvoice = invoice;
            }
        }
        return selectedInvoice;
    }


    public static ArrayList<DraftInvoiceItem> getSortedInvoiceItems(List<DraftInvoiceItem> draftInvoiceItems) {
        DraftInvoiceItem balanceItem = null;
        TreeSet<DraftInvoiceItem> sortedItems = new TreeSet<>();

        for (DraftInvoiceItem item: draftInvoiceItems) {

            if (item.getItem().equals("Previous Balance"))
                balanceItem = item;
            else
                sortedItems.add(item);
        }

        ArrayList<DraftInvoiceItem> itemList = new ArrayList<>();
        itemList.addAll(sortedItems);

        if (balanceItem != null)
            itemList.add(balanceItem);

        return itemList;
    }
}
