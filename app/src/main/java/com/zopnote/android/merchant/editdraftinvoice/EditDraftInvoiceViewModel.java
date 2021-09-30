package com.zopnote.android.merchant.editdraftinvoice;

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
import com.zopnote.android.merchant.data.model.DraftInvoice;
import com.zopnote.android.merchant.data.model.DraftInvoiceItem;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EditDraftInvoiceViewModel extends AndroidViewModel {
    private static String LOG_TAG = EditDraftInvoiceViewModel.class.getSimpleName();
    private static boolean DEBUG = false;

    private Repository repository;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> updateInvoiceApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateInvoiceApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateInvoiceApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public MutableLiveData<Boolean> getInvoiceApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> getInvoiceApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> getInvoiceApiCallError = new MutableLiveData<>();

    private String invoiceId;
    public String customerId;
    public List<DraftInvoiceItem> draftInvoiceItems;
    public DraftInvoice draftInvoice;

    public EditDraftInvoiceViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(String customerId, String invoiceId){
        if (merchant != null) {
            return;
        }

        this.customerId = customerId;
        this.invoiceId = invoiceId;

        merchant = repository.getMerchant();
        draftInvoiceItems = new ArrayList<>();
    }

    public void updateInvoice() {
        JSONObject jsonObject = getUpdateInvoiceJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_INVOICE);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        updateInvoiceApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                updateInvoiceApiCallSuccess.postValue(true);
                            } else {
                                updateInvoiceApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            updateInvoiceApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateInvoiceApiCallRunning.postValue(false);
                updateInvoiceApiCallError.postValue(true);
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

        updateInvoiceApiCallRunning.setValue(true);
        updateInvoiceApiCallError.setValue(false);
        updateInvoiceApiCallSuccess.setValue(false);
    }

    private JSONObject getUpdateInvoiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customerId);
            jsonObject.put("invoiceId", draftInvoice.getId());

            JSONArray invoiceItemsArray = new JSONArray();

            for(DraftInvoiceItem draftInvoiceItem : draftInvoiceItems){
                JSONObject invoiceItemObject = new JSONObject();
                invoiceItemObject.put("item", draftInvoiceItem.getItem());
                invoiceItemObject.put("amount", draftInvoiceItem.getAmount());
                invoiceItemObject.put("autoGenerated", draftInvoiceItem.isAutoGenerated());

                invoiceItemsArray.put(invoiceItemObject);
            }
            jsonObject.put("invoiceItems", invoiceItemsArray);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getInvoice() {

        String endPoint;
        if(invoiceId != null){
            endPoint = AppConstants.ENDPOINT_GET_INVOICE;
        }else{
            endPoint = AppConstants.ENDPOINT_GET_DRAFT_INVOICE;
        }
        System.out.println(endPoint);
        final Authenticator authenticator = new Authenticator(endPoint);

        authenticator.addParameter(Param.MERCHANT_ID, merchant.getValue().getId());
        authenticator.addParameter(Param.CUSTOMER_ID, customerId);

        if(invoiceId != null){
            authenticator.addParameter(Param.INVOICE_ID, invoiceId);
        }
        System.out.println(merchant.getValue().getId());
        System.out.println(customerId);
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println(response.toString());
                        System.out.println(response.length());
                        if (response.length() != 0 ){
                        getInvoiceApiCallRunning.postValue(false);

                        saveDraftInvoice(response);

                        getInvoiceApiCallSuccess.postValue(true);
                        }else {

                            getInvoiceApiCallRunning.postValue(false);
                            getInvoiceApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.draft_invoice_error_message);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getInvoiceApiCallRunning.postValue(false);
                getInvoiceApiCallError.postValue(true);
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

        getInvoiceApiCallRunning.setValue(true);
        getInvoiceApiCallError.setValue(false);
        getInvoiceApiCallSuccess.setValue(false);
    }

    private void saveDraftInvoice(JSONObject invoiceObject) {

        try{
            draftInvoice = new DraftInvoice();

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

            draftInvoice.setNotes(invoiceObject.getString("notes"));

            draftInvoice.setDueDate(new Date(invoiceObject.getLong("dueDate")));

        }catch (JSONException e){
            e.printStackTrace();
        }
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
