package com.zopnote.android.merchant.reports.collection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Collection;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CollectionViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = "CollectionViewModel";

    private Repository repository;

    public LiveData<Merchant> merchant;
    public String merchantId;
    public List<Collection> collectionReport;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();
    public MutableLiveData<Boolean> dateChanged
            = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public MutableLiveData<Boolean> filterTypeChanged = new MutableLiveData<>();
    public PaymentFilterOption filterType;

    public int month;
    public int dayFrom;
    public int dayTo;
    public String monthString;
    public int year;

    public CollectionViewModel(@NonNull Application context, Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init(int selectedMonth, int selectedYear, PaymentFilterOption type){
        if(merchant != null){
            return;
        }

        merchant = repository.getMerchant();
        collectionReport = new ArrayList();

        apiCallRunning.setValue(false);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);

        month = selectedMonth;
        year = selectedYear;
        dayFrom = 1;
        dayTo = 31;
        monthString = getMonthString();
        filterType = type;
    }

    private String getMonthString() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return FormatUtil.DATE_FORMAT_MMMM.format(calendar.getTime());
    }

    public void getCollectionReport() {
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/getInvoiceReport"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_INVOICE_REPORT);
        //merchantId="b93dc1ed-870f-46f1-96c4-eccca021d13f";
        authenticator.addParameter(Param.MERCHANT_ID, merchantId);
        authenticator.addParameter(Param.MONTH, String.valueOf(month));
        authenticator.addParameter(Param.YEAR, String.valueOf(year));
        System.out.println("API : "+authenticator.getUri());


        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        System.out.println("response : " + response);
                        if (true) Log.d("CSD", "CVM response: " + response.toString());

                        apiCallRunning.postValue(false);

                        saveCollectionReport(response);

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

    private void saveCollectionReport(JSONArray response) {
        try {
            collectionReport.clear();

            for (int j = 0; j < response.length(); j++) {
                JSONArray custArry = response.getJSONArray(j);
                for (int i = 0; i < custArry.length(); i++) {
                    JSONObject reportItemObject = (JSONObject) custArry.get(i);

                    Collection collection = new Collection();

                    collection.setId(reportItemObject.getString("id"));
                    collection.setDoorNumber(reportItemObject.getString("doorNumber"));

                    if (!reportItemObject.isNull("addressLine1")) {
                        collection.setAddressLine1(reportItemObject.getString("addressLine1"));
                    }
                    
                    collection.setAddressLine2(reportItemObject.getString("addressLine2"));
                    collection.setRoute(reportItemObject.getString("route"));
                    collection.setInvoiceAmount(reportItemObject.getDouble("invoiceAmount"));
                    if (reportItemObject.has("invoiceDate") && !reportItemObject.isNull("invoiceDate"))
                        collection.setInvoiceDate(new Date(reportItemObject.getLong("invoiceDate")));
                    if (reportItemObject.has("paymentDate") && !reportItemObject.isNull("paymentDate"))
                        collection.setInvoicePaidDate(new Date(reportItemObject.getLong("paymentDate")));
                    collection.setStatus(reportItemObject.getString("status"));

                    collectionReport.add(collection);
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
