package com.zopnote.android.merchant.managesubscription;

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
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_ADD_PAUSE;

public class ManageSubscriptionsViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AddOnDemandViewModel";

    private Context context;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;
    public LiveData<List<Invoice>> invoices;
    public LiveData<List<Subscription>> subscriptions;

    public Calendar pauseStartDateCalender;
    public Calendar pauseEndDateCalender;
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> endDateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> pauseApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> pauseApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> pauseApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteCustomizedApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteCustomizedApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteCustomizedApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public ManageSubscriptionsViewModel(Application context, Repository repository) {
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
        invoices = repository.getPendingInvoices(customerId);
        subscriptions = repository.getSubscriptions(customerId);
    }

    public void addPause(String subscriptionId, Date startDate, Date endDate) {
        JSONObject jsonObject = getAddPauseJsonRequest(subscriptionId, startDate, endDate);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());//AppConstants.ENDPOINT_ADD_PAUSE
        final Authenticator authenticator = new Authenticator(ENDPOINT_ADD_PAUSE);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        pauseApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                pauseApiCallSuccess.postValue(true);
                            } else {
                                pauseApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            pauseApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pauseApiRunning.postValue(false);
                pauseApiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;
                //return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        pauseApiRunning.setValue(true);
        pauseApiCallError.setValue(false);
        pauseApiCallSuccess.setValue(false);
    }

    private JSONObject getAddPauseJsonRequest(String subscriptionId, Date startDate, Date endDate) {
        try {
            JSONObject pauseObject = new JSONObject();

            JSONObject subscription = new JSONObject();
            subscription.put("subscriptionId", subscriptionId);

            if(startDate != null){
                subscription.put("pauseStartDate", startDate.getTime());
            }else{
                subscription.put("pauseStartDate", JSONObject.NULL);
            }

            if(endDate != null){
                subscription.put("pauseEndDate", endDate.getTime());
            }else {
                subscription.put("pauseEndDate", JSONObject.NULL);
            }

            JSONArray subscriptionsArray = new JSONArray();
            subscriptionsArray.put(subscription);

            pauseObject.put("customerId", customer.getValue().getId());
            pauseObject.put("merchantId", merchant.getValue().getId());
            pauseObject.put("subscriptions", subscriptionsArray);

            return pauseObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void deleteCustomized(String subscriptionId) {
        JSONObject jsonObject = getDeleteCustomizedJsonRequest(subscriptionId);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/deleteCustomizedSubscription"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_DELETE_CUST_SUBSCRIPTION);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        deleteCustomizedApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                deleteCustomizedApiCallSuccess.postValue(true);
                            } else {
                                deleteCustomizedApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            deleteCustomizedApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deleteCustomizedApiRunning.postValue(false);
                deleteCustomizedApiCallError.postValue(true);
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

        deleteCustomizedApiRunning.setValue(true);
        deleteCustomizedApiCallError.setValue(false);
        deleteCustomizedApiCallSuccess.setValue(false);
    }

    private JSONObject getDeleteCustomizedJsonRequest(String subscriptionId) {
        try {
            JSONObject pauseObject = new JSONObject();
            pauseObject.put("customerId", customer.getValue().getId());
            pauseObject.put("merchantId", merchant.getValue().getId());
            pauseObject.put("subscriptionId", subscriptionId);

            return pauseObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
