package com.zopnote.android.merchant.notifications.notificationpanel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
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
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.zopnote.android.merchant.BuildConfig.API_ENDPOINT;
import static com.zopnote.android.merchant.BuildConfig.DEBUG;

/**
 * Created by salah on 30/01/2020.
 */

public class NotificationDashboardViewModel extends AndroidViewModel {

    private String LOG_TAG = NotificationDashboardViewModel.class.getSimpleName();
    private Repository repository;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public String apiCallErrorMessage;
    public String notiTitle;
    public String notiMessage;
    public String actionUrl;
    public String notiMode;

    public NotificationDashboardViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;

    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
    }


    public void sendNotification() {
        JSONObject jsonObject = getAgreementJsonRequest();

        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        // "http://18.139.71.131:8080/ZopnoteWeb/app/v1/deleteNotifications"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SEND_PUSH_NOTIFICATION);
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
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                System.out.println(error);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
              /*  Map<String, String> params = new HashMap<String, String>();
                params.put("key", "76380346");
                params.put("Content-Type", "application/json");
                return params;*/
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

    private JSONObject getAgreementJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("notiTitle", notiTitle);
            jsonObject.put("notiMessage", notiMessage);
            jsonObject.put("notiMode", notiMode);

            if (actionUrl!=null)
                jsonObject.put("notiActionUrl", actionUrl);
            Log.d("CSD","Json : "+jsonObject.toString());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
