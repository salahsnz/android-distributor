package com.zopnote.android.merchant.managesubscription.editsubscription;

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
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class EditSubscriptionViewModel extends AndroidViewModel {

    private static String LOG_TAG = EditSubscriptionViewModel.class.getSimpleName();
    private static boolean DEBUG = false;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;

    public Calendar startDateCalender;
    public Calendar endDateCalender;
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> endDateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> subscriptionUpdateApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> subscriptionUpdateApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> subscriptionUpdateApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> subscriptionDeleteApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> subscriptionDeleteApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> subscriptionDeleteApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public String tag;
    public String quantity;

    public EditSubscriptionViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(String customerId) {
        if (customer != null) {
            return;
        }

        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);

        startDateChanged.setValue(false);
        endDateChanged.setValue(false);
    }

    public void updateSubscription(String subscriptionId, Date startDate, Date endDate) {
        JSONObject jsonObject = getUpdateSubscriptionJsonRequest(subscriptionId, startDate, endDate);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_SUBSCRIPTIONS);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        subscriptionUpdateApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                subscriptionUpdateApiCallSuccess.postValue(true);
                            } else {
                                subscriptionUpdateApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            subscriptionUpdateApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                subscriptionUpdateApiRunning.postValue(false);
                subscriptionUpdateApiCallError.postValue(true);
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

        subscriptionUpdateApiRunning.setValue(true);
        subscriptionUpdateApiCallError.setValue(false);
        subscriptionUpdateApiCallSuccess.setValue(false);
    }

    private JSONObject getUpdateSubscriptionJsonRequest(String subscriptionId, Date startDate, Date endDate) {
        try {
            JSONObject subscription = new JSONObject();
            subscription.put("subscriptionId", subscriptionId);


            if(startDate != null) {
                subscription.put("startDate", startDate.getTime());
            }else{
                subscription.put("startDate", JSONObject.NULL );
            }

            if(endDate != null){
                subscription.put("endDate", endDate.getTime());
            }else {
                subscription.put("endDate", JSONObject.NULL);
            }

            subscription.put("tag", tag);
            subscription.put("quantity", quantity);

            JSONArray subscriptions = new JSONArray();
            subscriptions.put(subscription);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("subscriptions", subscriptions);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteSubscription(String subscriptionId){
        JSONObject jsonObject = getTerminateSubscriptionJsonRequest(subscriptionId);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_REMOVE_SUBSCRIPTIONS);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        subscriptionDeleteApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                subscriptionDeleteApiCallSuccess.postValue(true);
                            } else {
                                subscriptionDeleteApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            subscriptionDeleteApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                subscriptionDeleteApiRunning.postValue(false);
                subscriptionDeleteApiCallError.postValue(true);
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

        subscriptionDeleteApiRunning.setValue(true);
        subscriptionDeleteApiCallError.setValue(false);
        subscriptionDeleteApiCallSuccess.setValue(false);
    }

    private JSONObject getTerminateSubscriptionJsonRequest(String subscriptionId) {
        try {
            JSONArray subscriptions = new JSONArray();
            subscriptions.put(subscriptionId);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("subscriptions", subscriptions);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
