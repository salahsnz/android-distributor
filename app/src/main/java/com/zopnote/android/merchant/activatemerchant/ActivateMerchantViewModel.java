package com.zopnote.android.merchant.activatemerchant;

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
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_ACTIVATE_MERCHANT;

/**
 * Created by KISHORE on 30/12/2020.
 */

public class ActivateMerchantViewModel extends AndroidViewModel {

    private String LOG_TAG = ActivateMerchantViewModel.class.getSimpleName();
    private Repository repository;
    public String merchantId;
    public String mobileNo;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public ActivateMerchantViewModel(Application context, Repository repository) {
        super(context);
        Log.d("CSD","ADD AREA VIEW MODEL");
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
    }

    public void callActivateMerchantAPI(String merchantName, String merchantBusinessName, String mobileNo) {

        JSONObject jsonObject = getActivateJsonRequest(merchantName,merchantBusinessName,mobileNo);
        if (true) Log.d("CSD", "request: " + jsonObject.toString());

        final Authenticator authenticator = new Authenticator(ENDPOINT_ACTIVATE_MERCHANT);
        authenticator.setBody(jsonObject.toString());

        Log.d("CSD", "URL " + authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "response: " + response.toString());
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
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                System.out.println(error);
                Log.d("CSD",error.toString());
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

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
    }

    private JSONObject getActivateJsonRequest(String merchantName, String businessName,String mobileNo) {
        try {
            JSONObject object = new JSONObject();

            object.put("merchantOwnerName", merchantName);
            object.put("merchantContact", mobileNo);
            object.put("merchantName", businessName);

            System.out.println(object.toString());
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
