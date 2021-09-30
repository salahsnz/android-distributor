package com.zopnote.android.merchant.movecustomer;

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

import java.util.List;
import java.util.Map;

public class MoveCustomerViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "MoveCustomerViewModel";

    private final Repository repository;
    private final Context context;
    public LiveData<Customer> customer;
    public LiveData<Merchant> merchant;
    public LiveData<List<Customer>> customers;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public MoveCustomerViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
        this.context = application;
    }

    public void init(String customerId, String route){
        if(customer != null){
            return;
        }

        customer = repository.getCustomer(customerId);
        customers = repository.getCustomers(route);
        merchant = repository.getMerchant();
    }

    public void moveCustomer(Customer previousCustomer, Customer nextCustomer) {
        JSONObject jsonObject = getMoveCustomerJsonRequest(previousCustomer, nextCustomer);
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_ROUTE_SEQUENCE);
        authenticator.setBody(jsonObject.toString());

        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());

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

    private JSONObject getMoveCustomerJsonRequest(Customer previousCustomer, Customer nextCustomer) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("route", customer.getValue().getRoute());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("previousCustomerId", previousCustomer.getId());
            if (nextCustomer != null) {
                jsonObject.put("nextCustomerId", nextCustomer.getId());
            }
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
