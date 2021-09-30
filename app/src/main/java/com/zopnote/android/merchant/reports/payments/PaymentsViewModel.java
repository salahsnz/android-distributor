package com.zopnote.android.merchant.reports.payments;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
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
import java.util.List;
import java.util.Map;

public class PaymentsViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private String LOG_TAG = PaymentsViewModel.class.getSimpleName();

    private Repository repository;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;
    public List<ReportItem> reportItems;

    public LiveData<Merchant> merchant;
    public String merchantId;
    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();
    public int month;
    public String monthString;
    public int year;

    public PaymentsViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(){
        if(merchant != null){
            return;
        }
        reportItems = new ArrayList<>();
        merchant = repository.getMerchant();

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH); //Jan month is 0
        year = calendar.get(Calendar.YEAR);
        monthString = FormatUtil.DATE_FORMAT_MMMM.format(calendar.getTime());
    }

    public void getPayments(int month, int year) {
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/getCollectionReport"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_COLLECTION_REPORT);
        //merchantId="b93dc1ed-870f-46f1-96c4-eccca021d13f";
        authenticator.addParameter(Param.MERCHANT_ID, merchantId);
        authenticator.addParameter(Param.MONTH, String.valueOf(month));
        authenticator.addParameter(Param.YEAR, String.valueOf(year));
       // authenticator.addParameter("key", "76380346");

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        System.out.println("response"+ response.toString());
                        if (true) Log.d("CSD", "response: " + response.toString());

                        apiCallRunning.postValue(false);

                        savePaymentsReport(response);

                        apiCallSuccess.postValue(true);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;*/
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
    }

    private void savePaymentsReport(JSONObject response) {
        try {
            reportItems.clear();

            JSONArray itemsArray = response.getJSONArray("reportItems");

            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject reportItemObject = (JSONObject) itemsArray.get(i);

                ReportItem reportItem = new ReportItem();
                reportItem.setRoute(reportItemObject.getString("route"));

                reportItem.setTotalAmountBilled(reportItemObject.getDouble("totalAmountBilled"));
                reportItem.setTotalAmountPaidOnline(reportItemObject.getDouble("totalAmountPaidOnline"));
                reportItem.setTotalAmountPaidCash(reportItemObject.getDouble("totalAmountPaidCash"));
                reportItem.setTotalAmountPaidCheque(reportItemObject.getDouble("totalAmountPaidCheque"));
                reportItem.setTotalAmountPaidGPay(reportItemObject.getDouble("totalAmountPaidGpay"));
                reportItem.setTotalAmountPaidPaytm(reportItemObject.getDouble("totalAmountPaidPaytm"));
                reportItem.setTotalAmountPaidPhonepe(reportItemObject.getDouble("totalAmountPaidPhonePe"));
                reportItem.setTotalAmountPaidUPI(reportItemObject.getDouble("totalAmountPaidUpi"));
                reportItem.setTotalAmountPaidOther(reportItemObject.getDouble("totalAmountPaidother"));
                reportItem.setTotalAmountUnpaid(reportItemObject.getDouble("totalAmountUnpaid"));

                reportItem.setTotalNumberBilled(reportItemObject.getInt("totalNumberBilled"));
                reportItem.setTotalNumberPaidOnline(reportItemObject.getInt("totalNumberPaidOnline"));
                reportItem.setTotalNumberPaidCash(reportItemObject.getInt("totalNumberPaidCash"));
                reportItem.setTotalNumberPaidCheque(reportItemObject.getInt("totalNumberPaidCheque"));
                reportItem.setTotalNumberPaidGPay(reportItemObject.getInt("totalNumberPaidGpay"));
                reportItem.setTotalNumberPaidPaytm(reportItemObject.getInt("totalNumberPaidPaytm"));
                reportItem.setTotalNumberPaidPhonepe(reportItemObject.getInt("totalNumberPaidPhonepe"));
                reportItem.setTotalNumberPaidUPI(reportItemObject.getInt("totalNumberPaidUpi"));
                reportItem.setTotalNumberPaidOther(reportItemObject.getInt("totalNumberPaidOther"));
                reportItem.setTotalNumberUnpaid(reportItemObject.getInt("totalNumberUnpaid"));

                reportItems.add(reportItem);
            }

            addSummary();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addSummary() {

        if(reportItems.isEmpty()){
            return;
        }

        ReportItem summaryItem = new ReportItem();
        summaryItem.setRoute("All");
        summaryItem.setTotalAmountBilled(0d);
        summaryItem.setTotalAmountPaidOnline(0d);
        summaryItem.setTotalAmountPaidCash(0d);
        summaryItem.setTotalAmountPaidCheque(0d);
        summaryItem.setTotalAmountPaidGPay(0d);
        summaryItem.setTotalAmountPaidPaytm(0d);
        summaryItem.setTotalAmountPaidPhonepe(0d);
        summaryItem.setTotalAmountPaidUPI(0d);
        summaryItem.setTotalAmountPaidOther(0d);
        summaryItem.setTotalAmountUnpaid(0d);

        summaryItem.setTotalNumberBilled(0);
        summaryItem.setTotalNumberPaidOnline(0);
        summaryItem.setTotalNumberPaidCash(0);
        summaryItem.setTotalNumberPaidCheque(0);
        summaryItem.setTotalNumberPaidGPay(0);
        summaryItem.setTotalNumberPaidPaytm(0);
        summaryItem.setTotalNumberPaidPhonepe(0);
        summaryItem.setTotalNumberPaidUPI(0);
        summaryItem.setTotalNumberPaidOther(0);
        summaryItem.setTotalNumberUnpaid(0);

        for (ReportItem reportItem: reportItems) {

            summaryItem.setTotalAmountBilled(summaryItem.getTotalAmountBilled() + reportItem.getTotalAmountBilled());
            summaryItem.setTotalAmountPaidOnline(summaryItem.getTotalAmountPaidOnline() + reportItem.getTotalAmountPaidOnline());
            summaryItem.setTotalAmountPaidCash(summaryItem.getTotalAmountPaidCash() + reportItem.getTotalAmountPaidCash());
            summaryItem.setTotalAmountPaidCheque(summaryItem.getTotalAmountPaidCheque() + reportItem.getTotalAmountPaidCheque());
            summaryItem.setTotalAmountPaidGPay(summaryItem.getTotalAmountPaidGPay() + reportItem.getTotalAmountPaidGPay());
            summaryItem.setTotalAmountPaidPaytm(summaryItem.getTotalAmountPaidPaytm() + reportItem.getTotalAmountPaidPaytm());
            summaryItem.setTotalAmountPaidPhonepe(summaryItem.getTotalAmountPaidPhonepe() + reportItem.getTotalAmountPaidPhonepe());
            summaryItem.setTotalAmountPaidUPI(summaryItem.getTotalAmountPaidUPI() + reportItem.getTotalAmountPaidUPI());
            summaryItem.setTotalAmountPaidOther(summaryItem.getTotalAmountPaidOther() + reportItem.getTotalAmountPaidOther());
            summaryItem.setTotalAmountUnpaid(summaryItem.getTotalAmountUnpaid() + reportItem.getTotalAmountUnpaid());

            summaryItem.setTotalNumberBilled(summaryItem.getTotalNumberBilled() + reportItem.getTotalNumberBilled());
            summaryItem.setTotalNumberPaidOnline(summaryItem.getTotalNumberPaidOnline() + reportItem.getTotalNumberPaidOnline());
            summaryItem.setTotalNumberPaidCash(summaryItem.getTotalNumberPaidCash() + reportItem.getTotalNumberPaidCash());
            summaryItem.setTotalNumberPaidCheque(summaryItem.getTotalNumberPaidCheque() + reportItem.getTotalNumberPaidCheque());
            summaryItem.setTotalNumberPaidGPay(summaryItem.getTotalNumberPaidGPay() + reportItem.getTotalNumberPaidGPay());
            summaryItem.setTotalNumberPaidPaytm(summaryItem.getTotalNumberPaidPaytm() + reportItem.getTotalNumberPaidPaytm());
            summaryItem.setTotalNumberPaidPhonepe(summaryItem.getTotalNumberPaidPhonepe() + reportItem.getTotalNumberPaidPhonepe());
            summaryItem.setTotalNumberPaidUPI(summaryItem.getTotalNumberPaidUPI() + reportItem.getTotalNumberPaidUPI());
            summaryItem.setTotalNumberPaidOther(summaryItem.getTotalNumberPaidOther() + reportItem.getTotalNumberPaidOther());
            summaryItem.setTotalNumberUnpaid(summaryItem.getTotalNumberUnpaid() + reportItem.getTotalNumberUnpaid());
        }
        reportItems.add(summaryItem);

    }
}
