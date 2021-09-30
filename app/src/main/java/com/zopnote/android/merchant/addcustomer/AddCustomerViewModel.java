package com.zopnote.android.merchant.addcustomer;

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
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.Utils;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nmohideen on 03/02/18.
 */

public class AddCustomerViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AddCustomerViewModel";

    private Context context;

    private Repository repository;

    public String mobileNumber;
    public String name;
    public String doorNumber;
    public String addressLine2;
    public Map<String, Product> productIdMap;
    public String route;
    public Map<String, String> addressLine1NameSelectedValueMap;
    public Map<String, String> addressLine1NameLabelMap;

    public MutableLiveData<Integer> step = new MutableLiveData<>();

    public LiveData<Merchant> merchant;
    public LiveData<List<Product>> products;
    public LiveData<List<Customer>> customers;
    public List<Product> productList;

    public MutableLiveData<Boolean> productsLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public AddCustomerViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;

        productsLoading.setValue(false);
    }

    public void init() {
        if (products != null) {
            return;
        }

        step.setValue(1);

        productIdMap = new HashMap<String, Product>();
        addressLine1NameSelectedValueMap = new LinkedHashMap<>();
        addressLine1NameLabelMap = new HashMap<>();

        merchant = repository.getMerchant();

        productsLoading.setValue(true);
        products = repository.getProducts();
        customers = repository.getCustomers();
        productList = new ArrayList<>();
    }

    public boolean isEdited() {
        if (mobileNumber != null && mobileNumber.trim().length() > 0) {
            return true;
        }

        return false;
    }

    public void addCustomer() {
        JSONObject jsonObject = getAddCustomerJsonRequest();
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_CUSTOMER);
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/addCustomer"
        authenticator.setBody(jsonObject.toString());
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        System.out.println("API"  +  authenticator.getUri());
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
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

    private JSONObject getAddCustomerJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            if (mobileNumber != null && mobileNumber.trim().length() > 0) {
                jsonObject.put("mobileNumber", "+91" + mobileNumber);
            }
            jsonObject.put("firstName", name);
            jsonObject.put("doorNumber", doorNumber);
            jsonObject.put("addressLine1", getAddressLine1JsonObject());
            jsonObject.put("addressLine2", addressLine2);
            jsonObject.put("route", route);
            jsonObject.put("subscriptions", getSubscriptionsJsonArray());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray getSubscriptionsJsonArray() {
        JSONArray jsonArray = new JSONArray();
        for (String key : productIdMap.keySet()) {
            jsonArray.put(key);
        }
        return jsonArray;
    }

    private JSONObject getAddressLine1JsonObject() {
        if(addressLine1NameSelectedValueMap.isEmpty()){
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            for (String key : addressLine1NameSelectedValueMap.keySet()) {
                jsonObject.put(key, addressLine1NameSelectedValueMap.get(key));
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
