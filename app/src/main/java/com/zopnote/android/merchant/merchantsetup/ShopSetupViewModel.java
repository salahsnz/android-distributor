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
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by KISHORE on 12/15/2020.
 */

public class ShopSetupViewModel extends AndroidViewModel {

    private String LOG_TAG = ShopSetupViewModel.class.getSimpleName();
    private Repository repository;
    public String merchantId;
    public String route;
    public String merchantBusinessName;
    public String serviceType="OnDemand";  //Default
    public String merchantBilling ="Monthly";  //Default
    public String merchantBillingType ="Yes";  //Default No for Pre-paid and Yes for Post-Paid
    public Merchant merchantModel;
    public boolean profileImageAlreadyExists=false;

    public LiveData<Merchant> merchant;
    public Product product;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> addShopSetupApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> addShopSetupApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> addShopSetupApiCallError = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public ShopSetupViewModel(Application context, Repository repository) {
        super(context);
        Log.d("CSD","SHOP SETUP VIEW MODEL");
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

        authenticator.addParameter(Param.MERCHANT_ID,merchant.getValue().getId() );
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

                            setShopSetup(response);

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

    private void setShopSetup(JSONObject merchantObject) {

        try {
            merchantModel = new Merchant();
            product = new Product();

            merchantModel.setOwnerName(merchantObject.getString("merchantOwnerName"));
            merchantModel.setName(merchantObject.getString("merchantName"));

            if (merchantObject.has("productList"))
            {
                try {
                    JSONArray jsonArray = new JSONArray(merchantObject.getString("productList"));

                    Log.d("CSD", "KKKKKK:" + jsonArray.getString(0));
                    product.setName(jsonArray.getString(0));
                }catch (Exception e)
                {
                    Log.d("CSD",e.toString());
                }
            }
            if (merchantObject.getString("profilePicUrl").length()>0 && merchantObject.getString("profilePicUrl").startsWith("https"))
            {
                profileImageAlreadyExists=true;
                merchantModel.setProfilePicUrl(merchantObject.getString("profilePicUrl"));
            }
            if (merchantObject.has("type"))
            {
                product.setType(merchantObject.getString("type"));
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.d("CSD","setShopSetup "+e.toString());
        }
    }

    public void submitService()
    {
        JSONObject jsonObject = getAddServiceJsonRequest();
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

                        addShopSetupApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                //resetMerchantApiCallMessage = response.getString("message");
                                addShopSetupApiCallSuccess.postValue(true);
                            } else {
                                addShopSetupApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            addShopSetupApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message); //94482244542
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("CSD",error.toString());
                error.printStackTrace();
                System.out.println(error.toString());
                addShopSetupApiCallRunning.postValue(false);
                addShopSetupApiCallError.postValue(true);
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

        addShopSetupApiCallRunning.setValue(true);
        addShopSetupApiCallError.setValue(false);
        addShopSetupApiCallSuccess.setValue(false);
    }

    private JSONObject getAddServiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put( AppConstants.ATTR_MERCHANT_UPDATE,"shopSetup");
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("merchantBilling",merchantBilling);
            jsonObject.put("type", serviceType);
            jsonObject.put("merchantPostPaid",merchantBillingType);
            jsonObject.put("merchantName", merchantBusinessName);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
