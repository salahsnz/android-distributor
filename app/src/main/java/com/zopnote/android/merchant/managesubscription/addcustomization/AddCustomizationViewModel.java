package com.zopnote.android.merchant.managesubscription.addcustomization;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

public class AddCustomizationViewModel extends AndroidViewModel {
    private static String LOG_TAG = AddCustomizationViewModel.class.getSimpleName();
    private static boolean DEBUG = false;

    private Repository repository;
    public LiveData<Merchant> merchant;
    public LiveData<Subscription> subscription;
    public String subscriptionId;
    public String customerId;

    public Calendar annualSubscriptionStartDateCalender;
    public Calendar annualSubscriptionEndDateCalender;
    public MutableLiveData<Boolean> annualSubscriptionStartDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> annualSubscriptionEndDateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> customSubscriptionApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> customSubscriptionApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> customSubscriptionApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public boolean annualSubscription;
    public String pricingMode;
    public Double perIssuePrice;

    public AddCustomizationViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(String subscriptionId, String customerId){
        if (subscription != null) {
            return;
        }

        this.subscriptionId = subscriptionId;
        this.customerId = customerId;
        merchant = repository.getMerchant();
        subscription = repository.getSubscription(subscriptionId);

        annualSubscriptionStartDateChanged.setValue(false);
        annualSubscriptionEndDateChanged.setValue(false);
    }

    public void customizeSubscription(JSONObject customSubscriptionJson) {
        JSONObject jsonObject = getJsonRequest(customSubscriptionJson);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_CUSTOMIZE_SUBSCRIPTION);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        customSubscriptionApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                customSubscriptionApiCallSuccess.postValue(true);
                            } else {
                                customSubscriptionApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            customSubscriptionApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customSubscriptionApiRunning.postValue(false);
                customSubscriptionApiCallError.postValue(true);
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

        customSubscriptionApiRunning.setValue(true);
        customSubscriptionApiCallError.setValue(false);
        customSubscriptionApiCallSuccess.setValue(false);
    }

    private JSONObject getJsonRequest(JSONObject customSubscriptionJson) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("customerId", customerId); //mandatory
            jsonObject.put("merchantId", merchant.getValue().getId()); //mandatory

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(customSubscriptionJson);
            jsonObject.put("subscriptions", jsonArray);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
