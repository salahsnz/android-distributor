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

public class ProductSetupViewModel extends AndroidViewModel {

    private String LOG_TAG = ProductSetupViewModel.class.getSimpleName();
    private Repository repository;
    public String merchantId;
    public String route;
    public Merchant merchantModel;
    public boolean profileImageAlreadyExists=false;
    public String productName;
    public String productPrice;
    public String productType;

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> addProductSetupApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> addProductSetupApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> addProductSetupApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public String apiCallErrorMessage;
    public boolean profileImageCaptured=false;
    public String encodedString="";

    public ProductSetupViewModel(Application context, Repository repository) {
        super(context);
        this.repository = repository;
    }

    public void init() {
        if (merchant != null) {
            return;
        }

        merchant = repository.getMerchant();
    }

    public void getProfileData() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_MERCHANT);

        authenticator.addParameter(Param.MERCHANT_ID,merchant.getValue().getId());//merchant.getValue().getId()
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

                            saveProfileData(response);

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

    private void saveProfileData(JSONObject merchantObject) {

        try {
            merchantModel = new Merchant();
            merchantModel.setOwnerName(merchantObject.getString("merchantOwnerName"));
            merchantModel.setName(merchantObject.getString("merchantName"));
            if (merchantObject.getString("profilePicUrl").length()>0 && merchantObject.getString("profilePicUrl").startsWith("https"))
            {
                profileImageAlreadyExists=true;
                merchantModel.setProfilePicUrl(merchantObject.getString("profilePicUrl"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void addProduct()
    {
        JSONObject jsonObject = getAddProductJsonRequest();
        if (true) Log.d("CSD", "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_SETUP_PRODUCT);
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

                        addProductSetupApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                //resetMerchantApiCallMessage = response.getString("message");
                                addProductSetupApiCallSuccess.postValue(true);
                            } else {
                                addProductSetupApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            addProductSetupApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD",error.toString());
                error.printStackTrace();
                System.out.println(error.toString());
                addProductSetupApiCallRunning.postValue(false);
                addProductSetupApiCallError.postValue(true);
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

        addProductSetupApiCallRunning.setValue(true);
        addProductSetupApiCallError.setValue(false);
        addProductSetupApiCallSuccess.setValue(false);
    }

    private JSONObject getAddProductJsonRequest() {
        Log.d("CSD","ENCODED STRING: "+encodedString);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("productName", productName);
            jsonObject.put("productPrice",productPrice);
            jsonObject.put("productPicUrl", encodedString);

            /*if (profileImageCaptured){
                jsonObject.put("productPicUrl", encodedString);
                //jsonObject.put("imageUpdated", "true");
            }
            else
            {
                if(profileImageAlreadyExists)
                {
                    if (encodedString==null) { //NOT MODIFYING PROFILE PIC
                       // jsonObject.put("imageUpdated", "false");
                    }
                    else if (encodedString.equals("")) //Delete Case
                    {
                        //jsonObject.put("profilePicUrl", encodedString);
                       // jsonObject.put("imageUpdated", "true");
                    }
                }
                //else
                    //jsonObject.put("imageUpdated", "false"); //Very First Time
            }*/

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("CSD","LINE 252:"+e.toString());
            return null;
        }
    }
}
