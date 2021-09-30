package com.zopnote.android.merchant.reports.draftinvoice;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.DraftInvoice;
import com.zopnote.android.merchant.data.model.DraftInvoiceItem;
import com.zopnote.android.merchant.data.model.DraftInvoiceReportItem;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DraftInvoiceReportViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = "InvoicePreviewReport";

    private Repository repository;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();
    public MutableLiveData<Boolean> monthInfoChanged = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public LiveData<Merchant> merchant;
    public String merchantId;
    public List<DraftInvoiceReportItem> draftInvoiceReport;

    public int month;
    public String monthString;
    public int year;

    public MutableLiveData<Boolean> invoiceTypeChanged = new MutableLiveData<>();
    public String invoiceType;

    public DraftInvoiceReportViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;

    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
        draftInvoiceReport = new ArrayList();
    }

    public LiveData<List<Customer>> getCustomers(String route){
        return repository.getCustomers(route);
    }

    public void getDraftInvoicesReport() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_DRAFT_INVOICE_REPORT);

        authenticator.addParameter(Param.MERCHANT_ID, merchantId);

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());

                        apiCallRunning.postValue(false);

                        saveDraftInvoicesReport(response);

                        apiCallSuccess.postValue(true);
                        apiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                networkError.postValue(false);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);
    }

    private void saveDraftInvoicesReport(JSONObject response) {
        try {

            JSONObject dateInfoObject = response.getJSONObject("dateInfo");
            saveDateInfo(dateInfoObject);

            draftInvoiceReport.clear();
            JSONArray invoicesArray = response.getJSONArray("invoices");
            for (int i = 0; i < invoicesArray.length(); i++) {
                JSONObject reportItemObject = (JSONObject) invoicesArray.get(i);

                DraftInvoiceReportItem reportItem = new DraftInvoiceReportItem();
                reportItem.setCustomerId(reportItemObject.getString("id"));
                reportItem.setDoorNumber(reportItemObject.getString("doorNumber"));
                reportItem.setRoute(reportItemObject.getString("route"));

                if( ! reportItemObject.isNull("addressLine1")){
                    reportItem.setAddressLine1(reportItemObject.getString("addressLine1"));
                }

                if( ! reportItemObject.isNull("addressLine2")){
                    reportItem.setAddressLine2(reportItemObject.getString("addressLine2"));
                }

                if( ! reportItemObject.isNull("invoice")){
                    JSONObject invoiceObject = reportItemObject.getJSONObject("invoice");
                    reportItem.setInvoice(getDraftInvoice(invoiceObject));
                }

                if( ! reportItemObject.isNull("notes")){
                    reportItem.setNotes(reportItemObject.getString("notes"));
                }

                draftInvoiceReport.add(reportItem);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void saveDateInfo(JSONObject dateInfoObject) throws JSONException {
        int monthInt = dateInfoObject.getInt("month");
        int yearInt = dateInfoObject.getInt("year");

        Calendar calendar = FormatUtil.getLocalCalender();
        calendar.set(Calendar.MONTH, monthInt);
        calendar.set(Calendar.YEAR, yearInt);

        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        monthString = FormatUtil.DATE_FORMAT_MMMM.format(calendar.getTime());

        monthInfoChanged.postValue(true);
    }

    private DraftInvoice getDraftInvoice(JSONObject invoiceObject) {
        DraftInvoice draftInvoice = new DraftInvoice();

        try{
            draftInvoice.setId(invoiceObject.getString("id"));

            if( ! invoiceObject.isNull("invoiceItems")){
                List<DraftInvoiceItem> draftInvoiceItems = getDraftInvoiceItems(invoiceObject.getJSONArray("invoiceItems"));
                draftInvoice.setInvoiceItems(draftInvoiceItems);
            }

            draftInvoice.setInvoicePeriod(invoiceObject.getString("invoicePeriod"));

            draftInvoice.setInvoiceDate(new Date(invoiceObject.getLong("invoiceDate")));

            String status = invoiceObject.getString("status");
            draftInvoice.setStatus(InvoiceStatusEnum.valueOf(status));

            draftInvoice.setInvoiceAmount(invoiceObject.getDouble("invoiceAmount"));

            draftInvoice.setInvoiceNumber(invoiceObject.getString("invoiceNumber"));

            draftInvoice.setDueDate(new Date(invoiceObject.getLong("dueDate")));

        }catch (JSONException e){
            e.printStackTrace();
        }
        return draftInvoice;
    }

    private List<DraftInvoiceItem> getDraftInvoiceItems(JSONArray draftInvoiceItems) {
        List<DraftInvoiceItem> draftInvoiceItemsList = new ArrayList<>();

        for (int i=0; i< draftInvoiceItems.length(); i++){
            try{
                DraftInvoiceItem draftInvoiceItem = new DraftInvoiceItem();

                JSONObject invoiceItemObject = draftInvoiceItems.getJSONObject(i);

                draftInvoiceItem.setAmount(invoiceItemObject.getDouble("amount"));
                draftInvoiceItem.setItem(invoiceItemObject.getString("item"));
                draftInvoiceItem.setAutoGenerated(invoiceItemObject.getBoolean("autoGenerated"));

                draftInvoiceItemsList.add(draftInvoiceItem);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return draftInvoiceItemsList;
    }
}
