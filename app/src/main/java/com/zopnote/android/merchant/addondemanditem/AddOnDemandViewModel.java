package com.zopnote.android.merchant.addondemanditem;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zopnote.android.merchant.util.FormatUtil.DATE_FORMAT_DMMM_HH_MM;
import static com.zopnote.android.merchant.util.FormatUtil.DATE_FORMAT_DMMM_HH_MM_SS;

/**
 * Created by nmohideen on 03/02/18.
 */

public class AddOnDemandViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AddOnDemandViewModel";

    private Context context;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;
    public LiveData<List<Invoice>> invoices;
    public LiveData<List<Subscription>> subscriptions;
    public String latestInvoiceId;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> addOndemandApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> addOndemandApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> addOndemandApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public Map<String,Double> productItemMap;
    public List<String> onDemandProduct;
    public boolean billCaptured;
    public String encodedString;
    public int convenienceCharges;

    public AddOnDemandViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init(String customerId) {
        if (customer != null) {
            return;
        }

        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);
        invoices = repository.getInvoices(customerId);
        subscriptions = repository.getSubscriptions(customerId);
    }

    public void getOnDemandItems() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ON_DEMAND_ITEMS);
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/getOnDemandItems"

        authenticator.addParameter(Param.MERCHANT_ID, merchant.getValue().getId());
        System.out.println("API : "+authenticator.getUri());
        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println(response);
                        apiCallRunning.postValue(false);

                        saveOnDemandItems(response);

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

    private void saveOnDemandItems(JSONArray response) {

        if (response.length() == 0){
            apiCallError.postValue(true);
            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            return;
        }
        try {
            onDemandProduct = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                onDemandProduct.add(response.getString(i));
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void addOndemandItem(String type) {
        JSONObject jsonObject = addOndemandItemJsonRequest(type);
        if (true) Log.d("CSD", "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/addOndemandItem"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_ON_DEMAND_ITEMS);
        authenticator.setBody(jsonObject.toString());
        System.out.println("API : "+jsonObject.toString());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        addOndemandApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                addOndemandApiCallSuccess.postValue(true);
                            } else {
                                addOndemandApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            System.out.println("JsonError Respo" + e);
                            Crashlytics.logException(e);
                            addOndemandApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Respo" + error);
                addOndemandApiCallRunning.postValue(false);
                addOndemandApiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

        addOndemandApiCallRunning.setValue(true);
        addOndemandApiCallError.setValue(false);
        addOndemandApiCallSuccess.setValue(false);
    }

    private JSONObject addOndemandItemJsonRequest(String type) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("invoiceId", latestInvoiceId);
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("type", type);

            if (billCaptured){
                jsonObject.put("image", encodedString);
            }

            JSONArray productItemsArray = new JSONArray();
            Date now = new Date();
            for(String key : productItemMap.keySet()){
                JSONObject productItemObject = new JSONObject();
                productItemObject.put("item", FormatUtil.formatLocalDate(DATE_FORMAT_DMMM_HH_MM_SS,now).toUpperCase() +" : "+ key);
                productItemObject.put("amount", productItemMap.get(key));
                productItemsArray.put(productItemObject);
            }

            jsonObject.put("productItems", productItemsArray);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }



}
