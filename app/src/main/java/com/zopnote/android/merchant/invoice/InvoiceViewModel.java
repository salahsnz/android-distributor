package com.zopnote.android.merchant.invoice;

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
import com.zopnote.android.merchant.analytics.Param;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.DailyIndentInvoice;
import com.zopnote.android.merchant.data.model.DailyIndentSubscription;
import com.zopnote.android.merchant.data.model.DateWiseBills;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nmohideen on 03/02/18.
 */

public class InvoiceViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    private static final String LOG_TAG = "UpdateInvoiceViewModel";
    public String invoiceId;
    public boolean isActionFromInvoiceHistory = false;
    public String customerId;

    private Context context;
    private Repository repository;

    public Double wholeMonthTotalInvAmt = 0D;
    public Double previousBalanceUnpaid = 0D;
    public Double previousMonthUnpaid = 0D;
    public LiveData<Merchant> merchant;
    public LiveData<Customer> customer;
    public LiveData<List<Invoice>> invoices;
    public Invoice latestInvoice;
    public String merchantName;
    public String merchantAddress;
    public String customerName;
    public String customerAddress;
    public Calendar startDateCalender,endDateCalender;
    public long startDate,endDate;
    public String selectedPeriod="This Month";
    public List<DateWiseBills> filteredList;
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> endDateChanged = new MutableLiveData<>();
    public DailyIndentInvoice dailyIndentInvoices;
    public List<DailyIndentSubscription> offeredProductList;
    public Map<String,DailyIndentSubscription> subscriptionMap;
    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();

    public MutableLiveData<Boolean> invoiceItemApiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> invoiceItemApiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> invoiceItemApiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<String> invoicePdfUri = new MutableLiveData<>();
    public String apiCallErrorMessage;

    public InvoiceViewModel(Application context, Repository repository) {
        super(context);
        this.context = context;
        this.repository = repository;
    }

    public void init(String customerId) {
        if (merchant != null) {
            return;
        }
        wholeMonthTotalInvAmt = 0.0;

        dailyIndentInvoices = new DailyIndentInvoice();
        offeredProductList = new ArrayList();
        merchant = repository.getMerchant();
        customer = repository.getCustomer(customerId);
        invoices = repository.getInvoices(customerId);
    }


    public void getInvoiceItems() {
        previousBalanceUnpaid = 0.0;
        previousMonthUnpaid = 0.0;
        wholeMonthTotalInvAmt = 0.0;
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_GET_VIEW_BILL_DISTRIBUTOR);
        Calendar c = Calendar.getInstance();
        authenticator.addParameter(Param.MONTH, String.valueOf(c.get(Calendar.MONTH)));
        authenticator.addParameter(Param.YEAR, String.valueOf(c.get(Calendar.YEAR)));
        authenticator.addParameter(Param.CUSTOMER_ID, customerId);
        authenticator.addParameter(Param.MERCHANT_ID, merchant.getValue().getId());
        System.out.println(authenticator.getUri());
        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (DEBUG) Log.d(LOG_TAG, "response: " + response.toString());
                        System.out.println("Response viewbill" + response.toString());

                        invoiceItemApiCallRunning.postValue(false);

                        saveInvoice(response);

                        invoiceItemApiCallSuccess.postValue(true);

                        invoiceItemApiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("here "+ error.toString());
                invoiceItemApiCallRunning.postValue(false);
                invoiceItemApiCallError.postValue(true);
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

        invoiceItemApiCallRunning.setValue(true);
        invoiceItemApiCallError.setValue(false);
        invoiceItemApiCallSuccess.setValue(false);
        networkError.setValue(false);
    }



    private void saveInvoice(JSONObject response) {
        try {


            if (!(offeredProductList.size() >0)) {
                JSONArray merchant = response.getJSONArray("merchant");
                subscriptionMap = new HashMap<>();
                for (int i = 0; i < merchant.length(); i++) {
                    JSONObject offeredProdItemObject = (JSONObject) merchant.get(i);
                    DailyIndentSubscription subscription = new DailyIndentSubscription();
                    String prodId = offeredProdItemObject.getString("id");
                    subscription.setProductId(prodId);
                    subscription.setProductShortCode(offeredProdItemObject.getString("sName"));
                    subscription.setSubscriptionQuantity(-1);
                    offeredProductList.add(subscription);
                    subscriptionMap.put(prodId,subscription);
                }

                Collections.sort(offeredProductList, new Comparator<DailyIndentSubscription>() {
                    @Override
                    public int compare(DailyIndentSubscription s1, DailyIndentSubscription s2) {
                        return s1.getProductShortCode().compareToIgnoreCase(s2.getProductShortCode());
                    }
                });
            }

            dailyIndentInvoices = new DailyIndentInvoice();

            dailyIndentInvoices.setInvoiceAmount(response.getDouble("invoiceAmount"));

            dailyIndentInvoices.setPreviousAmount(response.getDouble("previousAmount"));

            JSONArray datewiseBills = response.getJSONArray("datewiseBills");
            ArrayList<DateWiseBills> dateWiseBillsList = new ArrayList<>();
            wholeMonthTotalInvAmt = 0.0;
            for (int i = 0; i < datewiseBills.length(); i++) {
                JSONObject datewiseBillsObject = (JSONObject) datewiseBills.get(i);

                DateWiseBills dateWiseBills = new DateWiseBills();

                double dailyTotal  = datewiseBillsObject.getDouble("dailyTotal");
                wholeMonthTotalInvAmt +=dailyTotal;
                dateWiseBills.setDailyTotal(dailyTotal);

                dateWiseBills.setAdvancePaid(datewiseBillsObject.getDouble("advancePaid"));

                dateWiseBills.setIndentDate(datewiseBillsObject.getLong("indentDate"));

                JSONArray indents = datewiseBillsObject.getJSONArray("indents");

                Map<String, DailyIndentSubscription> customerSubsMap = new HashMap<>(subscriptionMap);

                for (int k = 0; k < indents.length(); k++) {
                    DailyIndentSubscription subscription = new DailyIndentSubscription();
                    JSONObject dailySubscriptionObject = (JSONObject) indents.get(k);

                    String prodId = dailySubscriptionObject.getString("productId");
                    subscription.setProductId(prodId);
                    subscription.setProductShortCode(dailySubscriptionObject.getString("shortCode"));
                    subscription.setSubscriptionQuantity(dailySubscriptionObject.getInt("subscriptionQuantity"));
                    subscription.setProductPrice(dailySubscriptionObject.getInt("productPrice"));

                    customerSubsMap.put(prodId,subscription);

                }
                List<DailyIndentSubscription> additionalList = new ArrayList<>(customerSubsMap.values());
                Collections.sort(additionalList, new Comparator<DailyIndentSubscription>() {
                    @Override
                    public int compare(DailyIndentSubscription s1, DailyIndentSubscription s2) {
                        return s1.getProductShortCode().compareToIgnoreCase(s2.getProductShortCode());
                    }
                });

                dateWiseBills.setIndents(additionalList);


                dateWiseBillsList.add(dateWiseBills);
            }

            dailyIndentInvoices.setDatewiseBills(dateWiseBillsList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteInvoice() {
        JSONObject jsonObject = getDeleteInvoiceJsonRequest();
        if (DEBUG) Log.d(LOG_TAG, "request: " + jsonObject.toString());
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_REMOVE_INVOICE);
        authenticator.setBody(jsonObject.toString());

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

    private JSONObject getDeleteInvoiceJsonRequest() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchant.getValue().getId());
            jsonObject.put("customerId", customer.getValue().getId());
            jsonObject.put("invoiceId", latestInvoice.getId());

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
