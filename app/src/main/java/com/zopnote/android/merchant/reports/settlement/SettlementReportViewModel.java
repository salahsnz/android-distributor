package com.zopnote.android.merchant.reports.settlement;

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
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.SettlementInfo;
import com.zopnote.android.merchant.data.model.SettlementReport;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_ADD_MERCHANT_ADVANCE;
import static com.zopnote.android.merchant.AppConstants.ENDPOINT_INVOICE_PAYOUT;

public class SettlementReportViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    public String advanceAmount;
    private String LOG_TAG = SettlementReportViewModel.class.getSimpleName();

    private final Repository repository;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunningReqAdv = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccessReqAdv = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallErrorReqAdv = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunningSettleNow = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccessSettleNow = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallErrorSettleNow = new MutableLiveData<>();


    public String apiCallErrorMessage;
    public String settleNowMessage;

    public String merchantId;
    public LiveData<Merchant> merchant;
    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();
    public int month;
    public String monthString;
    public int year;
    public SettlementReport settlementReport;

    public SettlementReportViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(){
        if(merchant != null){
            return;
        }
        merchant = repository.getMerchant();

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH); //Jan month is 0
        year = calendar.get(Calendar.YEAR);
        monthString = FormatUtil.DATE_FORMAT_MMMM.format(calendar.getTime());
    }

    public void getSettlementReport() {
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/getSettlementReport"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SETTLEMENT_REPORT);
        //merchantId="4142f15c-d6af-4035-ab17-6b4e83509ee3"; //Nandeesh
        //merchantId="f45febe1-9cc9-466f-aedc-5e328b32f478"; //Ravi Patil
        authenticator.addParameter(Param.MERCHANT_ID, merchantId);
        authenticator.addParameter(Param.MONTH, String.valueOf(month));
        authenticator.addParameter(Param.YEAR, String.valueOf(year));
        System.out.println("API : "+authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        /*if(response==null)
                            Log.d("CSD","Response :"+response.toString());
                        System.out.println(response);*/
                        apiCallRunning.postValue(false);

                        saveSettlementReport(response);

                        apiCallSuccess.postValue(true);
                        apiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("onErrorResponse : "+error);
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

    private void saveSettlementReport(JSONObject response) {
        settlementReport = new SettlementReport();
        if (response.length() == 0){
            List<SettlementInfo> settlements = new ArrayList();
            settlementReport.setSettlements(settlements);

            return;
        }
        try {


            if( ! response.isNull("amountBilled")){
                settlementReport.setBilled(response.getDouble("amountBilled"));
            }
            if( ! response.isNull("amountPaidCash")){
                settlementReport.setCashCollection(response.getDouble("amountPaidCash"));
            }
            if( ! response.isNull("amountUnpaid")){
                settlementReport.setUnPaid(response.getDouble("amountUnpaid"));
            }
            if( ! response.isNull("amountPaidOnline")){
                settlementReport.setOnlineCollection(response.getDouble("amountPaidOnline"));
            }
            if( ! response.isNull("settledAmount")){
                settlementReport.setSettled(response.getDouble("settledAmount"));
            }
            if( ! response.isNull("charges")){
                settlementReport.setCharges(response.getDouble("charges"));
                //settlementReport.setCharges(9999);
            }
            if( ! response.isNull("merchantType")){
                settlementReport.setMerchantType(response.getString("merchantType"));
            }
            if( ! response.isNull("cgst")){
                settlementReport.setCgst(response.getDouble("cgst"));
            }
            if( ! response.isNull("sgst")){
                settlementReport.setSgst(response.getDouble("sgst"));
            }
            if( ! response.isNull("advanceTransfer")){
                settlementReport.setAdvanceTransfer(response.getDouble("advanceTransfer"));
            }
            if( ! response.isNull("availableAmount")){
                settlementReport.setAvailableAmount(response.getDouble("availableAmount"));
            }
            if( ! response.isNull("amountPrevious")){
                settlementReport.setPreviousBalance(response.getDouble("amountPrevious"));
            }
            if( ! response.isNull("totalInvoices")){
                settlementReport.setTotalNoOfInvoicesProcessed(response.getInt("totalInvoices"));
            }
            /*if( ! response.isNull("totalInvoices")){
                settlementReport.setTotalNoOfInvoicesProcessed(response.getInt("totalInvoices"));
            }*/
            if( ! response.isNull("pgTotalCharge")){
                settlementReport.setPaidBankCharges(response.getDouble("pgTotalCharge"));
            }
            double transferred = settlementReport.getSettled() - settlementReport.getCharges()
                    - settlementReport.getCgst()- settlementReport.getSgst();
            settlementReport.setTransferred(transferred);

            double pending =  settlementReport.getOnlineCollection() - settlementReport.getSettled();
            settlementReport.setPending(pending);

            List<SettlementInfo> settlements = new ArrayList();
            settlementReport.setSettlements(settlements);

            if( ! response.isNull("settlements")){

                JSONArray settlementsArray = response.getJSONArray("settlements");
                for (int i = 0; i < settlementsArray.length(); i++) {
                    try{
                        SettlementInfo settlementInfo = new SettlementInfo();

                        JSONObject settlementObject = settlementsArray.getJSONObject(i);

                        if( ! settlementObject.isNull("amount")){
                            settlementInfo.setAmount(settlementObject.getDouble("amount"));
                        }

                        if( ! settlementObject.isNull("charges")){
                            settlementInfo.setCharges(settlementObject.getDouble("charges"));
                        }

                        if( ! settlementObject.isNull("date")){
                            settlementInfo.setDate(new Date(settlementObject.getLong("date")));
                        }

                        if( ! settlementObject.isNull("transferredAmount")){
                            settlementInfo.setTransferredAmount(settlementObject.getDouble("transferredAmount"));
                        }

                        settlements.add(settlementInfo);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }

                // sorting for date wise listing
                Collections.sort(settlements, new Comparator<SettlementInfo>() {

                    @Override
                    public int compare(SettlementInfo lhs, SettlementInfo rhs) {
                        return lhs.getDate().compareTo(rhs.getDate());

                    }
                });
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void requestAdvance() {
        JSONObject jsonObject = getRequestAdvanceJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/addMerchantAdvance"
        final Authenticator authenticator = new Authenticator(ENDPOINT_ADD_MERCHANT_ADVANCE);

        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(">>>>>>>>>>>>> " + response.toString());
                        if (true) Log.d("CSD", "response: " + response.toString());
                        apiCallRunningReqAdv.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase(
                                    "success")) {
                                apiCallSuccessReqAdv.postValue(true);
                            } else {
                                System.out.println("Response jsonError"+response.toString());
                                apiCallErrorReqAdv.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            System.out.println("Response Error"+e.toString());
                            Crashlytics.logException(e);
                            apiCallErrorReqAdv.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunningReqAdv.postValue(false);
                apiCallErrorReqAdv.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        apiCallRunningReqAdv.setValue(true);
        apiCallErrorReqAdv.setValue(false);
        apiCallSuccessReqAdv.setValue(false);
    }

    private JSONObject getRequestAdvanceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("advanceAmount", advanceAmount);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void settleNow() {
        JSONObject jsonObject = getSettleNowJsonRequest();
        //Log.d("CSD","Month : "+this.month+" - "+this.year);
        if (true) Log.d("CSD", "request: " + jsonObject.toString());
        //
        final Authenticator authenticator = new Authenticator(ENDPOINT_INVOICE_PAYOUT);
        authenticator.setBody(jsonObject.toString());

         Log.d("CSD", "request: " + authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        apiCallRunningSettleNow.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase(
                                    "success")) {
                                settleNowMessage = response.getString("statusMsg");
                                apiCallSuccessSettleNow.postValue(true);
                            } else {
                                System.out.println("Response jsonError"+response.toString());
                                apiCallErrorSettleNow.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            System.out.println("Response Error"+e.toString());
                            Crashlytics.logException(e);
                            apiCallErrorSettleNow.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunningSettleNow.postValue(false);
                apiCallErrorSettleNow.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 0, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        apiCallRunningSettleNow.setValue(true);
        apiCallErrorSettleNow.setValue(false);
        apiCallSuccessSettleNow.setValue(false);
    }

    private JSONObject getSettleNowJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("month", String.valueOf(month));
            jsonObject.put("year", String.valueOf(this.year));

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
