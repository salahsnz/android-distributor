package com.zopnote.android.merchant.products.editproduct;

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
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProductViewModel extends AndroidViewModel {
    private static String LOG_TAG = EditProductViewModel.class.getSimpleName();
    private static boolean DEBUG = false;

    private Repository repository;
    public LiveData<Merchant> merchant;
    public Product product;
    public boolean addProduct;

    public MutableLiveData<Boolean> updateProductApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateProductApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateProductApiCallError = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public String pricingMode;
    public Double perIssuePrice;

    public EditProductViewModel(@NonNull Application application, Repository repository) {
        super(application);
        this.repository = repository;
    }

    public void init(Product product, boolean addProduct){
        if(merchant != null){
            return;
        }

        merchant = repository.getMerchant();
        this.product = product;
        this.addProduct = addProduct;
    }

    public void updateProduct(JSONObject jsonObject) {
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator;

        if(addProduct){
            authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_PRODUCT);
        }else{
            authenticator = new Authenticator(AppConstants.ENDPOINT_UPDATE_PRODUCT);
            System.out.println(jsonObject.toString());
        }

        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        updateProductApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                updateProductApiCallSuccess.postValue(true);
                            } else {
                                updateProductApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            updateProductApiCallError.postValue(true);
                            apiCallErrorMessage = getApplication().getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateProductApiRunning.postValue(false);
                updateProductApiCallError.postValue(true);
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

        updateProductApiRunning.setValue(true);
        updateProductApiCallError.setValue(false);
        updateProductApiCallSuccess.setValue(false);
    }
}
