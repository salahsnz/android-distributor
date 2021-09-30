package com.zopnote.android.merchant.updateinvoice;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
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
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nmohideen on 03/02/18.
 */

public class UpdateInvoiceViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "UpdateInvoiceViewModel";
    public String invoiceId;
    public String invoiceStatusAction;
    public String customerId;

    private Context context;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;
    public LiveData<List<Invoice>> invoices;
    public Invoice latestInvoice;
    public String merchantName;
    public Calendar InvStatusDateChangeCalender;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public UpdateInvoiceViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }
        InvStatusDateChangeCalender = FormatUtil.getLocalCalender();
        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);
        invoices = repository.getInvoices(customerId);
    }

    public void updateInvoice() {
        JSONObject jsonObject = getUpdateInvoiceJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/adminUpdateInvoice"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADMIN_UPDATE_INVOICE);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        apiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                apiCallSuccess.postValue(true);
                            } else {
                                apiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            apiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message)+"0";
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message )+"1";
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

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
    }

    private JSONObject getUpdateInvoiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customerId);
            jsonObject.put("invoiceId", latestInvoice.getId());
            jsonObject.put("action",invoiceStatusAction);
            if (invoiceStatusAction.equalsIgnoreCase("PAID"))
                jsonObject.put("paymentDate", String.valueOf(InvStatusDateChangeCalender.getTime().getTime()));
            else
                jsonObject.put("paymentDate", String.valueOf(new Date()));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
