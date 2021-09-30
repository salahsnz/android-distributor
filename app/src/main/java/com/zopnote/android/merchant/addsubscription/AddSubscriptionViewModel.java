package com.zopnote.android.merchant.addsubscription;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
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
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSubscriptionViewModel extends AndroidViewModel{
    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AddSubscription";

    private Context context;

    private Repository repository;

    public Map<String, Product> productIdMap;
    public String customerId;
    public String quantity;

    public Calendar startDateCalender;
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();

    public MutableLiveData<Integer> step = new MutableLiveData<>();

    public LiveData<List<Product>> products;
    public LiveData<List<Customer>> customers;
    public LiveData<Merchant> merchant;
    public List<Product> productList;

    public MutableLiveData<Boolean> productsLoading = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();

   /* public MutableLiveData<Boolean> generateInvoiceApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> generateInvoiceApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> generateInvoiceApiCallError = new MutableLiveData<>();*/
    public String apiCallErrorMessage;

    public AddSubscriptionViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;

        productsLoading.setValue(false);
    }

    public void init() {
        if (products != null) {
            return;
        }

        step.setValue(1);

        productIdMap = new HashMap<>();

        productsLoading.setValue(true);
        products = repository.getProducts();
        customers = repository.getCustomers();
        merchant = repository.getMerchant();
        productList = new ArrayList<>();
    }

    public boolean isEdited() {
        if (productIdMap != null && productIdMap.size() > 0) {
            return true;
        }
        return false;
    }

   /* public void generateInvoice() {
        JSONObject jsonObject = getGenerateInvoiceJsonRequest();
        if (true) Log.d("CSD", "GENERATE INVOICE " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_GENERATE_INVOICE);
        authenticator.setBody(jsonObject.toString());

        Log.d("CSD",authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        generateInvoiceApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                generateInvoiceApiCallSuccess.postValue(true);
                            } else {
                                generateInvoiceApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            generateInvoiceApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                generateInvoiceApiRunning.postValue(false);
                generateInvoiceApiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

        generateInvoiceApiRunning.setValue(true);
        generateInvoiceApiCallError.setValue(false);
        generateInvoiceApiCallSuccess.setValue(false);
    }*/

    private JSONObject getGenerateInvoiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customerId);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addSubscription() {
        JSONObject jsonObject = getAddSubscriptionJsonRequest();
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ADD_SUBSCRIPTIONS);
        authenticator.setBody(jsonObject.toString());
        System.out.println(jsonObject.toString());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("RRRResponse "+  response.toString());
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
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

        apiCallRunning.setValue(true);
        apiCallError.setValue(false);
        apiCallSuccess.setValue(false);
    }

    private JSONObject getAddSubscriptionJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customerId);
            jsonObject.put("subscriptions", getSubscriptionsJsonArray());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONArray getSubscriptionsJsonArray() throws JSONException {
        JSONArray subscriptionsArray = new JSONArray();

        for (String key : productIdMap.keySet()) {
            JSONObject subscription = new JSONObject();
            subscription.put("productId", key);

            if(startDateCalender != null) {
                subscription.put("startDate", startDateCalender.getTime().getTime());
            }else{
                subscription.put("startDate", null);
            }
            subscription.put("quantity", quantity);

            subscriptionsArray.put(subscription);
        }
        return subscriptionsArray;
    }
}
