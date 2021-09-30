package com.zopnote.android.merchant.reports.ordersummarycustomerdetails;

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
import com.android.volley.toolbox.JsonArrayRequest;
import com.zopnote.android.merchant.AppConstants;
import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.OrderSummaryCustomer;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.util.Authenticator;
import com.zopnote.android.merchant.util.FormatUtil;
import com.zopnote.android.merchant.util.VolleyManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderSummaryCustomerDetailsReportViewModel extends AndroidViewModel {

    private static final boolean DEBUG = false;
    public String advanceAmount;
   // private String LOG_TAG = OrderSummaryReportViewModel.class.getSimpleName();
   //public OrderSummaryReport orderSummaryReport;

    private final Repository repository;
    public List<OrderSummaryCustomer> customers;

    public MutableLiveData<Boolean> apiCallRunning = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallError = new MutableLiveData<>();
    public MutableLiveData<Boolean> networkError = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunningReqAdv = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccessReqAdv = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallErrorReqAdv = new MutableLiveData<>();

    public MutableLiveData<Boolean> apiCallRunningSettleNow = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallSuccessSettleNow = new MutableLiveData<>();
    public MutableLiveData<Boolean> apiCallErrorSettleNow = new MutableLiveData<>();

    public String apiCallErrorMessage;
    public String settleNowMessage;
    public String merchantId;
    public String customerId;
    public String invoiceNo;
    public int totalOrders=0;
    public int totalPaidOrders=0;
    public int totalUnPaidOrders=0;
    public double totalBilled=0.0;
    public double totalPaidAmount=0.0;
    public double totalUnPaidAmount=0.0;
    public Calendar startDateCalender,endDateCalender;
    public long startDate,endDate;
    public int month;
    public String monthString;
    public int year;

    public String customerName,customerStatus,addressLine1,addressLine2,mobileNo,doorNo,route,email,
            radioSelected,radioSelectedIndex;

    public String main_radioSelectedIndex,main_startDateSelected,main_endDateSelected,main_radioSelected;
    public String selectedPeriod=""; //CHANGE to TODAY

    public LiveData<Merchant> merchant;

    public MutableLiveData<Boolean> dateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> startDateChanged = new MutableLiveData<>();
    public MutableLiveData<Boolean> endDateChanged = new MutableLiveData<>();

    public OrderSummaryCustomerDetailsReportViewModel(@NonNull Application application, Repository repository) {
        super(application);
        Log.d("CSD","ORDER SUMMARY CD REPORT ACTIVTY  VIEW MODEL");
        this.repository = repository;
    }

    public void init(){
        if(merchant != null){
            return;
        }
        merchant = repository.getMerchant();

        customers = new ArrayList();

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH); //Jan month is 0
        year = calendar.get(Calendar.YEAR);
        monthString = FormatUtil.DATE_FORMAT_MMMM.format(calendar.getTime());
    }

    private void dateToLong()
    {
        int noOfDays=0;
        switch (selectedPeriod)
        {
            case "Today":
                noOfDays=0; //Change to 0

                startDate=createCalendarInstance(noOfDays);
                endDate =new Date().getTime();
                break;
            case "This Week":
                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);

                System.out.println("Date " + c.getTime());
                startDate=c.getTimeInMillis();
                endDate =new Date().getTime();

                break;
            case "Last Week":
                Calendar c1 = Calendar.getInstance();
                c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                c1.set(Calendar.HOUR_OF_DAY, 0);
                c1.set(Calendar.MINUTE, 0);
                c1.set(Calendar.SECOND, 0);
                c1.set(Calendar.MILLISECOND, 0);
                c1.add(Calendar.DATE, -1);//This is Sunday of Last week
                endDate=c1.getTimeInMillis(); //This Last Sunday

                c1.add(Calendar.DATE, -6);//This is Monday of Last week
                startDate=c1.getTimeInMillis();

                Log.d("CSD","LAST WEEK");
                break;
            case "This Month":

                Date d=new Date();
                int i=d.getDate();
                noOfDays=-(i-1);

                startDate=createCalendarInstance(noOfDays);
                endDate =new Date().getTime();

                break;
        }

        Log.d("CSD"," START DATE: "+startDate+" END DATE "+endDate);
    }

    private long createCalendarInstance(int noOfDays)
    {
        Calendar date = Calendar.getInstance();

        //Setting todays date time 00:00
        date.add(Calendar.DATE,noOfDays);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return date.getTimeInMillis();
    }

    public void getOrderSummaryCustomerDetailsReport() {
        final Authenticator authenticator = new Authenticator(AppConstants.ENDPOINT_ORDER_SUMMARY_CUSTOMER_DETAILS_REPORT);

        //If Picked from DatePicker don't set startdate and enddate from here
        Log.d("CSD","SELECT PERIOD>>>> "+selectedPeriod);

        if (selectedPeriod!="Custom")
            dateToLong();

        //merchantId="b93dc1ed-870f-46f1-96c4-eccca021d13f";

        authenticator.addParameter("merchantId", merchantId);
        authenticator.addParameter("customerId", customerId);

        authenticator.addParameter("startDate", String.valueOf(startDate)); //03/01/2020
        authenticator.addParameter("endDate", String.valueOf(endDate));     //11/26/2020

        System.out.println("API : "+authenticator.getUri());
        Log.d("CSD", "OSCD API >>>>>>>"+authenticator.getUri());

        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.GET,
                authenticator.getUri(),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (true)
                             Log.d("CSD","RESPONSE LENGTH : "+response.length());
                        System.out.println(response);
                        apiCallRunning.postValue(false);

                        saveCustomerInvoicesList(response);

                        apiCallSuccess.postValue(true);
                        apiCallError.postValue(false);
                        networkError.postValue(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("onErrorResponse : "+error);
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

    private void saveCustomerInvoicesList(JSONArray response) {
        clearInvoiceData();
        Log.d("CSD","SAVE ORDER SUMMARY REPORT---"+response.length()+"---"+response.toString());

        try {
            for (int j = 0; j < response.length(); j++) {
                String s = response.get(j).toString();

                JSONArray invoiceArray = new JSONArray(s);

                totalOrders=invoiceArray.length();
                Log.d("CSD", "INV ARR LEN" + invoiceArray.length());

                for (int i = 0; i < invoiceArray.length(); i++) {

                    String invoice = invoiceArray.get(i).toString();

                    JSONObject invoiceObj = new JSONObject(invoice);
                    String invoiceNo = invoiceObj.getString("invoiceNumber");
                    String status = invoiceObj.getString("invoiceStatus");
                    String invoiceAmount = invoiceObj.getString("amount");
                    Double invAmt=0.0;
                    invAmt= Double.parseDouble(invoiceAmount);

                    totalBilled=totalBilled+Double.parseDouble(invoiceAmount);

                    String customerId = invoiceObj.getString("customerID");

                    OrderSummaryCustomer c = new OrderSummaryCustomer();

                    c.setCustomerId(customerId);
                    c.setMerchantId(merchantId);
                    c.setTotalOrders(totalOrders);

                    if (status.equals("PAID")) {
                        totalPaidOrders++;
                        totalPaidAmount=totalPaidAmount+invAmt;
                        Log.d ("CSD","PAID Amount"+totalPaidAmount);
                    }
                    else if ((status.equals("OPEN")) || (status.equals("UNPAID")) || (status.equals("EXPIRED")))  {
                        totalUnPaidOrders++;
                        totalUnPaidAmount=totalUnPaidAmount+invAmt;
                        Log.d ("CSD","UN PAID Amount"+totalUnPaidAmount);
                    }

                    c.setInvoiceDate(invoiceObj.getLong("invoiceDate"));
                    c.setInvoiceId((invoiceObj.getString("invoiceID")));
                    c.setInvoiceNumber(invoiceNo);
                    c.setStatus(status);
                    c.setInvoiceAmount(invoiceAmount);
                    customers.add(c);
                }
            }

            // sorting for date wise listing
            Collections.sort(customers, new Comparator<OrderSummaryCustomer>() {

                @Override
                public int compare(OrderSummaryCustomer lhs, OrderSummaryCustomer rhs) {
                    return lhs.getInvoiceDate().compareTo(rhs.getInvoiceDate());
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
            Log.d("CSD", "ERROR : "+ex.toString());
        }
    }

    private void clearInvoiceData()
    {
        customers.clear();
        totalOrders=0;
        totalPaidOrders=0;
        totalUnPaidOrders=0;
        totalBilled=0.0;
        totalPaidAmount=0.0;
        totalUnPaidAmount=0.0;
    }
}
