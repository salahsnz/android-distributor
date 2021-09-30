package com.zopnote.android.merchant.viewcustomer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zopnote.android.merchant.AppConstants.ENDPOINT_ACCEPT_CASH_PAYMENT;

/**
 * Created by nmohideen on 03/02/18.
 */

public class ViewCustomerViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "AddOnDemandViewModel";

    private Context context;
    private Repository repository;

    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;
    public LiveData<List<Invoice>> invoices;
    public LiveData<List<Subscription>> subscriptions;
    public String latestInvoiceId;
    public String cashPaymentAction;

    public String partAdvanceAmount;

    public Calendar checkOutDateCalender;
    public MutableLiveData<Boolean> checkOutDateChanged = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();

    public String apiCallErrorMessage;

    public MutableLiveData<Boolean> editNotesApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> editNotesApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> editNotesApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> generateInvoiceApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> generateInvoiceApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> generateInvoiceApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> deleteCustomerApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteCustomerApiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteCustomerApiCallSuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> checkoutApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> checkoutApiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> checkoutApiCallSuccess = new MutableLiveData<>();

    public MutableLiveData<Boolean> updateMobileApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateMobileApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> updateMobileApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> releaseCurrentInvoiceApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> releaseCurrentInvoiceApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> releaseCurrentInvoiceApiCallError = new MutableLiveData<>();


    public MutableLiveData<Boolean> partPaymentApiRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> partPaymentApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> partPaymentApiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> refreshApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> refreshApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> refreshApiCallError = new MutableLiveData<>();

    public ViewCustomerViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init(String customerId) {
        if (customer != null) {
            return;
        }

        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);
        invoices = repository.getInvoices(customerId);
        subscriptions = repository.getSubscriptions(customerId);
    }


    public void refreshCustomer() {
        JSONObject jsonObject = postRefreshCustomerJsonRequest();
        if (true) Log.d("CSD", "JSON : " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_REFRESH_CUSTOMER);
        authenticator.setBody(jsonObject.toString());
        System.out.println("API : "+authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "response: " + response.toString());
                        refreshApiCallRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                refreshApiCallSuccess.postValue(true);
                            } else {
                                refreshApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            System.out.println("JsonError Respo" + e);
                            Crashlytics.logException(e);
                            refreshApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Respo" + error);
                refreshApiCallRunning.postValue(false);
                refreshApiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

        refreshApiCallRunning.setValue(true);
        refreshApiCallError.setValue(false);
        refreshApiCallSuccess.setValue(false);
    }
    private JSONObject postRefreshCustomerJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void acceptPayment() {
        JSONObject jsonObject = getAcceptCashPaymentJsonRequest();
        if (true) Log.d("CSD", "JSON : " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ACCEPT_CASH_PAYMENT);
        authenticator.setBody(jsonObject.toString());
        System.out.println("API : "+authenticator.getUri());

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
                            System.out.println("JsonError Respo" + e);
                            Crashlytics.logException(e);
                            apiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Respo" + error);
                apiCallRunning.postValue(false);
                apiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

    public void acceptPartPayment()
    {
        Log.d("CSD","ACCEPT PART PAYMENT");

        JSONObject jsonObject = getAcceptPartPaymentJsonRequest();
        if (true) Log.d("CSD", "JSON : " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ACCEPT_PART_PAYMENT);
        authenticator.setBody(jsonObject.toString());
        System.out.println("API : "+authenticator.getUri());

        Log.d("CSD",authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "response: " + response.toString());
                      /*  try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }*/
                        partPaymentApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                partPaymentApiCallSuccess.postValue(true);
                            } else {
                                partPaymentApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            System.out.println("JsonError Respo" + e);
                            Log.d("CSD",e.toString());
                            Crashlytics.logException(e);
                            partPaymentApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error Respo" + error);
                partPaymentApiRunning.postValue(false);
                partPaymentApiCallError.postValue(true);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
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

        partPaymentApiRunning.setValue(true);
        partPaymentApiCallError.setValue(false);
        partPaymentApiCallSuccess.setValue(false);
    }

    private JSONObject getAcceptCashPaymentJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("invoiceId", latestInvoiceId);
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("action", cashPaymentAction);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getAcceptPartPaymentJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("invoiceId", latestInvoiceId);
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("action", cashPaymentAction);
            jsonObject.put("advanceAmount", partAdvanceAmount);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveNotes(String notes) {
        JSONObject jsonObject = getCustomerNotesJsonRequest(notes);
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_CUSTOMER_NOTES);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "SAVE NOTES " + response.toString());
                        editNotesApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                editNotesApiCallSuccess.postValue(true);
                            } else {
                                editNotesApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            editNotesApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                editNotesApiRunning.postValue(false);
                editNotesApiCallError.postValue(true);
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

        editNotesApiRunning.setValue(true);
        editNotesApiCallError.setValue(false);
        editNotesApiCallSuccess.setValue(false);
    }

    private JSONObject getCustomerNotesJsonRequest(String notes) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("notes", notes);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void generateInvoice() {
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
    }

    private JSONObject getGenerateInvoiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteCustomer() {
        JSONObject jsonObject = getDeleteCustomerJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        // "http://18.139.71.131:8080/ZopnoteWeb/app/v1/deleteCustomer"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_DELETE_CUSTOMER);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        deleteCustomerApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                deleteCustomerApiCallSuccess.postValue(true);
                            } else {
                                deleteCustomerApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            deleteCustomerApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                deleteCustomerApiRunning.postValue(false);
                deleteCustomerApiCallError.postValue(true);
                System.out.println(error);
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

        deleteCustomerApiRunning.setValue(true);
        deleteCustomerApiCallError.setValue(false);
        deleteCustomerApiCallSuccess.setValue(false);
    }

    private JSONObject getDeleteCustomerJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void addCustomerMobileNumber() {
        JSONObject jsonObject = getUpdateCustMobileJsonRequest();
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

    private JSONObject getUpdateCustMobileJsonRequest() {
        try {
            JSONObject object = new JSONObject();

            object.put("merchantId", merchant.getValue().getId());
            object.put("mobileNumber", getDisplayMobileNumber());


            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void checkOut() {
        JSONObject jsonObject = getCheckOutJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        // "http://18.139.71.131:8080/ZopnoteWeb/app/v1/deleteCustomer"
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_CHECK_OUT);
        authenticator.setBody(jsonObject.toString());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        checkoutApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                checkoutApiCallSuccess.postValue(true);
                            } else {
                                checkoutApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            checkoutApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                checkoutApiRunning.postValue(false);
                checkoutApiCallError.postValue(true);
                System.out.println(error);
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

        checkoutApiRunning.setValue(true);
        checkoutApiCallError.setValue(false);
        checkoutApiCallSuccess.setValue(false);
    }

    private JSONObject getCheckOutJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("checkoutDate", String.valueOf(checkOutDateCalender.getTime().getTime()));
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void subscriberView(){
        String url = AppConstants.WEB_APP_URL_1 + getDisplayMobileNumber() + AppConstants.WEB_APP_URL_2;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(i);
    }
    private String getDisplayMobileNumber() {
        return customer.getValue().getMobileNumber().replaceAll("^\\+91", "");
    }

    public void releaseCurrentInvoice()
    {
        Log.d("CSD","MERCHANTID "+ merchant.getValue().getId());
        Log.d("CSD","CUSTOMERID "+customer.getValue().getId());

        JSONObject jsonObject = getReleaseCurrentInvoiceJsonRequest();

        if (true) Log.d("CSD", "request: " + jsonObject.toString());

        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_RELEASE_CURRENT_INVOICE);
        authenticator.setBody(jsonObject.toString());
    
        Log.d("CSD",authenticator.getUri());

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST,
                authenticator.getUri(),
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (true) Log.d("CSD", "response:>>>> " + response.toString());
                        releaseCurrentInvoiceApiRunning.postValue(false);
                        try {
                            String status = response.getString("status");
                            if (status.equalsIgnoreCase("success")) {
                                releaseCurrentInvoiceApiCallSuccess.postValue(true);
                            } else {
                                releaseCurrentInvoiceApiCallError.postValue(true);
                                apiCallErrorMessage = response.getString("errorMessage");
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            releaseCurrentInvoiceApiCallError.postValue(true);
                            apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                            Log.d("CSD","ERROR : "+e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                releaseCurrentInvoiceApiRunning.postValue(false);
                releaseCurrentInvoiceApiCallError.postValue(true);
                System.out.println(error);
                apiCallErrorMessage = context.getResources().getString(R.string.generic_error_message);
                Log.d("CSD","VOLLEY ERROR"+error.toString());
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

        releaseCurrentInvoiceApiRunning.setValue(true);
        releaseCurrentInvoiceApiCallError.setValue(false);
        releaseCurrentInvoiceApiCallSuccess.setValue(false);
    }

    private JSONObject getReleaseCurrentInvoiceJsonRequest() {
        try {
            Log.d("CSD","INSIDE JSON");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());

            return jsonObject;
        } catch (JSONException e) {
            Log.d("CSD",e.toString());
            e.printStackTrace();
            return null;
        }
    }
}
