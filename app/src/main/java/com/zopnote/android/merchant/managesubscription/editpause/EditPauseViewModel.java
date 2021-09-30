package com.zopnote.android.merchant.managesubscription.editpause;

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

public class EditPauseViewModel extends AndroidViewModel {

    private static String LOG_TAG = EditPauseViewModel.class.getSimpleName();
    private static boolean DEBUG = false;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;

    public Calendar pauseStartDateCalender;
    public Calendar pauseEndDateCalender;
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> endDateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> updatePauseApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updatePauseApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updatePauseApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> deletePauseApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> deletePauseApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> deletePauseApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public String tag;

    public EditPauseViewModel(@NonNull Application application, Repository repository) {
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

    public void updatePause(String subscriptionId, Date startDate, Date endDate, String pauseId) {
        JSONObject jsonObject = getUpdatePauseJsonRequest(subscriptionId, startDate, endDate, pauseId);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_PAUSE);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        updatePauseApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                updatePauseApiCallSuccess.postValue(true);
                            } else {
                                updatePauseApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            updatePauseApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updatePauseApiRunning.postValue(false);
                updatePauseApiCallError.postValue(true);
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

        updatePauseApiRunning.setValue(true);
        updatePauseApiCallError.setValue(false);
        updatePauseApiCallSuccess.setValue(false);
    }

    private JSONObject getUpdatePauseJsonRequest(String subscriptionId, Date startDate, Date endDate, String pauseId) {
        try {
            JSONObject pauseObject = new JSONObject();

            JSONObject subscription = new JSONObject();
            subscription.put("pauseId", pauseId);
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

    public void deletePause(String subscriptionId, String pauseId){
        JSONObject jsonObject = getDeletePauseJsonRequest(subscriptionId, pauseId);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_DELETE_PAUSE);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        deletePauseApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                deletePauseApiCallSuccess.postValue(true);
                            } else {
                                deletePauseApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            deletePauseApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deletePauseApiRunning.postValue(false);
                deletePauseApiCallError.postValue(true);
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

        deletePauseApiRunning.setValue(true);
        deletePauseApiCallError.setValue(false);
        deletePauseApiCallSuccess.setValue(false);
    }

    private JSONObject getDeletePauseJsonRequest(String subscriptionId, String pauseId) {
        try {

            JSONObject subscriptionObject = new JSONObject();
            subscriptionObject.put("pauseId", pauseId);
            subscriptionObject.put("subscriptionId", subscriptionId);

            JSONArray subscriptions = new JSONArray();
            subscriptions.put(subscriptionObject);

            JSONObject deleteRequest = new JSONObject();
            deleteRequest.put("merchantId", merchant.getValue().getId());
            deleteRequest.put("customerId", customer.getValue().getId());
            deleteRequest.put("subscriptions", subscriptions);

            return deleteRequest;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
