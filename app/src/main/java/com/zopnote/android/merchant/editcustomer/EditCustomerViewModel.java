package com.zopnote.android.merchant.editcustomer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
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
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EditCustomerViewModel extends AndroidViewModel {
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "EditCustomerViewModel";

    private Context context;

    private Repository repository;
    public LiveData<Customer> customer;
    public LiveData<Merchant> merchant;
    public LiveData<List<Customer>> customers;

    public String mobileNumber;
    public String placeHolderMobileNumber;
    public String name;
    public String email;
    public String doorNumber;
    public String addressLine2;
    public String route;
    public Map<String, String> addressLine1NameSelectedValueMap;
    public Map<String, String> addressLine1NameLabelMap;

    public MutableLiveData<Boolean> customersLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public EditCustomerViewModel(@NonNull Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;

        customersLoading.setValue(false);
    }

    public void init(String customerId) {
        if (customer != null) {
            return;
        }

        addressLine1NameSelectedValueMap = new LinkedHashMap<>();
        addressLine1NameLabelMap = new HashMap<>();

        customersLoading.setValue(true);
        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);
        customers = repository.getCustomers();
    }

    public void updateProfile() {
        JSONObject jsonObject = getUpdateCustomerJsonRequest();
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/updateCustomer"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_CUSTOMER_PROFILE);
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

    private JSONObject getUpdateCustomerJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("mobileNumber", "+91" + mobileNumber);
            jsonObject.put("email", email);
            jsonObject.put("firstName", name);
            jsonObject.put("lastName", null);
            jsonObject.put("doorNumber", doorNumber);
            jsonObject.put("addressLine1", getAddressLine1JsonObject());
            jsonObject.put("addressLine2", addressLine2);
            jsonObject.put("route", route);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
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
