package com.zopnote.android.merchant.registermerchant;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.InitRequestBuilder;
import com.zopnote.android.merchant.util.Prefs;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_REGISTER_MERCHANT;

/**
 * Created by nmohideen on 14/02/18.
 */

public class RegisterViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> isRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> isError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthCallSuccess = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public RegisterViewModel(@NonNull Application application) {
        super(application);

        isRunning.setValue(false);
        isError.setValue(false);
        isAuthCallSuccess.setValue(false);
    }

    public void callRegisterApi(String merchantName, String businessName) {

        JSONObject jsonObject = getRegisterJsonRequest(merchantName,businessName);
        //AppConstants.ENDPOINT_REGISTER_MERCHANT
        final Authenticator authenticator = new Authenticator(ENDPOINT_REGISTER_MERCHANT);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                isAuthCallSuccess.postValue(true);
                            } else {
                                isError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }

                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            isError.postValue(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isRunning.postValue(false);
                isError.postValue(true);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1f)); // 10s timeout, 2 retry, 2f backoff multiplier
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        isRunning.setValue(true);
    }

    private JSONObject getRegisterJsonRequest(String merchantName, String businessName) {
        try {
            JSONObject object = new JSONObject();

            object.put("merchantOwnerName", merchantName);
            object.put("merchantName", businessName);
            object.put("mobileNumber", Prefs.getString(AppConstants.PREFS_SIGNED_IN_MOBILE_NUMBER,""));

            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
