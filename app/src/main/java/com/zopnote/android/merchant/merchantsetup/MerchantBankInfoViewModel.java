package com.zopnote.android.merchant.merchantsetup;

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
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by KISHORE on 12/15/2020.
 */

public class MerchantBankInfoViewModel extends AndroidViewModel {

    private String LOG_TAG = MerchantBankInfoViewModel.class.getSimpleName();
    private Repository repository;
    public String merchantId;
    public String merchantBankAccountName;
    public String merchantBankAccountNo;
    public String merchantBankName;
    public String merchantBankIFSCCode;
    public String merchantBusinessName;
    public Merchant merchantModel;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> addMerchantApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> addMerchantApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> addMerchantApiCallError = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public MerchantBankInfoViewModel(Application context, Repository repository) {
        super(context);
        Log.d("CSD","MERCHANT BankInfo VIEW MODEL");
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
    }

    public void getBankInfoData() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_MERCHANT);

        authenticator.addParameter(Param.MERCHANT_ID,merchant.getValue().getId() );//merchant.getValue().getId()
        System.out.println("API : "+authenticator.getUri());
        Log.d("CSD",authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //if (true) Log.d("CSD", "response: " + response.toString());

                        System.out.println("CSD"+ "response: " + response.toString());

                        if (response.length() != 0 ) {
                            apiCallRunning.postValue(false);

                            saveBankInfoData(response);

                            apiCallSuccess.postValue(true);
                        }
                        else {
                            apiCallRunning.postValue(false);
                            apiCallSuccess.postValue(true);
                            apiCallError.postValue(false);
                            networkError.postValue(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("onErrorResponse : "+error.toString());
                Log.d("CSD",error.toString());
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                networkError.postValue(false);
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

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
        networkError.setValue(false);
    }

    private void saveBankInfoData(JSONObject merchantObject) {

        try {
            merchantModel = new Merchant();
            merchantModel.setBankAccountName(merchantObject.getString("merchantBankAccountName"));
            merchantModel.setBankAccountNo(merchantObject.getString("merchantBankAccountNo"));
            merchantModel.setBankIFSCCode(merchantObject.getString("merchantBankIFSCCode"));
            merchantModel.setBankName(merchantObject.getString("merchantBankName"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }


    public void submitBankInfo()
    {
        JSONObject jsonObject = getAddMerchantJsonRequest();
        if (true) Log.d("CSD", "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_MERCHANT);
        authenticator.setBody(jsonObject.toString());

        System.out.println("API : "+authenticator.getUri());
        Log.d("CSD",authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "response " + response.toString());

                        addMerchantApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                //resetMerchantApiCallMessage = response.getString("message");
                                addMerchantApiCallSuccess.postValue(true);
                            } else {
                                addMerchantApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            addMerchantApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD",error.toString());
                error.printStackTrace();
                System.out.println(error.toString());
                addMerchantApiCallRunning.postValue(false);
                addMerchantApiCallError.postValue(true);
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

        addMerchantApiCallRunning.setValue(true);
        addMerchantApiCallError.setValue(false);
        addMerchantApiCallSuccess.setValue(false);
    }

    private JSONObject getAddMerchantJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( AppConstants.ATTR_MERCHANT_UPDATE,"Bank");
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("merchantBankAccountName",merchantBankAccountName);
            jsonObject.put("merchantBankAccountNo", merchantBankAccountNo);
            jsonObject.put("merchantBankIFSCCode",merchantBankIFSCCode);
            jsonObject.put("merchantBankName", merchantBankName);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
