package com.zopnote.android.merchant;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.zopnote.android.merchant.util.Installation;

public class AppConstants {

    //TODO: whats the use of DEBUG_KEY
    public static final String DEBUG_KEY = "f0afbd8fd5744c89989d62343e9e6187";

    //endpoints
    private static final String API_VERSION = "/app/v1";
    public static final String ENDPOINT_GCM_REGISTER = BuildConfig.API_ENDPOINT + API_VERSION + "/gcmRegister";
    public static final String ENDPOINT_INIT = BuildConfig.API_ENDPOINT + API_VERSION + "/init";
    public static final String ENDPOINT_INIT_M = BuildConfig.API_ENDPOINT + API_VERSION + "/initM";
    public static final String ENDPOINT_ADD_CUSTOMER = BuildConfig.API_ENDPOINT + API_VERSION + "/addCustomer";
    public static final String ENDPOINT_UPDATE_CUSTOMER_PROFILE = BuildConfig.API_ENDPOINT + API_VERSION + "/updateCustomer";
    public static final String ENDPOINT_DELETE_CUSTOMER = BuildConfig.API_ENDPOINT + API_VERSION + "/deleteCustomer";
    public static final String ENDPOINT_ACCEPT_CASH_PAYMENT = BuildConfig.API_ENDPOINT + API_VERSION + "/acceptCashPaymentV2";
    public static final String ENDPOINT_ACCEPT_PART_PAYMENT = BuildConfig.API_ENDPOINT + API_VERSION + "/acceptVendorPartPayment";
    public static final String ENDPOINT_ADD_PAUSE = BuildConfig.API_ENDPOINT + API_VERSION + "/addPauseV2";
    public static final String ENDPOINT_DELETE_NOTIFICATION = BuildConfig.API_ENDPOINT + API_VERSION + "/deleteNotifications";

    public static final String ENDPOINT_ADD_CUSTOMER_MOBILE = BuildConfig.API_ENDPOINT + API_VERSION + "/addCustomerMobileNumber";
    public static final String ENDPOINT_RESET_MERCHANT = BuildConfig.API_ENDPOINT + API_VERSION + "/resetMerchant";
    public static final String ENDPOINT_UPDATE_PAUSE = BuildConfig.API_ENDPOINT + API_VERSION + "/updatePause";;
    public static final String ENDPOINT_DELETE_PAUSE = BuildConfig.API_ENDPOINT + API_VERSION + "/removePause";
    public static final String ENDPOINT_SEND_OTP = BuildConfig.API_ENDPOINT + API_VERSION + "/sendOTP";
    public static final String ENDPOINT_RE_SEND_OTP = BuildConfig.API_ENDPOINT + API_VERSION + "/resendOTP";
    public static final String ENDPOINT_VERIFY_OTP = BuildConfig.API_ENDPOINT + API_VERSION + "/verifyOTP";

    public static final String ENDPOINT_MERCHANT = BuildConfig.API_ENDPOINT + API_VERSION + "/getMerchant";
    public static final String ENDPOINT_ADD_MERCHANT = BuildConfig.API_ENDPOINT + API_VERSION + "/addMerchant";
    public static final String ENDPOINT_SETUP_PRODUCT = BuildConfig.API_ENDPOINT + API_VERSION + "/setupProduct";
    public static final String ENDPOINT_ACTIVATE_MERCHANT = BuildConfig.API_ENDPOINT + API_VERSION + "/activateMerchant";
    public static final String ENDPOINT_ADD_SUBSCRIPTIONS = BuildConfig.API_ENDPOINT + API_VERSION + "/addVendorSubscriptions";
    public static final String ENDPOINT_UPDATE_SUBSCRIPTIONS = BuildConfig.API_ENDPOINT + API_VERSION + "/updateSubscriptions";
    public static final String ENDPOINT_REMOVE_SUBSCRIPTIONS = BuildConfig.API_ENDPOINT + API_VERSION + "/removeSubscriptions";
    public static final String ENDPOINT_CUSTOMIZE_SUBSCRIPTION = BuildConfig.API_ENDPOINT + API_VERSION + "/customizeSubscriptions";

    public static final String ENDPOINT_UPDATE_ROUTE_SEQUENCE = BuildConfig.API_ENDPOINT + API_VERSION + "/updateRouteSequence";
    public static final String ENDPOINT_COLLECTION_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getCollectionReport";
    public static final String ENDPOINT_SUBSCRIPTIONS_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getSubscriptionsReport";
    public static final String ENDPOINT_REFRESH_CUSTOMER = BuildConfig.API_ENDPOINT + API_VERSION + "/refreshCustomer";

    public static final String ENDPOINT_CUSTOMER_NOTES = BuildConfig.API_ENDPOINT + API_VERSION + "/editCustomerNotes";
    public static final String ENDPOINT_GENERATE_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/generateVendorInvoice";
    public static final String ENDPOINT_UPDATE_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/updateInvoice";
    public static final String ENDPOINT_UPDATE_DIST_INDENT = BuildConfig.API_ENDPOINT + API_VERSION + "/updateDistributorIndent";
    public static final String ENDPOINT_GET_DIST_INDENT = BuildConfig.API_ENDPOINT + API_VERSION + "/getIndentByDistributor";
    public static final String ENDPOINT_GET_VIEW_BILL_DISTRIBUTOR = BuildConfig.API_ENDPOINT + API_VERSION + "/getBillByDistributorCustomer";
    public static final String ENDPOINT_REMOVE_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/removeInvoice";
    public static final String ENDPOINT_GET_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/getInvoice";
    public static final String ENDPOINT_GET_DRAFT_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/getDraftInvoice";
    public static final String ENDPOINT_DRAFT_INVOICE_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getDraftInvoiceReport";
    public static final String ENDPOINT_DELETE_CUST_SUBSCRIPTION = BuildConfig.API_ENDPOINT + API_VERSION + "/deleteCustomizedSubscription";
    public static final String ENDPOINT_CHECK_OUT = BuildConfig.API_ENDPOINT + API_VERSION + "/checkOutCustomer";
    public static final String ENDPOINT_RELEASE_CURRENT_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/releaseInvoice";

    public static final String ENDPOINT_ADD_ROUTE = BuildConfig.API_ENDPOINT + API_VERSION + "/addRoute";
    public static final String ENDPOINT_ADD_AREA = BuildConfig.API_ENDPOINT + API_VERSION + "/addArea";

    public static final String ENDPOINT_INDENT_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getIndentReport";
    public static final String ENDPOINT_SETTLEMENT_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getSettlementReport";
    public static final String ENDPOINT_ORDER_SUMMARY_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getOrderSummaryReport";
    public static final String ENDPOINT_ORDER_SUMMARY_CUSTOMER_DETAILS_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getOrderDetailReport";
    public static final String ENDPOINT_NOTIFICATION = BuildConfig.API_ENDPOINT + API_VERSION + "/getNotification";
    public static final String ENDPOINT_INVOICE_REPORT = BuildConfig.API_ENDPOINT + API_VERSION + "/getInvoiceReportV2";
    //public static final String ENDPOINT_INVOICE_PAYOUT = BuildConfig.API_ENDPOINT + API_VERSION + "/payOut";
    public static final String ENDPOINT_INVOICE_PAYOUT = BuildConfig.API_ENDPOINT + API_VERSION + "/payOutV2";
    public static final String ENDPOINT_ADMIN_UPDATE_INVOICE = BuildConfig.API_ENDPOINT + API_VERSION + "/adminUpdateInvoice";
    public static final String ENDPOINT_ON_DEMAND_ITEMS = BuildConfig.API_ENDPOINT + API_VERSION + "/getOnDemandItems";
    public static final String ENDPOINT_ADD_ON_DEMAND_ITEMS = BuildConfig.API_ENDPOINT + API_VERSION + "/addOndemandItem";
    public static final String ENDPOINT_SUBMIT_AGREEMENT = BuildConfig.API_ENDPOINT + API_VERSION + "/submitAgreement";
    public static final String ENDPOINT_REGISTER_MERCHANT = BuildConfig.API_ENDPOINT + API_VERSION + "/registerMerchant";
    public static final String ENDPOINT_ADD_PRODUCT = BuildConfig.API_ENDPOINT + API_VERSION + "/addProduct";
    public static final String ENDPOINT_UPDATE_PRODUCT = BuildConfig.API_ENDPOINT + API_VERSION + "/updateVendorProduct";
    public static final String ENDPOINT_SEND_SMS_REMAINDER = BuildConfig.API_ENDPOINT + API_VERSION + "/sendSMSReminder";
    public static final String ENDPOINT_ADD_MERCHANT_ADVANCE = BuildConfig.API_ENDPOINT + API_VERSION + "/addMerchantAdvance";
    public static final String ENDPOINT_PROCESS_SETTLEMENT = BuildConfig.API_ENDPOINT + API_VERSION + "/processSettlement";
    public static final String ENDPOINT_SEND_PUSH_NOTIFICATION = BuildConfig.API_ENDPOINT + API_VERSION + "/sendPushNotification";
    public static final String GOOGLE_PLAY_APP_LINK = "https://play.google.com/store/apps/details?id=com.zopnote.android.merchant&referrer=utm_source%3Dzopnote_app%26utm_medium%3Dshare";
    public static final String WEBSITE_HOST = "www.zopnote.com";
    public static final String GOOGLE_PLAY_APP_LINK_BITLY = "http://bit.ly/zopnote-app-share";
    public static final String PRIVACY_URL = "http://www.zopnote.com/privacy.html";
    public static final String TOS_URL = "http://www.zopnote.com/tos.html";
    public static final String AGREEMENT_URL = "http://www.zopnote.com/agreement.html";

    public static final String WEB_APP_URL_1 = "https://www.zopnote.com/testlogin?mobileNumber=";
    public static final String WEB_APP_URL_2 = "&key=7b3f8420-201a-4474-b290-99154f899980";

    public static final String ATTR_DEBUG = "debug";
    public static final String ATTR_KEY = "key";
    public static final String ATTR_AIFA = "aifa";
    public static final String ATTR_UID = "uid";
    public static final String ATTR_APP_VERSION = "appVersion";
    public static final String ATTR_GCM_TOKEN = "gcmToken";
    public static final String ATTR_FIREBASE_PROJECT_ID = "firebaseId";
    public static final String ATTR_FIREBASE_TOKEN = "firebaseToken";
    public static final String ATTR_MOBILE_NUMBER = "mobileNumber";
    public static final String ATTR_ACCOUNT_KIT_AUTHCODE = "accountKitAuthCode";
    public static final String ATTR_IS_NEW_USER = "isNewUser";
    public static final String ATTR_MERCHANT_STATUS = "merchantStatus";
    public static final String ATTR_ADV_AMOUNT = "advanceAmount";
    public static final String ATTR_MERCHANT_ROUTE = "merchantRoute";
    public static final String ATTR_MERCHANT_UPDATE="merchantUpdate";

    public static final String PREFS_NAME = "com.zopnote.android.prefs";
    public static final String PREFS_APP_INIT_COMPLETE = "aic";
    public static final String PREFS_ACCEPT_MERCHANT_AGREMENT = "ama";
    public static final String PREFS_APP_UPDATE_CONTENT = "auco";
    public static final String PREFS_APP_UPDATE_CAMPAIGN = "auca";
    public static final String PREFS_SIGNED_IN_MOBILE_NUMBER = "pmn";
    public static final String PREFS_CURRENT_INITIALIZED_APP_VERSION = "ciav";
    public static final String PREFS_LAST_USED_ROUTE = "lur";
    public static final String PREFS_LAST_USED_ADDRESS_LINE1 = "luadl1";
    public static final String PREFS_LAST_USED_ADDRESS_LINE2 = "luadl2";
    public static final String PREFS_INDENT_SWITCH_IS_CHECKED = "isic";
    public static final String PREFS_NOTIFICATION_INIT_COUNT = "nic";
    public static final String PREF_VENDOR_UID = "vuid";
    public static final String PREFS_DRAFT_INVOICE_SWITCH_IS_CHECKED = "disc";


    public static final int REMOTE_CONFIG_CACHE_SECONDS = 60 * 30; // 30 minutes in seconds
    public static final String REMOTE_CONFIG_A_GROUP = "admin_group";
    public static final String REMOTE_CONFIG_LATEST_APP_VERSION_CODE = "latest_app_version_code";

    public static final String FEATURE_INTRO_ID_SUBSCRIPTION_FILTER = "feature_intro_id_subscription_filter";

    public static final String NOTIFICATION_SINGLE ="single";
    public static final String NOTIFICATION_EVERYONE ="broadcast";


    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should not reach here
            return "";
        }
    }

    public static int getAppVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should not reach here
            return 0;
        }
    }

    public static String getSupportMailtoLink(Context context) {
        return "mailto:" + context.getResources().getString(com.zopnote.android.merchant.R.string.support_email);
    }

    public static String getFeedbackMailSubject(Context context) {
        return String.format("Android app feedback [%s ]",
                getAppVersionName(context));
    }

    public static Intent getAppShareIntent(Context context) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(com.zopnote.android.merchant.R.string.share_app_title));
        StringBuilder shareTextBuilder = new StringBuilder();
        shareTextBuilder.append(context.getResources().getString(com.zopnote.android.merchant.R.string.share_app_text));
        shareTextBuilder.append("\n\n");
        shareTextBuilder.append(GOOGLE_PLAY_APP_LINK_BITLY);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareTextBuilder.toString());
        return sharingIntent;
    }

    public static String getUserId(Context context) {
        return Installation.getUid(context);
    }
}
