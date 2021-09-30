package com.zopnote.android.merchant.home;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.Editable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.MyApplication;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_PROCESS_SETTLEMENT;

/**
 * Created by nmohideen on 03/02/18.
 */

public class HomeViewModel extends AndroidViewModel {
    private static String LOG_TAG = HomeViewModel.class.getSimpleName();
    private static boolean DEBUG = false;
    private Repository repository;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> updateMobileApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateMobileApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateMobileApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> resetMerchantApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> resetMerchantApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> resetMerchantApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> sendRemainderApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendRemainderApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendRemainderApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> sendSettlementApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendSettlementApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendSettlementApiCallError = new MutableLiveData<>();

    public String apiCallErrorMessage;
    public String apiCallSuccessMessage;
    public String resetMerchantApiCallMessage;

    public HomeViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init() {
        merchant = repository.getMerchant();
    }


    public void resetMerchant() {
        JSONObject jsonObject = getResetMerchantJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/addCustomerMobileNumber"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_RESET_MERCHANT);
        authenticator.setBody(jsonObject.toString());

        System.out.println("API : "+authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        resetMerchantApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                resetMerchantApiCallMessage = response.getString("message");
                                resetMerchantApiCallSuccess.postValue(true);
                            } else {
                                resetMerchantApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            resetMerchantApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println(error.toString());
                resetMerchantApiRunning.postValue(false);
                resetMerchantApiCallError.postValue(true);
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

        resetMerchantApiRunning.setValue(true);
        resetMerchantApiCallError.setValue(false);
        resetMerchantApiCallSuccess.setValue(false);
    }

    private JSONObject getResetMerchantJsonRequest() {
        try {
            JSONObject object = new JSONObject();

            object.put("merchantId", merchant.getValue().getId());


            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addCustomerMobileNumber(String mobile) {
            JSONObject jsonObject = getUpdateCustMobileJsonRequest(mobile);
            if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
            //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/addCustomerMobileNumber"
            final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_CUSTOMER_MOBILE);
            authenticator.setBody(jsonObject.toString());

            System.out.println("API : "+authenticator.getUri());

            JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                    authenticator.getUri(),
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                            updateMobileApiRunning.postValue(false);
                            try {
                                String status = response.getString("status");
                                if (status.equalsIgnoreCase("success")) {
                                    updateMobileApiCallSuccess.postValue(true);
                                } else {
                                    updateMobileApiCallError.postValue(true);
                                    apiCallErrorMessage = response.getString("errorMessage");
                                }
                            } catch (JSONException e) {
                                Crashlytics.logException(e);
                                updateMobileApiCallError.postValue(true);
                                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    System.out.println(error.toString());
                    updateMobileApiRunning.postValue(false);
                    updateMobileApiCallError.postValue(true);
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

            updateMobileApiRunning.setValue(true);
            updateMobileApiCallError.setValue(false);
            updateMobileApiCallSuccess.setValue(false);
        }

        private JSONObject getUpdateCustMobileJsonRequest(String mobile) {
            try {
                JSONObject object = new JSONObject();

                object.put("merchantId", merchant.getValue().getId());
                object.put("mobileNumber", mobile);


                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }


    public void sendPaymentRemainderSMS(String route) {
        JSONObject jsonObject = getPaymentRemainderSMSJsonRequest(route);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        //"http://18.139.71.131:8080/ZopnoteWeb/app/v1/sendSMSReminder"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SEND_SMS_REMAINDER);
        authenticator.setBody(jsonObject.toString());

        System.out.println("API : "+authenticator.getUri());
        System.out.println("API : "+jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        sendRemainderApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                apiCallSuccessMessage = "Msg Count : "+response.getString("msgCount") +" Amount : " + response.getString("totalOutstandingAmt");
                                sendRemainderApiCallSuccess.postValue(true);
                            } else {
                                sendRemainderApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            sendRemainderApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println(error.toString());
                sendRemainderApiRunning.postValue(false);
                sendRemainderApiCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(1000000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        sendRemainderApiRunning.setValue(true);
        sendRemainderApiCallError.setValue(false);
        sendRemainderApiCallSuccess.setValue(false);
    }

    private JSONObject getPaymentRemainderSMSJsonRequest(String route) {
        try {
            JSONObject object = new JSONObject();

            object.put("merchantId", merchant.getValue().getId());
            object.put("route", route);

            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void processSettlement() {

        JSONObject object = new JSONObject();

        try {
            object.put("merchantId", merchant.getValue().getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Authenticator authenticator = new Authenticator(ENDPOINT_PROCESS_SETTLEMENT);
        authenticator.setBody(object.toString());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        sendSettlementApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                apiCallSuccessMessage = response.getString("successMessage");
                                sendSettlementApiCallSuccess.postValue(true);
                            } else {
                                sendSettlementApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            sendSettlementApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println(error.toString());
                sendSettlementApiRunning.postValue(false);
                sendSettlementApiCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               // Map<String, String> params = new HashMap<String, String>();
              //  params.put("key", "76380346");
               // params.put("Content-Type", "application/json");
              //  return params;
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(1000000, 1, 1f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        sendSettlementApiRunning.setValue(true);
        sendSettlementApiCallError.setValue(false);
        sendSettlementApiCallSuccess.setValue(false);
    }



}

