package com.zopnote.android.merchant.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
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
import com.zopnote.android.merchant.BuildConfig;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.InitRequestBuilder;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nmohideen on 14/02/18.
 */
//112368
public class LoginViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> isRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> isError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthCallSuccess = new MutableLiveData<>();


    public MutableLiveData<Boolean> sendOTPCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendOTPCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendOTPCallSuccess = new MutableLiveData<>();


    public MutableLiveData<Boolean> verifyOTPCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> verifyOTPCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> verifyOTPCallSuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> resendOTPCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> resendOTPCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> resendOTPCallSuccess = new MutableLiveData<>();


    public MutableLiveData<Boolean> isNewUser = new MutableLiveData<>();

    public MutableLiveData<Integer> step = new MutableLiveData<>();

    public boolean isOTPSend;
    public String authorizationCode;

    public String loginToken;
    public String uid;
    public String mobileNumber;
    public String merchantStatus;
    public String otp;
    private String apiCallErrorMessage;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        step.setValue(1);
        isRunning.setValue(false);
        isError.setValue(false);
        isAuthCallSuccess.setValue(false);
        isNewUser.setValue(false);
    }

    public void callAuthApi() {

        InitRequestBuilder initRequestBuilder = new InitRequestBuilder(this.getApplication())
                .setDebugIfApplicable()
                .setAifa()
                .setAppVersion()
                .setFirebaseProjectId()
                .setMobileNumber("+91"+mobileNumber);
        JSONObject jsonObject = initRequestBuilder.build();
        System.out.println("------------------------"+jsonObject.toString());

        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_INIT_M);// "http://18.139.71.131:8080/ZopnoteWeb/app/v1/init"
       //System.out.println("Authenticator : "+authenticator.getUri());
        authenticator.setBody(jsonObject.toString());
        System.out.println(">>>>>>>> After Login URL : "+jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("Login   :  " + response.toString());
                        //Log.d("CSD" ,"------------"+ response.toString());
                        isRunning.postValue(false);

                        try {

                            loginToken = response.getString(AppConstants.ATTR_FIREBASE_TOKEN);
                            uid = response.getString(AppConstants.ATTR_UID);
                            mobileNumber = response.getString(AppConstants.ATTR_MOBILE_NUMBER);
                            merchantStatus = response.getString(AppConstants.ATTR_MERCHANT_STATUS);
                            boolean isNewUsers = response.getBoolean("isNewUser");
                            if (BuildConfig.PRODUCT_FLAVOUR_MERCHANT) {
                                if (isNewUsers) {
                                    isNewUser.postValue(true);
                                } else {
                                    if (merchantStatus.equalsIgnoreCase("") || merchantStatus.equalsIgnoreCase("1111")) {
                                        System.out.println("merchant got" );
                                        isNewUser.postValue(true);
                                    }else
                                        isAuthCallSuccess.postValue(true);
                                }
                            } else {
                                isAuthCallSuccess.postValue(true);
                            }

                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            isError.postValue(true);

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Login error  :  " + error.toString());
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


    public void sendOTP() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SEND_OTP);
        authenticator.addParameter("mobileNumber", mobileNumber);
        System.out.println("API : " + authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        sendOTPCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                sendOTPCallSuccess.postValue(true);
                            } else {
                                sendOTPCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            sendOTPCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Login error  :  " + error.toString());
                sendOTPCallRunning.postValue(false);
                sendOTPCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        sendOTPCallRunning.setValue(true);
        sendOTPCallError.setValue(false);
        sendOTPCallSuccess.setValue(false);
    }


    public void verifyOTP() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_VERIFY_OTP);
        authenticator.addParameter("mobileNumber", mobileNumber);
        authenticator.addParameter("otp", otp);

        System.out.println("API : " + authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("verifyOTP  :  " + response.toString());
                        verifyOTPCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                verifyOTPCallSuccess.postValue(true);
                            } else {
                                verifyOTPCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            verifyOTPCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                verifyOTPCallRunning.postValue(false);
                verifyOTPCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        verifyOTPCallRunning.setValue(true);
        verifyOTPCallError.setValue(false);
        verifyOTPCallSuccess.setValue(false);
    }

    public void reSendOTP() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_RE_SEND_OTP);
        authenticator.addParameter("mobileNumber", mobileNumber);
        authenticator.addParameter("retryType", "text");

        System.out.println("API : " + authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("reSendOTP  :  " + response.toString());
                        resendOTPCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                resendOTPCallSuccess.postValue(true);
                            } else {
                                resendOTPCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            resendOTPCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                resendOTPCallRunning.postValue(false);
                resendOTPCallError.postValue(true);
                apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return authenticator.getVolleyHttpHeaders();
            }
        };
        volleyRequest.setShouldCache(false);
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2f));
        VolleyManager.getInstance(this.getApplication()).addToRequestQueue(volleyRequest);

        resendOTPCallRunning.setValue(true);
        resendOTPCallError.setValue(false);
        resendOTPCallSuccess.setValue(false);
    }


}
