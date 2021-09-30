package com.zopnote.android.merchant;

import android.databinding.DataBinderMapper;
import android.databinding.DataBindingComponent;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import com.zopnote.android.merchant.databinding.ActivateMerchantFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddAreaFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddCustomerActBindingImpl;
import com.zopnote.android.merchant.databinding.AddCustomizationActBindingImpl;
import com.zopnote.android.merchant.databinding.AddCustomizationFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddDeliveryDaysAndPriceItemBindingImpl;
import com.zopnote.android.merchant.databinding.AddDeliveryDaysItemBindingImpl;
import com.zopnote.android.merchant.databinding.AddOndemandItemFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddPriceItemBindingImpl;
import com.zopnote.android.merchant.databinding.AddProductActBindingImpl;
import com.zopnote.android.merchant.databinding.AddProductFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddProductItemBindingImpl;
import com.zopnote.android.merchant.databinding.AddRouteFragBindingImpl;
import com.zopnote.android.merchant.databinding.AddSubscriptionActBindingImpl;
import com.zopnote.android.merchant.databinding.AdvancePaymentBindingImpl;
import com.zopnote.android.merchant.databinding.BillImagePreviewFragBindingImpl;
import com.zopnote.android.merchant.databinding.CheckOutDateFragBindingImpl;
import com.zopnote.android.merchant.databinding.CollectionActBindingImpl;
import com.zopnote.android.merchant.databinding.CollectionFragBindingImpl;
import com.zopnote.android.merchant.databinding.CollectionItemBindingImpl;
import com.zopnote.android.merchant.databinding.CollectionSummaryFragBindingImpl;
import com.zopnote.android.merchant.databinding.CustomProductItemsBindingImpl;
import com.zopnote.android.merchant.databinding.CustomSpinnerViewBindingImpl;
import com.zopnote.android.merchant.databinding.CustomerItemBindingImpl;
import com.zopnote.android.merchant.databinding.CustomerSubscriptionItemBindingImpl;
import com.zopnote.android.merchant.databinding.CustomersEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.CustomersFragBindingImpl;
import com.zopnote.android.merchant.databinding.DailyIndentActBindingImpl;
import com.zopnote.android.merchant.databinding.DailyIndentFragBindingImpl;
import com.zopnote.android.merchant.databinding.DailyIndentItemBindingImpl;
import com.zopnote.android.merchant.databinding.DateFragBindingImpl;
import com.zopnote.android.merchant.databinding.DatewiseInvoiceItemItemBindingImpl;
import com.zopnote.android.merchant.databinding.DoorNumberFragBindingImpl;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportActBindingImpl;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportFragBindingImpl;
import com.zopnote.android.merchant.databinding.DraftInvoiceReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.DraftInvoicesEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.EditCustomerActBindingImpl;
import com.zopnote.android.merchant.databinding.EditCustomerContentBindingImpl;
import com.zopnote.android.merchant.databinding.EditDraftInvoiceActBindingImpl;
import com.zopnote.android.merchant.databinding.EditDraftInvoiceFragBindingImpl;
import com.zopnote.android.merchant.databinding.EditInvoiceActBindingImpl;
import com.zopnote.android.merchant.databinding.EditPauseActBindingImpl;
import com.zopnote.android.merchant.databinding.EditProductActBindingImpl;
import com.zopnote.android.merchant.databinding.EditProductFragBindingImpl;
import com.zopnote.android.merchant.databinding.EditSubscriptionActBindingImpl;
import com.zopnote.android.merchant.databinding.HomeFragBindingImpl;
import com.zopnote.android.merchant.databinding.InboxEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.InboxFragBindingImpl;
import com.zopnote.android.merchant.databinding.InboxItemBindingImpl;
import com.zopnote.android.merchant.databinding.IndentActBindingImpl;
import com.zopnote.android.merchant.databinding.IndentEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.IndentFragBindingImpl;
import com.zopnote.android.merchant.databinding.IndentItemBindingImpl;
import com.zopnote.android.merchant.databinding.IntroPageBindingImpl;
import com.zopnote.android.merchant.databinding.InvoiceFragBindingImpl;
import com.zopnote.android.merchant.databinding.InvoiceHistoryActBindingImpl;
import com.zopnote.android.merchant.databinding.InvoiceNotFoundErrorBindingImpl;
import com.zopnote.android.merchant.databinding.InvoicehistoryFragBindingImpl;
import com.zopnote.android.merchant.databinding.InvoicehistoryItemBindingImpl;
import com.zopnote.android.merchant.databinding.LoadingViewBindingImpl;
import com.zopnote.android.merchant.databinding.LocationFragBindingImpl;
import com.zopnote.android.merchant.databinding.LoginActBindingImpl;
import com.zopnote.android.merchant.databinding.ManageSubscriptionItemBindingImpl;
import com.zopnote.android.merchant.databinding.ManageSubscriptionsFragBindingImpl;
import com.zopnote.android.merchant.databinding.MerchantBankInfoFragBindingImpl;
import com.zopnote.android.merchant.databinding.MerchantKycFragBindingImpl;
import com.zopnote.android.merchant.databinding.MerchantProfileFragBindingImpl;
import com.zopnote.android.merchant.databinding.MoveCustomerActBindingImpl;
import com.zopnote.android.merchant.databinding.MoveCustomerFragBindingImpl;
import com.zopnote.android.merchant.databinding.NetworkErrorBindingImpl;
import com.zopnote.android.merchant.databinding.NoContentAvailableBindingImpl;
import com.zopnote.android.merchant.databinding.NoContentErrorBindingImpl;
import com.zopnote.android.merchant.databinding.NotificationDashboardActBindingImpl;
import com.zopnote.android.merchant.databinding.OnboardActBindingImpl;
import com.zopnote.android.merchant.databinding.OnboardFragBindingImpl;
import com.zopnote.android.merchant.databinding.OnboardReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryCdSelectionBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsReportActBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryCustomerDetailsTotalBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryDetailsLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryReportActBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummarySelectionBindingImpl;
import com.zopnote.android.merchant.databinding.OrderSummaryTotalBindingImpl;
import com.zopnote.android.merchant.databinding.OrdersummaryCustomerDetailsReportFragBindingImpl;
import com.zopnote.android.merchant.databinding.OrdersummaryReportFragBindingImpl;
import com.zopnote.android.merchant.databinding.PauseItemBindingImpl;
import com.zopnote.android.merchant.databinding.PauseSubscriptionDialogBindingImpl;
import com.zopnote.android.merchant.databinding.PaymentReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.PaymentsActBindingImpl;
import com.zopnote.android.merchant.databinding.PaymentsEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.PaymentsFragBindingImpl;
import com.zopnote.android.merchant.databinding.ProductItemBindingImpl;
import com.zopnote.android.merchant.databinding.ProductSetupFragBindingImpl;
import com.zopnote.android.merchant.databinding.ProductsActBindingImpl;
import com.zopnote.android.merchant.databinding.ProductsEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.ProductsFragBindingImpl;
import com.zopnote.android.merchant.databinding.ProfileFragBindingImpl;
import com.zopnote.android.merchant.databinding.RegisterActBindingImpl;
import com.zopnote.android.merchant.databinding.ReportsActivityBindingImpl;
import com.zopnote.android.merchant.databinding.ReviewFragBindingImpl;
import com.zopnote.android.merchant.databinding.ReviewSubscriptionFragBindingImpl;
import com.zopnote.android.merchant.databinding.SearchActBindingImpl;
import com.zopnote.android.merchant.databinding.SearchResultsEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.SettlementAmountBreakupLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.SettlementDetailsLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.SettlementReportActBindingImpl;
import com.zopnote.android.merchant.databinding.SettlementReportFragBindingImpl;
import com.zopnote.android.merchant.databinding.SettlementReportItemBindingImpl;
import com.zopnote.android.merchant.databinding.ShopSetupFragBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionItemBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionReportPauseItemBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionReportSubscriptionItemBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsFragBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsReportActBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsReportFragBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsReportItemContentBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsReportItemHeaderBindingImpl;
import com.zopnote.android.merchant.databinding.SubscriptionsReportItemStickyHeaderBindingImpl;
import com.zopnote.android.merchant.databinding.UpdateInvoiceActBindingImpl;
import com.zopnote.android.merchant.databinding.VendorItemBindingImpl;
import com.zopnote.android.merchant.databinding.VendorsActBindingImpl;
import com.zopnote.android.merchant.databinding.VendorsEmptyViewBindingImpl;
import com.zopnote.android.merchant.databinding.ViewCustomerFragBindingImpl;
import com.zopnote.android.merchant.databinding.ViewCustomizationActBindingImpl;
import com.zopnote.android.merchant.databinding.ViewCustomizationFragBindingImpl;
import com.zopnote.android.merchant.databinding.ViewDeliveryDaysAndPricingLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.ViewPostpaidCustomizationLayoutBindingImpl;
import com.zopnote.android.merchant.databinding.ViewPrepaidCustomizationLayoutBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVATEMERCHANTFRAG = 1;

  private static final int LAYOUT_ADDAREAFRAG = 2;

  private static final int LAYOUT_ADDCUSTOMERACT = 3;

  private static final int LAYOUT_ADDCUSTOMIZATIONACT = 4;

  private static final int LAYOUT_ADDCUSTOMIZATIONFRAG = 5;

  private static final int LAYOUT_ADDDELIVERYDAYSANDPRICEITEM = 6;

  private static final int LAYOUT_ADDDELIVERYDAYSITEM = 7;

  private static final int LAYOUT_ADDONDEMANDITEMFRAG = 8;

  private static final int LAYOUT_ADDPRICEITEM = 9;

  private static final int LAYOUT_ADDPRODUCTACT = 10;

  private static final int LAYOUT_ADDPRODUCTFRAG = 11;

  private static final int LAYOUT_ADDPRODUCTITEM = 12;

  private static final int LAYOUT_ADDROUTEFRAG = 13;

  private static final int LAYOUT_ADDSUBSCRIPTIONACT = 14;

  private static final int LAYOUT_ADVANCEPAYMENT = 15;

  private static final int LAYOUT_BILLIMAGEPREVIEWFRAG = 16;

  private static final int LAYOUT_CHECKOUTDATEFRAG = 17;

  private static final int LAYOUT_COLLECTIONACT = 18;

  private static final int LAYOUT_COLLECTIONFRAG = 19;

  private static final int LAYOUT_COLLECTIONITEM = 20;

  private static final int LAYOUT_COLLECTIONSUMMARYFRAG = 21;

  private static final int LAYOUT_CUSTOMPRODUCTITEMS = 22;

  private static final int LAYOUT_CUSTOMSPINNERVIEW = 23;

  private static final int LAYOUT_CUSTOMERITEM = 24;

  private static final int LAYOUT_CUSTOMERSUBSCRIPTIONITEM = 25;

  private static final int LAYOUT_CUSTOMERSEMPTYVIEW = 26;

  private static final int LAYOUT_CUSTOMERSFRAG = 27;

  private static final int LAYOUT_DAILYINDENTACT = 28;

  private static final int LAYOUT_DAILYINDENTFRAG = 29;

  private static final int LAYOUT_DAILYINDENTITEM = 30;

  private static final int LAYOUT_DATEFRAG = 31;

  private static final int LAYOUT_DATEWISEINVOICEITEMITEM = 32;

  private static final int LAYOUT_DOORNUMBERFRAG = 33;

  private static final int LAYOUT_DRAFTINVOICEREPORTACT = 34;

  private static final int LAYOUT_DRAFTINVOICEREPORTFRAG = 35;

  private static final int LAYOUT_DRAFTINVOICEREPORTITEM = 36;

  private static final int LAYOUT_DRAFTINVOICESEMPTYVIEW = 37;

  private static final int LAYOUT_EDITCUSTOMERACT = 38;

  private static final int LAYOUT_EDITCUSTOMERCONTENT = 39;

  private static final int LAYOUT_EDITDRAFTINVOICEACT = 40;

  private static final int LAYOUT_EDITDRAFTINVOICEFRAG = 41;

  private static final int LAYOUT_EDITINVOICEACT = 42;

  private static final int LAYOUT_EDITPAUSEACT = 43;

  private static final int LAYOUT_EDITPRODUCTACT = 44;

  private static final int LAYOUT_EDITPRODUCTFRAG = 45;

  private static final int LAYOUT_EDITSUBSCRIPTIONACT = 46;

  private static final int LAYOUT_HOMEFRAG = 47;

  private static final int LAYOUT_INBOXEMPTYVIEW = 48;

  private static final int LAYOUT_INBOXFRAG = 49;

  private static final int LAYOUT_INBOXITEM = 50;

  private static final int LAYOUT_INDENTACT = 51;

  private static final int LAYOUT_INDENTEMPTYVIEW = 52;

  private static final int LAYOUT_INDENTFRAG = 53;

  private static final int LAYOUT_INDENTITEM = 54;

  private static final int LAYOUT_INTROPAGE = 55;

  private static final int LAYOUT_INVOICEFRAG = 56;

  private static final int LAYOUT_INVOICEHISTORYACT = 57;

  private static final int LAYOUT_INVOICENOTFOUNDERROR = 58;

  private static final int LAYOUT_INVOICEHISTORYFRAG = 59;

  private static final int LAYOUT_INVOICEHISTORYITEM = 60;

  private static final int LAYOUT_LOADINGVIEW = 61;

  private static final int LAYOUT_LOCATIONFRAG = 62;

  private static final int LAYOUT_LOGINACT = 63;

  private static final int LAYOUT_MANAGESUBSCRIPTIONITEM = 64;

  private static final int LAYOUT_MANAGESUBSCRIPTIONSFRAG = 65;

  private static final int LAYOUT_MERCHANTBANKINFOFRAG = 66;

  private static final int LAYOUT_MERCHANTKYCFRAG = 67;

  private static final int LAYOUT_MERCHANTPROFILEFRAG = 68;

  private static final int LAYOUT_MOVECUSTOMERACT = 69;

  private static final int LAYOUT_MOVECUSTOMERFRAG = 70;

  private static final int LAYOUT_NETWORKERROR = 71;

  private static final int LAYOUT_NOCONTENTAVAILABLE = 72;

  private static final int LAYOUT_NOCONTENTERROR = 73;

  private static final int LAYOUT_NOTIFICATIONDASHBOARDACT = 74;

  private static final int LAYOUT_ONBOARDACT = 75;

  private static final int LAYOUT_ONBOARDFRAG = 76;

  private static final int LAYOUT_ONBOARDREPORTITEM = 77;

  private static final int LAYOUT_ORDERSUMMARYCDSELECTION = 78;

  private static final int LAYOUT_ORDERSUMMARYCUSTOMERDETAILSLAYOUT = 79;

  private static final int LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTACT = 80;

  private static final int LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTITEM = 81;

  private static final int LAYOUT_ORDERSUMMARYCUSTOMERDETAILSTOTAL = 82;

  private static final int LAYOUT_ORDERSUMMARYDETAILSLAYOUT = 83;

  private static final int LAYOUT_ORDERSUMMARYREPORTACT = 84;

  private static final int LAYOUT_ORDERSUMMARYREPORTITEM = 85;

  private static final int LAYOUT_ORDERSUMMARYSELECTION = 86;

  private static final int LAYOUT_ORDERSUMMARYTOTAL = 87;

  private static final int LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTFRAG = 88;

  private static final int LAYOUT_ORDERSUMMARYREPORTFRAG = 89;

  private static final int LAYOUT_PAUSEITEM = 90;

  private static final int LAYOUT_PAUSESUBSCRIPTIONDIALOG = 91;

  private static final int LAYOUT_PAYMENTREPORTITEM = 92;

  private static final int LAYOUT_PAYMENTSACT = 93;

  private static final int LAYOUT_PAYMENTSEMPTYVIEW = 94;

  private static final int LAYOUT_PAYMENTSFRAG = 95;

  private static final int LAYOUT_PRODUCTITEM = 96;

  private static final int LAYOUT_PRODUCTSETUPFRAG = 97;

  private static final int LAYOUT_PRODUCTSACT = 98;

  private static final int LAYOUT_PRODUCTSEMPTYVIEW = 99;

  private static final int LAYOUT_PRODUCTSFRAG = 100;

  private static final int LAYOUT_PROFILEFRAG = 101;

  private static final int LAYOUT_REGISTERACT = 102;

  private static final int LAYOUT_REPORTSACTIVITY = 103;

  private static final int LAYOUT_REVIEWFRAG = 104;

  private static final int LAYOUT_REVIEWSUBSCRIPTIONFRAG = 105;

  private static final int LAYOUT_SEARCHACT = 106;

  private static final int LAYOUT_SEARCHRESULTSEMPTYVIEW = 107;

  private static final int LAYOUT_SETTLEMENTAMOUNTBREAKUPLAYOUT = 108;

  private static final int LAYOUT_SETTLEMENTDETAILSLAYOUT = 109;

  private static final int LAYOUT_SETTLEMENTREPORTACT = 110;

  private static final int LAYOUT_SETTLEMENTREPORTFRAG = 111;

  private static final int LAYOUT_SETTLEMENTREPORTITEM = 112;

  private static final int LAYOUT_SHOPSETUPFRAG = 113;

  private static final int LAYOUT_SUBSCRIPTIONITEM = 114;

  private static final int LAYOUT_SUBSCRIPTIONREPORTPAUSEITEM = 115;

  private static final int LAYOUT_SUBSCRIPTIONREPORTSUBSCRIPTIONITEM = 116;

  private static final int LAYOUT_SUBSCRIPTIONSFRAG = 117;

  private static final int LAYOUT_SUBSCRIPTIONSREPORTACT = 118;

  private static final int LAYOUT_SUBSCRIPTIONSREPORTFRAG = 119;

  private static final int LAYOUT_SUBSCRIPTIONSREPORTITEMCONTENT = 120;

  private static final int LAYOUT_SUBSCRIPTIONSREPORTITEMHEADER = 121;

  private static final int LAYOUT_SUBSCRIPTIONSREPORTITEMSTICKYHEADER = 122;

  private static final int LAYOUT_UPDATEINVOICEACT = 123;

  private static final int LAYOUT_VENDORITEM = 124;

  private static final int LAYOUT_VENDORSACT = 125;

  private static final int LAYOUT_VENDORSEMPTYVIEW = 126;

  private static final int LAYOUT_VIEWCUSTOMERFRAG = 127;

  private static final int LAYOUT_VIEWCUSTOMIZATIONACT = 128;

  private static final int LAYOUT_VIEWCUSTOMIZATIONFRAG = 129;

  private static final int LAYOUT_VIEWDELIVERYDAYSANDPRICINGLAYOUT = 130;

  private static final int LAYOUT_VIEWPOSTPAIDCUSTOMIZATIONLAYOUT = 131;

  private static final int LAYOUT_VIEWPREPAIDCUSTOMIZATIONLAYOUT = 132;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(132);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.activate_merchant_frag, LAYOUT_ACTIVATEMERCHANTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_area_frag, LAYOUT_ADDAREAFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_customer_act, LAYOUT_ADDCUSTOMERACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_customization_act, LAYOUT_ADDCUSTOMIZATIONACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_customization_frag, LAYOUT_ADDCUSTOMIZATIONFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_delivery_days_and_price_item, LAYOUT_ADDDELIVERYDAYSANDPRICEITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_delivery_days_item, LAYOUT_ADDDELIVERYDAYSITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_ondemand_item_frag, LAYOUT_ADDONDEMANDITEMFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_price_item, LAYOUT_ADDPRICEITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_product_act, LAYOUT_ADDPRODUCTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_product_frag, LAYOUT_ADDPRODUCTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_product_item, LAYOUT_ADDPRODUCTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_route_frag, LAYOUT_ADDROUTEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.add_subscription_act, LAYOUT_ADDSUBSCRIPTIONACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.advance_payment, LAYOUT_ADVANCEPAYMENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.bill_image_preview_frag, LAYOUT_BILLIMAGEPREVIEWFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.check_out_date_frag, LAYOUT_CHECKOUTDATEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.collection_act, LAYOUT_COLLECTIONACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.collection_frag, LAYOUT_COLLECTIONFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.collection_item, LAYOUT_COLLECTIONITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.collection_summary_frag, LAYOUT_COLLECTIONSUMMARYFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.custom_product_items, LAYOUT_CUSTOMPRODUCTITEMS);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.custom_spinner_view, LAYOUT_CUSTOMSPINNERVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.customer_item, LAYOUT_CUSTOMERITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.customer_subscription_item, LAYOUT_CUSTOMERSUBSCRIPTIONITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.customers_empty_view, LAYOUT_CUSTOMERSEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.customers_frag, LAYOUT_CUSTOMERSFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.daily_indent_act, LAYOUT_DAILYINDENTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.daily_indent_frag, LAYOUT_DAILYINDENTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.daily_indent_item, LAYOUT_DAILYINDENTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.date_frag, LAYOUT_DATEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.datewise_invoice_item_item, LAYOUT_DATEWISEINVOICEITEMITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.door_number_frag, LAYOUT_DOORNUMBERFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.draft_invoice_report_act, LAYOUT_DRAFTINVOICEREPORTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.draft_invoice_report_frag, LAYOUT_DRAFTINVOICEREPORTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.draft_invoice_report_item, LAYOUT_DRAFTINVOICEREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.draft_invoices_empty_view, LAYOUT_DRAFTINVOICESEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_customer_act, LAYOUT_EDITCUSTOMERACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_customer_content, LAYOUT_EDITCUSTOMERCONTENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_draft_invoice_act, LAYOUT_EDITDRAFTINVOICEACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_draft_invoice_frag, LAYOUT_EDITDRAFTINVOICEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_invoice_act, LAYOUT_EDITINVOICEACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_pause_act, LAYOUT_EDITPAUSEACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_product_act, LAYOUT_EDITPRODUCTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_product_frag, LAYOUT_EDITPRODUCTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.edit_subscription_act, LAYOUT_EDITSUBSCRIPTIONACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.home_frag, LAYOUT_HOMEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.inbox_empty_view, LAYOUT_INBOXEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.inbox_frag, LAYOUT_INBOXFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.inbox_item, LAYOUT_INBOXITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.indent_act, LAYOUT_INDENTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.indent_empty_view, LAYOUT_INDENTEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.indent_frag, LAYOUT_INDENTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.indent_item, LAYOUT_INDENTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.intro_page, LAYOUT_INTROPAGE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.invoice_frag, LAYOUT_INVOICEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.invoice_history_act, LAYOUT_INVOICEHISTORYACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.invoice_not_found_error, LAYOUT_INVOICENOTFOUNDERROR);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.invoicehistory_frag, LAYOUT_INVOICEHISTORYFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.invoicehistory_item, LAYOUT_INVOICEHISTORYITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.loading_view, LAYOUT_LOADINGVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.location_frag, LAYOUT_LOCATIONFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.login_act, LAYOUT_LOGINACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.manage_subscription_item, LAYOUT_MANAGESUBSCRIPTIONITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.manage_subscriptions_frag, LAYOUT_MANAGESUBSCRIPTIONSFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.merchant_bank_info_frag, LAYOUT_MERCHANTBANKINFOFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.merchant_kyc_frag, LAYOUT_MERCHANTKYCFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.merchant_profile_frag, LAYOUT_MERCHANTPROFILEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.move_customer_act, LAYOUT_MOVECUSTOMERACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.move_customer_frag, LAYOUT_MOVECUSTOMERFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.network_error, LAYOUT_NETWORKERROR);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.no_content_available, LAYOUT_NOCONTENTAVAILABLE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.no_content_error, LAYOUT_NOCONTENTERROR);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.notification_dashboard_act, LAYOUT_NOTIFICATIONDASHBOARDACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.onboard_act, LAYOUT_ONBOARDACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.onboard_frag, LAYOUT_ONBOARDFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.onboard_report_item, LAYOUT_ONBOARDREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_cd_selection, LAYOUT_ORDERSUMMARYCDSELECTION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_customer_details_layout, LAYOUT_ORDERSUMMARYCUSTOMERDETAILSLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_customer_details_report_act, LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_customer_details_report_item, LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_customer_details_total, LAYOUT_ORDERSUMMARYCUSTOMERDETAILSTOTAL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_details_layout, LAYOUT_ORDERSUMMARYDETAILSLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_report_act, LAYOUT_ORDERSUMMARYREPORTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_report_item, LAYOUT_ORDERSUMMARYREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_selection, LAYOUT_ORDERSUMMARYSELECTION);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.order_summary_total, LAYOUT_ORDERSUMMARYTOTAL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.ordersummary_customer_details_report_frag, LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.ordersummary_report_frag, LAYOUT_ORDERSUMMARYREPORTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.pause_item, LAYOUT_PAUSEITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.pause_subscription_dialog, LAYOUT_PAUSESUBSCRIPTIONDIALOG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.payment_report_item, LAYOUT_PAYMENTREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.payments_act, LAYOUT_PAYMENTSACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.payments_empty_view, LAYOUT_PAYMENTSEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.payments_frag, LAYOUT_PAYMENTSFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.product_item, LAYOUT_PRODUCTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.product_setup_frag, LAYOUT_PRODUCTSETUPFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.products_act, LAYOUT_PRODUCTSACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.products_empty_view, LAYOUT_PRODUCTSEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.products_frag, LAYOUT_PRODUCTSFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.profile_frag, LAYOUT_PROFILEFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.register_act, LAYOUT_REGISTERACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.reports_activity, LAYOUT_REPORTSACTIVITY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.review_frag, LAYOUT_REVIEWFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.review_subscription_frag, LAYOUT_REVIEWSUBSCRIPTIONFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.search_act, LAYOUT_SEARCHACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.search_results_empty_view, LAYOUT_SEARCHRESULTSEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.settlement_amount_breakup_layout, LAYOUT_SETTLEMENTAMOUNTBREAKUPLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.settlement_details_layout, LAYOUT_SETTLEMENTDETAILSLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.settlement_report_act, LAYOUT_SETTLEMENTREPORTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.settlement_report_frag, LAYOUT_SETTLEMENTREPORTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.settlement_report_item, LAYOUT_SETTLEMENTREPORTITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.shop_setup_frag, LAYOUT_SHOPSETUPFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscription_item, LAYOUT_SUBSCRIPTIONITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscription_report_pause_item, LAYOUT_SUBSCRIPTIONREPORTPAUSEITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscription_report_subscription_item, LAYOUT_SUBSCRIPTIONREPORTSUBSCRIPTIONITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_frag, LAYOUT_SUBSCRIPTIONSFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_report_act, LAYOUT_SUBSCRIPTIONSREPORTACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_report_frag, LAYOUT_SUBSCRIPTIONSREPORTFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_report_item_content, LAYOUT_SUBSCRIPTIONSREPORTITEMCONTENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_report_item_header, LAYOUT_SUBSCRIPTIONSREPORTITEMHEADER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.subscriptions_report_item_sticky_header, LAYOUT_SUBSCRIPTIONSREPORTITEMSTICKYHEADER);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.update_invoice_act, LAYOUT_UPDATEINVOICEACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.vendor_item, LAYOUT_VENDORITEM);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.vendors_act, LAYOUT_VENDORSACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.vendors_empty_view, LAYOUT_VENDORSEMPTYVIEW);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_customer_frag, LAYOUT_VIEWCUSTOMERFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_customization_act, LAYOUT_VIEWCUSTOMIZATIONACT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_customization_frag, LAYOUT_VIEWCUSTOMIZATIONFRAG);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_delivery_days_and_pricing_layout, LAYOUT_VIEWDELIVERYDAYSANDPRICINGLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_postpaid_customization_layout, LAYOUT_VIEWPOSTPAIDCUSTOMIZATIONLAYOUT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.zopnote.android.merchant.R.layout.view_prepaid_customization_layout, LAYOUT_VIEWPREPAIDCUSTOMIZATIONLAYOUT);
  }

  private final ViewDataBinding internalGetViewDataBinding0(DataBindingComponent component,
      View view, int internalId, Object tag) {
    switch(internalId) {
      case  LAYOUT_ACTIVATEMERCHANTFRAG: {
        if ("layout/activate_merchant_frag_0".equals(tag)) {
          return new ActivateMerchantFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for activate_merchant_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDAREAFRAG: {
        if ("layout/add_area_frag_0".equals(tag)) {
          return new AddAreaFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_area_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDCUSTOMERACT: {
        if ("layout/add_customer_act_0".equals(tag)) {
          return new AddCustomerActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_customer_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDCUSTOMIZATIONACT: {
        if ("layout/add_customization_act_0".equals(tag)) {
          return new AddCustomizationActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_customization_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDCUSTOMIZATIONFRAG: {
        if ("layout/add_customization_frag_0".equals(tag)) {
          return new AddCustomizationFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_customization_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDDELIVERYDAYSANDPRICEITEM: {
        if ("layout/add_delivery_days_and_price_item_0".equals(tag)) {
          return new AddDeliveryDaysAndPriceItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_delivery_days_and_price_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDDELIVERYDAYSITEM: {
        if ("layout/add_delivery_days_item_0".equals(tag)) {
          return new AddDeliveryDaysItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_delivery_days_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDONDEMANDITEMFRAG: {
        if ("layout/add_ondemand_item_frag_0".equals(tag)) {
          return new AddOndemandItemFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_ondemand_item_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDPRICEITEM: {
        if ("layout/add_price_item_0".equals(tag)) {
          return new AddPriceItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_price_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDPRODUCTACT: {
        if ("layout/add_product_act_0".equals(tag)) {
          return new AddProductActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_product_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDPRODUCTFRAG: {
        if ("layout/add_product_frag_0".equals(tag)) {
          return new AddProductFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_product_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDPRODUCTITEM: {
        if ("layout/add_product_item_0".equals(tag)) {
          return new AddProductItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_product_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDROUTEFRAG: {
        if ("layout/add_route_frag_0".equals(tag)) {
          return new AddRouteFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_route_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ADDSUBSCRIPTIONACT: {
        if ("layout/add_subscription_act_0".equals(tag)) {
          return new AddSubscriptionActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for add_subscription_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ADVANCEPAYMENT: {
        if ("layout/advance_payment_0".equals(tag)) {
          return new AdvancePaymentBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for advance_payment is invalid. Received: " + tag);
      }
      case  LAYOUT_BILLIMAGEPREVIEWFRAG: {
        if ("layout/bill_image_preview_frag_0".equals(tag)) {
          return new BillImagePreviewFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for bill_image_preview_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_CHECKOUTDATEFRAG: {
        if ("layout/check_out_date_frag_0".equals(tag)) {
          return new CheckOutDateFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for check_out_date_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_COLLECTIONACT: {
        if ("layout/collection_act_0".equals(tag)) {
          return new CollectionActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for collection_act is invalid. Received: " + tag);
      }
      case  LAYOUT_COLLECTIONFRAG: {
        if ("layout/collection_frag_0".equals(tag)) {
          return new CollectionFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for collection_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_COLLECTIONITEM: {
        if ("layout/collection_item_0".equals(tag)) {
          return new CollectionItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for collection_item is invalid. Received: " + tag);
      }
      case  LAYOUT_COLLECTIONSUMMARYFRAG: {
        if ("layout/collection_summary_frag_0".equals(tag)) {
          return new CollectionSummaryFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for collection_summary_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMPRODUCTITEMS: {
        if ("layout/custom_product_items_0".equals(tag)) {
          return new CustomProductItemsBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for custom_product_items is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMSPINNERVIEW: {
        if ("layout/custom_spinner_view_0".equals(tag)) {
          return new CustomSpinnerViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for custom_spinner_view is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMERITEM: {
        if ("layout/customer_item_0".equals(tag)) {
          return new CustomerItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for customer_item is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMERSUBSCRIPTIONITEM: {
        if ("layout/customer_subscription_item_0".equals(tag)) {
          return new CustomerSubscriptionItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for customer_subscription_item is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMERSEMPTYVIEW: {
        if ("layout/customers_empty_view_0".equals(tag)) {
          return new CustomersEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for customers_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_CUSTOMERSFRAG: {
        if ("layout/customers_frag_0".equals(tag)) {
          return new CustomersFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for customers_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_DAILYINDENTACT: {
        if ("layout/daily_indent_act_0".equals(tag)) {
          return new DailyIndentActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for daily_indent_act is invalid. Received: " + tag);
      }
      case  LAYOUT_DAILYINDENTFRAG: {
        if ("layout/daily_indent_frag_0".equals(tag)) {
          return new DailyIndentFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for daily_indent_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_DAILYINDENTITEM: {
        if ("layout/daily_indent_item_0".equals(tag)) {
          return new DailyIndentItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for daily_indent_item is invalid. Received: " + tag);
      }
      case  LAYOUT_DATEFRAG: {
        if ("layout/date_frag_0".equals(tag)) {
          return new DateFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for date_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_DATEWISEINVOICEITEMITEM: {
        if ("layout/datewise_invoice_item_item_0".equals(tag)) {
          return new DatewiseInvoiceItemItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for datewise_invoice_item_item is invalid. Received: " + tag);
      }
      case  LAYOUT_DOORNUMBERFRAG: {
        if ("layout/door_number_frag_0".equals(tag)) {
          return new DoorNumberFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for door_number_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_DRAFTINVOICEREPORTACT: {
        if ("layout/draft_invoice_report_act_0".equals(tag)) {
          return new DraftInvoiceReportActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for draft_invoice_report_act is invalid. Received: " + tag);
      }
      case  LAYOUT_DRAFTINVOICEREPORTFRAG: {
        if ("layout/draft_invoice_report_frag_0".equals(tag)) {
          return new DraftInvoiceReportFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for draft_invoice_report_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_DRAFTINVOICEREPORTITEM: {
        if ("layout/draft_invoice_report_item_0".equals(tag)) {
          return new DraftInvoiceReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for draft_invoice_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_DRAFTINVOICESEMPTYVIEW: {
        if ("layout/draft_invoices_empty_view_0".equals(tag)) {
          return new DraftInvoicesEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for draft_invoices_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITCUSTOMERACT: {
        if ("layout/edit_customer_act_0".equals(tag)) {
          return new EditCustomerActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_customer_act is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITCUSTOMERCONTENT: {
        if ("layout/edit_customer_content_0".equals(tag)) {
          return new EditCustomerContentBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_customer_content is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITDRAFTINVOICEACT: {
        if ("layout/edit_draft_invoice_act_0".equals(tag)) {
          return new EditDraftInvoiceActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_draft_invoice_act is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITDRAFTINVOICEFRAG: {
        if ("layout/edit_draft_invoice_frag_0".equals(tag)) {
          return new EditDraftInvoiceFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_draft_invoice_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITINVOICEACT: {
        if ("layout/edit_invoice_act_0".equals(tag)) {
          return new EditInvoiceActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_invoice_act is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITPAUSEACT: {
        if ("layout/edit_pause_act_0".equals(tag)) {
          return new EditPauseActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_pause_act is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITPRODUCTACT: {
        if ("layout/edit_product_act_0".equals(tag)) {
          return new EditProductActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_product_act is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITPRODUCTFRAG: {
        if ("layout/edit_product_frag_0".equals(tag)) {
          return new EditProductFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_product_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_EDITSUBSCRIPTIONACT: {
        if ("layout/edit_subscription_act_0".equals(tag)) {
          return new EditSubscriptionActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for edit_subscription_act is invalid. Received: " + tag);
      }
      case  LAYOUT_HOMEFRAG: {
        if ("layout/home_frag_0".equals(tag)) {
          return new HomeFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for home_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_INBOXEMPTYVIEW: {
        if ("layout/inbox_empty_view_0".equals(tag)) {
          return new InboxEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for inbox_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_INBOXFRAG: {
        if ("layout/inbox_frag_0".equals(tag)) {
          return new InboxFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for inbox_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_INBOXITEM: {
        if ("layout/inbox_item_0".equals(tag)) {
          return new InboxItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for inbox_item is invalid. Received: " + tag);
      }
    }
    return null;
  }

  private final ViewDataBinding internalGetViewDataBinding1(DataBindingComponent component,
      View view, int internalId, Object tag) {
    switch(internalId) {
      case  LAYOUT_INDENTACT: {
        if ("layout/indent_act_0".equals(tag)) {
          return new IndentActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for indent_act is invalid. Received: " + tag);
      }
      case  LAYOUT_INDENTEMPTYVIEW: {
        if ("layout/indent_empty_view_0".equals(tag)) {
          return new IndentEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for indent_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_INDENTFRAG: {
        if ("layout/indent_frag_0".equals(tag)) {
          return new IndentFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for indent_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_INDENTITEM: {
        if ("layout/indent_item_0".equals(tag)) {
          return new IndentItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for indent_item is invalid. Received: " + tag);
      }
      case  LAYOUT_INTROPAGE: {
        if ("layout/intro_page_0".equals(tag)) {
          return new IntroPageBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for intro_page is invalid. Received: " + tag);
      }
      case  LAYOUT_INVOICEFRAG: {
        if ("layout/invoice_frag_0".equals(tag)) {
          return new InvoiceFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for invoice_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_INVOICEHISTORYACT: {
        if ("layout/invoice_history_act_0".equals(tag)) {
          return new InvoiceHistoryActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for invoice_history_act is invalid. Received: " + tag);
      }
      case  LAYOUT_INVOICENOTFOUNDERROR: {
        if ("layout/invoice_not_found_error_0".equals(tag)) {
          return new InvoiceNotFoundErrorBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for invoice_not_found_error is invalid. Received: " + tag);
      }
      case  LAYOUT_INVOICEHISTORYFRAG: {
        if ("layout/invoicehistory_frag_0".equals(tag)) {
          return new InvoicehistoryFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for invoicehistory_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_INVOICEHISTORYITEM: {
        if ("layout/invoicehistory_item_0".equals(tag)) {
          return new InvoicehistoryItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for invoicehistory_item is invalid. Received: " + tag);
      }
      case  LAYOUT_LOADINGVIEW: {
        if ("layout/loading_view_0".equals(tag)) {
          return new LoadingViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for loading_view is invalid. Received: " + tag);
      }
      case  LAYOUT_LOCATIONFRAG: {
        if ("layout/location_frag_0".equals(tag)) {
          return new LocationFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for location_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_LOGINACT: {
        if ("layout/login_act_0".equals(tag)) {
          return new LoginActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for login_act is invalid. Received: " + tag);
      }
      case  LAYOUT_MANAGESUBSCRIPTIONITEM: {
        if ("layout/manage_subscription_item_0".equals(tag)) {
          return new ManageSubscriptionItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for manage_subscription_item is invalid. Received: " + tag);
      }
      case  LAYOUT_MANAGESUBSCRIPTIONSFRAG: {
        if ("layout/manage_subscriptions_frag_0".equals(tag)) {
          return new ManageSubscriptionsFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for manage_subscriptions_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_MERCHANTBANKINFOFRAG: {
        if ("layout/merchant_bank_info_frag_0".equals(tag)) {
          return new MerchantBankInfoFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for merchant_bank_info_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_MERCHANTKYCFRAG: {
        if ("layout/merchant_kyc_frag_0".equals(tag)) {
          return new MerchantKycFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for merchant_kyc_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_MERCHANTPROFILEFRAG: {
        if ("layout/merchant_profile_frag_0".equals(tag)) {
          return new MerchantProfileFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for merchant_profile_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_MOVECUSTOMERACT: {
        if ("layout/move_customer_act_0".equals(tag)) {
          return new MoveCustomerActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for move_customer_act is invalid. Received: " + tag);
      }
      case  LAYOUT_MOVECUSTOMERFRAG: {
        if ("layout/move_customer_frag_0".equals(tag)) {
          return new MoveCustomerFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for move_customer_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_NETWORKERROR: {
        if ("layout/network_error_0".equals(tag)) {
          return new NetworkErrorBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for network_error is invalid. Received: " + tag);
      }
      case  LAYOUT_NOCONTENTAVAILABLE: {
        if ("layout/no_content_available_0".equals(tag)) {
          return new NoContentAvailableBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for no_content_available is invalid. Received: " + tag);
      }
      case  LAYOUT_NOCONTENTERROR: {
        if ("layout/no_content_error_0".equals(tag)) {
          return new NoContentErrorBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for no_content_error is invalid. Received: " + tag);
      }
      case  LAYOUT_NOTIFICATIONDASHBOARDACT: {
        if ("layout/notification_dashboard_act_0".equals(tag)) {
          return new NotificationDashboardActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for notification_dashboard_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ONBOARDACT: {
        if ("layout/onboard_act_0".equals(tag)) {
          return new OnboardActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for onboard_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ONBOARDFRAG: {
        if ("layout/onboard_frag_0".equals(tag)) {
          return new OnboardFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for onboard_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ONBOARDREPORTITEM: {
        if ("layout/onboard_report_item_0".equals(tag)) {
          return new OnboardReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for onboard_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCDSELECTION: {
        if ("layout/order_summary_cd_selection_0".equals(tag)) {
          return new OrderSummaryCdSelectionBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_cd_selection is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCUSTOMERDETAILSLAYOUT: {
        if ("layout/order_summary_customer_details_layout_0".equals(tag)) {
          return new OrderSummaryCustomerDetailsLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_customer_details_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTACT: {
        if ("layout/order_summary_customer_details_report_act_0".equals(tag)) {
          return new OrderSummaryCustomerDetailsReportActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_customer_details_report_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTITEM: {
        if ("layout/order_summary_customer_details_report_item_0".equals(tag)) {
          return new OrderSummaryCustomerDetailsReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_customer_details_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCUSTOMERDETAILSTOTAL: {
        if ("layout/order_summary_customer_details_total_0".equals(tag)) {
          return new OrderSummaryCustomerDetailsTotalBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_customer_details_total is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYDETAILSLAYOUT: {
        if ("layout/order_summary_details_layout_0".equals(tag)) {
          return new OrderSummaryDetailsLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_details_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYREPORTACT: {
        if ("layout/order_summary_report_act_0".equals(tag)) {
          return new OrderSummaryReportActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_report_act is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYREPORTITEM: {
        if ("layout/order_summary_report_item_0".equals(tag)) {
          return new OrderSummaryReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYSELECTION: {
        if ("layout/order_summary_selection_0".equals(tag)) {
          return new OrderSummarySelectionBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_selection is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYTOTAL: {
        if ("layout/order_summary_total_0".equals(tag)) {
          return new OrderSummaryTotalBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for order_summary_total is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYCUSTOMERDETAILSREPORTFRAG: {
        if ("layout/ordersummary_customer_details_report_frag_0".equals(tag)) {
          return new OrdersummaryCustomerDetailsReportFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for ordersummary_customer_details_report_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_ORDERSUMMARYREPORTFRAG: {
        if ("layout/ordersummary_report_frag_0".equals(tag)) {
          return new OrdersummaryReportFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for ordersummary_report_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_PAUSEITEM: {
        if ("layout/pause_item_0".equals(tag)) {
          return new PauseItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for pause_item is invalid. Received: " + tag);
      }
      case  LAYOUT_PAUSESUBSCRIPTIONDIALOG: {
        if ("layout/pause_subscription_dialog_0".equals(tag)) {
          return new PauseSubscriptionDialogBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for pause_subscription_dialog is invalid. Received: " + tag);
      }
      case  LAYOUT_PAYMENTREPORTITEM: {
        if ("layout/payment_report_item_0".equals(tag)) {
          return new PaymentReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for payment_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_PAYMENTSACT: {
        if ("layout/payments_act_0".equals(tag)) {
          return new PaymentsActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for payments_act is invalid. Received: " + tag);
      }
      case  LAYOUT_PAYMENTSEMPTYVIEW: {
        if ("layout/payments_empty_view_0".equals(tag)) {
          return new PaymentsEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for payments_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_PAYMENTSFRAG: {
        if ("layout/payments_frag_0".equals(tag)) {
          return new PaymentsFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for payments_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_PRODUCTITEM: {
        if ("layout/product_item_0".equals(tag)) {
          return new ProductItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for product_item is invalid. Received: " + tag);
      }
      case  LAYOUT_PRODUCTSETUPFRAG: {
        if ("layout/product_setup_frag_0".equals(tag)) {
          return new ProductSetupFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for product_setup_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_PRODUCTSACT: {
        if ("layout/products_act_0".equals(tag)) {
          return new ProductsActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for products_act is invalid. Received: " + tag);
      }
      case  LAYOUT_PRODUCTSEMPTYVIEW: {
        if ("layout/products_empty_view_0".equals(tag)) {
          return new ProductsEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for products_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_PRODUCTSFRAG: {
        if ("layout/products_frag_0".equals(tag)) {
          return new ProductsFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for products_frag is invalid. Received: " + tag);
      }
    }
    return null;
  }

  private final ViewDataBinding internalGetViewDataBinding2(DataBindingComponent component,
      View view, int internalId, Object tag) {
    switch(internalId) {
      case  LAYOUT_PROFILEFRAG: {
        if ("layout/profile_frag_0".equals(tag)) {
          return new ProfileFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for profile_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_REGISTERACT: {
        if ("layout/register_act_0".equals(tag)) {
          return new RegisterActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for register_act is invalid. Received: " + tag);
      }
      case  LAYOUT_REPORTSACTIVITY: {
        if ("layout/reports_activity_0".equals(tag)) {
          return new ReportsActivityBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for reports_activity is invalid. Received: " + tag);
      }
      case  LAYOUT_REVIEWFRAG: {
        if ("layout/review_frag_0".equals(tag)) {
          return new ReviewFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for review_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_REVIEWSUBSCRIPTIONFRAG: {
        if ("layout/review_subscription_frag_0".equals(tag)) {
          return new ReviewSubscriptionFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for review_subscription_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_SEARCHACT: {
        if ("layout/search_act_0".equals(tag)) {
          return new SearchActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for search_act is invalid. Received: " + tag);
      }
      case  LAYOUT_SEARCHRESULTSEMPTYVIEW: {
        if ("layout/search_results_empty_view_0".equals(tag)) {
          return new SearchResultsEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for search_results_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_SETTLEMENTAMOUNTBREAKUPLAYOUT: {
        if ("layout/settlement_amount_breakup_layout_0".equals(tag)) {
          return new SettlementAmountBreakupLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for settlement_amount_breakup_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_SETTLEMENTDETAILSLAYOUT: {
        if ("layout/settlement_details_layout_0".equals(tag)) {
          return new SettlementDetailsLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for settlement_details_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_SETTLEMENTREPORTACT: {
        if ("layout/settlement_report_act_0".equals(tag)) {
          return new SettlementReportActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for settlement_report_act is invalid. Received: " + tag);
      }
      case  LAYOUT_SETTLEMENTREPORTFRAG: {
        if ("layout/settlement_report_frag_0".equals(tag)) {
          return new SettlementReportFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for settlement_report_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_SETTLEMENTREPORTITEM: {
        if ("layout/settlement_report_item_0".equals(tag)) {
          return new SettlementReportItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for settlement_report_item is invalid. Received: " + tag);
      }
      case  LAYOUT_SHOPSETUPFRAG: {
        if ("layout/shop_setup_frag_0".equals(tag)) {
          return new ShopSetupFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for shop_setup_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONITEM: {
        if ("layout/subscription_item_0".equals(tag)) {
          return new SubscriptionItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscription_item is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONREPORTPAUSEITEM: {
        if ("layout/subscription_report_pause_item_0".equals(tag)) {
          return new SubscriptionReportPauseItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscription_report_pause_item is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONREPORTSUBSCRIPTIONITEM: {
        if ("layout/subscription_report_subscription_item_0".equals(tag)) {
          return new SubscriptionReportSubscriptionItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscription_report_subscription_item is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSFRAG: {
        if ("layout/subscriptions_frag_0".equals(tag)) {
          return new SubscriptionsFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSREPORTACT: {
        if ("layout/subscriptions_report_act_0".equals(tag)) {
          return new SubscriptionsReportActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_report_act is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSREPORTFRAG: {
        if ("layout/subscriptions_report_frag_0".equals(tag)) {
          return new SubscriptionsReportFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_report_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSREPORTITEMCONTENT: {
        if ("layout/subscriptions_report_item_content_0".equals(tag)) {
          return new SubscriptionsReportItemContentBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_report_item_content is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSREPORTITEMHEADER: {
        if ("layout/subscriptions_report_item_header_0".equals(tag)) {
          return new SubscriptionsReportItemHeaderBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_report_item_header is invalid. Received: " + tag);
      }
      case  LAYOUT_SUBSCRIPTIONSREPORTITEMSTICKYHEADER: {
        if ("layout/subscriptions_report_item_sticky_header_0".equals(tag)) {
          return new SubscriptionsReportItemStickyHeaderBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for subscriptions_report_item_sticky_header is invalid. Received: " + tag);
      }
      case  LAYOUT_UPDATEINVOICEACT: {
        if ("layout/update_invoice_act_0".equals(tag)) {
          return new UpdateInvoiceActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for update_invoice_act is invalid. Received: " + tag);
      }
      case  LAYOUT_VENDORITEM: {
        if ("layout/vendor_item_0".equals(tag)) {
          return new VendorItemBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for vendor_item is invalid. Received: " + tag);
      }
      case  LAYOUT_VENDORSACT: {
        if ("layout/vendors_act_0".equals(tag)) {
          return new VendorsActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for vendors_act is invalid. Received: " + tag);
      }
      case  LAYOUT_VENDORSEMPTYVIEW: {
        if ("layout/vendors_empty_view_0".equals(tag)) {
          return new VendorsEmptyViewBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for vendors_empty_view is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWCUSTOMERFRAG: {
        if ("layout/view_customer_frag_0".equals(tag)) {
          return new ViewCustomerFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_customer_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWCUSTOMIZATIONACT: {
        if ("layout/view_customization_act_0".equals(tag)) {
          return new ViewCustomizationActBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_customization_act is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWCUSTOMIZATIONFRAG: {
        if ("layout/view_customization_frag_0".equals(tag)) {
          return new ViewCustomizationFragBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_customization_frag is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWDELIVERYDAYSANDPRICINGLAYOUT: {
        if ("layout/view_delivery_days_and_pricing_layout_0".equals(tag)) {
          return new ViewDeliveryDaysAndPricingLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_delivery_days_and_pricing_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWPOSTPAIDCUSTOMIZATIONLAYOUT: {
        if ("layout/view_postpaid_customization_layout_0".equals(tag)) {
          return new ViewPostpaidCustomizationLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_postpaid_customization_layout is invalid. Received: " + tag);
      }
      case  LAYOUT_VIEWPREPAIDCUSTOMIZATIONLAYOUT: {
        if ("layout/view_prepaid_customization_layout_0".equals(tag)) {
          return new ViewPrepaidCustomizationLayoutBindingImpl(component, view);
        }
        throw new IllegalArgumentException("The tag for view_prepaid_customization_layout is invalid. Received: " + tag);
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      // find which method will have it. -1 is necessary becausefirst id starts with 1;
      int methodIndex = (localizedLayoutId - 1) / 50;
      switch(methodIndex) {
        case 0: {
          return internalGetViewDataBinding0(component, view, localizedLayoutId, tag);
        }
        case 1: {
          return internalGetViewDataBinding1(component, view, localizedLayoutId, tag);
        }
        case 2: {
          return internalGetViewDataBinding2(component, view, localizedLayoutId, tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new com.android.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(1);

    static {
      sKeys.put(0, "_all");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(132);

    static {
      sKeys.put("layout/activate_merchant_frag_0", com.zopnote.android.merchant.R.layout.activate_merchant_frag);
      sKeys.put("layout/add_area_frag_0", com.zopnote.android.merchant.R.layout.add_area_frag);
      sKeys.put("layout/add_customer_act_0", com.zopnote.android.merchant.R.layout.add_customer_act);
      sKeys.put("layout/add_customization_act_0", com.zopnote.android.merchant.R.layout.add_customization_act);
      sKeys.put("layout/add_customization_frag_0", com.zopnote.android.merchant.R.layout.add_customization_frag);
      sKeys.put("layout/add_delivery_days_and_price_item_0", com.zopnote.android.merchant.R.layout.add_delivery_days_and_price_item);
      sKeys.put("layout/add_delivery_days_item_0", com.zopnote.android.merchant.R.layout.add_delivery_days_item);
      sKeys.put("layout/add_ondemand_item_frag_0", com.zopnote.android.merchant.R.layout.add_ondemand_item_frag);
      sKeys.put("layout/add_price_item_0", com.zopnote.android.merchant.R.layout.add_price_item);
      sKeys.put("layout/add_product_act_0", com.zopnote.android.merchant.R.layout.add_product_act);
      sKeys.put("layout/add_product_frag_0", com.zopnote.android.merchant.R.layout.add_product_frag);
      sKeys.put("layout/add_product_item_0", com.zopnote.android.merchant.R.layout.add_product_item);
      sKeys.put("layout/add_route_frag_0", com.zopnote.android.merchant.R.layout.add_route_frag);
      sKeys.put("layout/add_subscription_act_0", com.zopnote.android.merchant.R.layout.add_subscription_act);
      sKeys.put("layout/advance_payment_0", com.zopnote.android.merchant.R.layout.advance_payment);
      sKeys.put("layout/bill_image_preview_frag_0", com.zopnote.android.merchant.R.layout.bill_image_preview_frag);
      sKeys.put("layout/check_out_date_frag_0", com.zopnote.android.merchant.R.layout.check_out_date_frag);
      sKeys.put("layout/collection_act_0", com.zopnote.android.merchant.R.layout.collection_act);
      sKeys.put("layout/collection_frag_0", com.zopnote.android.merchant.R.layout.collection_frag);
      sKeys.put("layout/collection_item_0", com.zopnote.android.merchant.R.layout.collection_item);
      sKeys.put("layout/collection_summary_frag_0", com.zopnote.android.merchant.R.layout.collection_summary_frag);
      sKeys.put("layout/custom_product_items_0", com.zopnote.android.merchant.R.layout.custom_product_items);
      sKeys.put("layout/custom_spinner_view_0", com.zopnote.android.merchant.R.layout.custom_spinner_view);
      sKeys.put("layout/customer_item_0", com.zopnote.android.merchant.R.layout.customer_item);
      sKeys.put("layout/customer_subscription_item_0", com.zopnote.android.merchant.R.layout.customer_subscription_item);
      sKeys.put("layout/customers_empty_view_0", com.zopnote.android.merchant.R.layout.customers_empty_view);
      sKeys.put("layout/customers_frag_0", com.zopnote.android.merchant.R.layout.customers_frag);
      sKeys.put("layout/daily_indent_act_0", com.zopnote.android.merchant.R.layout.daily_indent_act);
      sKeys.put("layout/daily_indent_frag_0", com.zopnote.android.merchant.R.layout.daily_indent_frag);
      sKeys.put("layout/daily_indent_item_0", com.zopnote.android.merchant.R.layout.daily_indent_item);
      sKeys.put("layout/date_frag_0", com.zopnote.android.merchant.R.layout.date_frag);
      sKeys.put("layout/datewise_invoice_item_item_0", com.zopnote.android.merchant.R.layout.datewise_invoice_item_item);
      sKeys.put("layout/door_number_frag_0", com.zopnote.android.merchant.R.layout.door_number_frag);
      sKeys.put("layout/draft_invoice_report_act_0", com.zopnote.android.merchant.R.layout.draft_invoice_report_act);
      sKeys.put("layout/draft_invoice_report_frag_0", com.zopnote.android.merchant.R.layout.draft_invoice_report_frag);
      sKeys.put("layout/draft_invoice_report_item_0", com.zopnote.android.merchant.R.layout.draft_invoice_report_item);
      sKeys.put("layout/draft_invoices_empty_view_0", com.zopnote.android.merchant.R.layout.draft_invoices_empty_view);
      sKeys.put("layout/edit_customer_act_0", com.zopnote.android.merchant.R.layout.edit_customer_act);
      sKeys.put("layout/edit_customer_content_0", com.zopnote.android.merchant.R.layout.edit_customer_content);
      sKeys.put("layout/edit_draft_invoice_act_0", com.zopnote.android.merchant.R.layout.edit_draft_invoice_act);
      sKeys.put("layout/edit_draft_invoice_frag_0", com.zopnote.android.merchant.R.layout.edit_draft_invoice_frag);
      sKeys.put("layout/edit_invoice_act_0", com.zopnote.android.merchant.R.layout.edit_invoice_act);
      sKeys.put("layout/edit_pause_act_0", com.zopnote.android.merchant.R.layout.edit_pause_act);
      sKeys.put("layout/edit_product_act_0", com.zopnote.android.merchant.R.layout.edit_product_act);
      sKeys.put("layout/edit_product_frag_0", com.zopnote.android.merchant.R.layout.edit_product_frag);
      sKeys.put("layout/edit_subscription_act_0", com.zopnote.android.merchant.R.layout.edit_subscription_act);
      sKeys.put("layout/home_frag_0", com.zopnote.android.merchant.R.layout.home_frag);
      sKeys.put("layout/inbox_empty_view_0", com.zopnote.android.merchant.R.layout.inbox_empty_view);
      sKeys.put("layout/inbox_frag_0", com.zopnote.android.merchant.R.layout.inbox_frag);
      sKeys.put("layout/inbox_item_0", com.zopnote.android.merchant.R.layout.inbox_item);
      sKeys.put("layout/indent_act_0", com.zopnote.android.merchant.R.layout.indent_act);
      sKeys.put("layout/indent_empty_view_0", com.zopnote.android.merchant.R.layout.indent_empty_view);
      sKeys.put("layout/indent_frag_0", com.zopnote.android.merchant.R.layout.indent_frag);
      sKeys.put("layout/indent_item_0", com.zopnote.android.merchant.R.layout.indent_item);
      sKeys.put("layout/intro_page_0", com.zopnote.android.merchant.R.layout.intro_page);
      sKeys.put("layout/invoice_frag_0", com.zopnote.android.merchant.R.layout.invoice_frag);
      sKeys.put("layout/invoice_history_act_0", com.zopnote.android.merchant.R.layout.invoice_history_act);
      sKeys.put("layout/invoice_not_found_error_0", com.zopnote.android.merchant.R.layout.invoice_not_found_error);
      sKeys.put("layout/invoicehistory_frag_0", com.zopnote.android.merchant.R.layout.invoicehistory_frag);
      sKeys.put("layout/invoicehistory_item_0", com.zopnote.android.merchant.R.layout.invoicehistory_item);
      sKeys.put("layout/loading_view_0", com.zopnote.android.merchant.R.layout.loading_view);
      sKeys.put("layout/location_frag_0", com.zopnote.android.merchant.R.layout.location_frag);
      sKeys.put("layout/login_act_0", com.zopnote.android.merchant.R.layout.login_act);
      sKeys.put("layout/manage_subscription_item_0", com.zopnote.android.merchant.R.layout.manage_subscription_item);
      sKeys.put("layout/manage_subscriptions_frag_0", com.zopnote.android.merchant.R.layout.manage_subscriptions_frag);
      sKeys.put("layout/merchant_bank_info_frag_0", com.zopnote.android.merchant.R.layout.merchant_bank_info_frag);
      sKeys.put("layout/merchant_kyc_frag_0", com.zopnote.android.merchant.R.layout.merchant_kyc_frag);
      sKeys.put("layout/merchant_profile_frag_0", com.zopnote.android.merchant.R.layout.merchant_profile_frag);
      sKeys.put("layout/move_customer_act_0", com.zopnote.android.merchant.R.layout.move_customer_act);
      sKeys.put("layout/move_customer_frag_0", com.zopnote.android.merchant.R.layout.move_customer_frag);
      sKeys.put("layout/network_error_0", com.zopnote.android.merchant.R.layout.network_error);
      sKeys.put("layout/no_content_available_0", com.zopnote.android.merchant.R.layout.no_content_available);
      sKeys.put("layout/no_content_error_0", com.zopnote.android.merchant.R.layout.no_content_error);
      sKeys.put("layout/notification_dashboard_act_0", com.zopnote.android.merchant.R.layout.notification_dashboard_act);
      sKeys.put("layout/onboard_act_0", com.zopnote.android.merchant.R.layout.onboard_act);
      sKeys.put("layout/onboard_frag_0", com.zopnote.android.merchant.R.layout.onboard_frag);
      sKeys.put("layout/onboard_report_item_0", com.zopnote.android.merchant.R.layout.onboard_report_item);
      sKeys.put("layout/order_summary_cd_selection_0", com.zopnote.android.merchant.R.layout.order_summary_cd_selection);
      sKeys.put("layout/order_summary_customer_details_layout_0", com.zopnote.android.merchant.R.layout.order_summary_customer_details_layout);
      sKeys.put("layout/order_summary_customer_details_report_act_0", com.zopnote.android.merchant.R.layout.order_summary_customer_details_report_act);
      sKeys.put("layout/order_summary_customer_details_report_item_0", com.zopnote.android.merchant.R.layout.order_summary_customer_details_report_item);
      sKeys.put("layout/order_summary_customer_details_total_0", com.zopnote.android.merchant.R.layout.order_summary_customer_details_total);
      sKeys.put("layout/order_summary_details_layout_0", com.zopnote.android.merchant.R.layout.order_summary_details_layout);
      sKeys.put("layout/order_summary_report_act_0", com.zopnote.android.merchant.R.layout.order_summary_report_act);
      sKeys.put("layout/order_summary_report_item_0", com.zopnote.android.merchant.R.layout.order_summary_report_item);
      sKeys.put("layout/order_summary_selection_0", com.zopnote.android.merchant.R.layout.order_summary_selection);
      sKeys.put("layout/order_summary_total_0", com.zopnote.android.merchant.R.layout.order_summary_total);
      sKeys.put("layout/ordersummary_customer_details_report_frag_0", com.zopnote.android.merchant.R.layout.ordersummary_customer_details_report_frag);
      sKeys.put("layout/ordersummary_report_frag_0", com.zopnote.android.merchant.R.layout.ordersummary_report_frag);
      sKeys.put("layout/pause_item_0", com.zopnote.android.merchant.R.layout.pause_item);
      sKeys.put("layout/pause_subscription_dialog_0", com.zopnote.android.merchant.R.layout.pause_subscription_dialog);
      sKeys.put("layout/payment_report_item_0", com.zopnote.android.merchant.R.layout.payment_report_item);
      sKeys.put("layout/payments_act_0", com.zopnote.android.merchant.R.layout.payments_act);
      sKeys.put("layout/payments_empty_view_0", com.zopnote.android.merchant.R.layout.payments_empty_view);
      sKeys.put("layout/payments_frag_0", com.zopnote.android.merchant.R.layout.payments_frag);
      sKeys.put("layout/product_item_0", com.zopnote.android.merchant.R.layout.product_item);
      sKeys.put("layout/product_setup_frag_0", com.zopnote.android.merchant.R.layout.product_setup_frag);
      sKeys.put("layout/products_act_0", com.zopnote.android.merchant.R.layout.products_act);
      sKeys.put("layout/products_empty_view_0", com.zopnote.android.merchant.R.layout.products_empty_view);
      sKeys.put("layout/products_frag_0", com.zopnote.android.merchant.R.layout.products_frag);
      sKeys.put("layout/profile_frag_0", com.zopnote.android.merchant.R.layout.profile_frag);
      sKeys.put("layout/register_act_0", com.zopnote.android.merchant.R.layout.register_act);
      sKeys.put("layout/reports_activity_0", com.zopnote.android.merchant.R.layout.reports_activity);
      sKeys.put("layout/review_frag_0", com.zopnote.android.merchant.R.layout.review_frag);
      sKeys.put("layout/review_subscription_frag_0", com.zopnote.android.merchant.R.layout.review_subscription_frag);
      sKeys.put("layout/search_act_0", com.zopnote.android.merchant.R.layout.search_act);
      sKeys.put("layout/search_results_empty_view_0", com.zopnote.android.merchant.R.layout.search_results_empty_view);
      sKeys.put("layout/settlement_amount_breakup_layout_0", com.zopnote.android.merchant.R.layout.settlement_amount_breakup_layout);
      sKeys.put("layout/settlement_details_layout_0", com.zopnote.android.merchant.R.layout.settlement_details_layout);
      sKeys.put("layout/settlement_report_act_0", com.zopnote.android.merchant.R.layout.settlement_report_act);
      sKeys.put("layout/settlement_report_frag_0", com.zopnote.android.merchant.R.layout.settlement_report_frag);
      sKeys.put("layout/settlement_report_item_0", com.zopnote.android.merchant.R.layout.settlement_report_item);
      sKeys.put("layout/shop_setup_frag_0", com.zopnote.android.merchant.R.layout.shop_setup_frag);
      sKeys.put("layout/subscription_item_0", com.zopnote.android.merchant.R.layout.subscription_item);
      sKeys.put("layout/subscription_report_pause_item_0", com.zopnote.android.merchant.R.layout.subscription_report_pause_item);
      sKeys.put("layout/subscription_report_subscription_item_0", com.zopnote.android.merchant.R.layout.subscription_report_subscription_item);
      sKeys.put("layout/subscriptions_frag_0", com.zopnote.android.merchant.R.layout.subscriptions_frag);
      sKeys.put("layout/subscriptions_report_act_0", com.zopnote.android.merchant.R.layout.subscriptions_report_act);
      sKeys.put("layout/subscriptions_report_frag_0", com.zopnote.android.merchant.R.layout.subscriptions_report_frag);
      sKeys.put("layout/subscriptions_report_item_content_0", com.zopnote.android.merchant.R.layout.subscriptions_report_item_content);
      sKeys.put("layout/subscriptions_report_item_header_0", com.zopnote.android.merchant.R.layout.subscriptions_report_item_header);
      sKeys.put("layout/subscriptions_report_item_sticky_header_0", com.zopnote.android.merchant.R.layout.subscriptions_report_item_sticky_header);
      sKeys.put("layout/update_invoice_act_0", com.zopnote.android.merchant.R.layout.update_invoice_act);
      sKeys.put("layout/vendor_item_0", com.zopnote.android.merchant.R.layout.vendor_item);
      sKeys.put("layout/vendors_act_0", com.zopnote.android.merchant.R.layout.vendors_act);
      sKeys.put("layout/vendors_empty_view_0", com.zopnote.android.merchant.R.layout.vendors_empty_view);
      sKeys.put("layout/view_customer_frag_0", com.zopnote.android.merchant.R.layout.view_customer_frag);
      sKeys.put("layout/view_customization_act_0", com.zopnote.android.merchant.R.layout.view_customization_act);
      sKeys.put("layout/view_customization_frag_0", com.zopnote.android.merchant.R.layout.view_customization_frag);
      sKeys.put("layout/view_delivery_days_and_pricing_layout_0", com.zopnote.android.merchant.R.layout.view_delivery_days_and_pricing_layout);
      sKeys.put("layout/view_postpaid_customization_layout_0", com.zopnote.android.merchant.R.layout.view_postpaid_customization_layout);
      sKeys.put("layout/view_prepaid_customization_layout_0", com.zopnote.android.merchant.R.layout.view_prepaid_customization_layout);
    }
  }
}
