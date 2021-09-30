package com.zopnote.android.merchant;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.VisibleForTesting;

import com.zopnote.android.merchant.activatemerchant.ActivateMerchantViewModel;
import com.zopnote.android.merchant.addarea.AddAreaViewModel;
import com.zopnote.android.merchant.addcustomer.AddCustomerViewModel;
import com.zopnote.android.merchant.addondemanditem.AddOnDemandViewModel;
import com.zopnote.android.merchant.addroute.AddRouteViewModel;
import com.zopnote.android.merchant.addsubscription.AddSubscriptionViewModel;
import com.zopnote.android.merchant.agreement.AgreementViewModel;
import com.zopnote.android.merchant.customers.CustomersViewModel;
import com.zopnote.android.merchant.data.repository.Repository;
import com.zopnote.android.merchant.editcustomer.EditCustomerViewModel;
import com.zopnote.android.merchant.editdraftinvoice.EditDraftInvoiceViewModel;
import com.zopnote.android.merchant.home.HomeViewModel;
import com.zopnote.android.merchant.indent.IndentViewModel;
import com.zopnote.android.merchant.dailyindent.DailyIndentViewModel;
import com.zopnote.android.merchant.invoice.InvoiceViewModel;
import com.zopnote.android.merchant.invoice.editinvoice.EditInvoiceViewModel;
import com.zopnote.android.merchant.login.LoginViewModel;
import com.zopnote.android.merchant.managesubscription.ManageSubscriptionsViewModel;
import com.zopnote.android.merchant.managesubscription.addcustomization.AddCustomizationViewModel;
import com.zopnote.android.merchant.managesubscription.editpause.EditPauseViewModel;
import com.zopnote.android.merchant.managesubscription.editsubscription.EditSubscriptionViewModel;
import com.zopnote.android.merchant.managesubscription.viewcustomization.ViewCustomizationViewModel;
import com.zopnote.android.merchant.merchantsetup.MerchantBankInfoViewModel;
import com.zopnote.android.merchant.merchantsetup.MerchantKYCViewModel;
import com.zopnote.android.merchant.merchantsetup.MerchantProfileViewModel;
import com.zopnote.android.merchant.merchantsetup.ProductSetupViewModel;
import com.zopnote.android.merchant.merchantsetup.ShopSetupViewModel;
import com.zopnote.android.merchant.movecustomer.MoveCustomerViewModel;
import com.zopnote.android.merchant.notifications.inbox.InboxViewModel;
import com.zopnote.android.merchant.notifications.notificationpanel.NotificationDashboardViewModel;
import com.zopnote.android.merchant.products.ProductsViewModel;
import com.zopnote.android.merchant.products.addproduct.AddProductViewModel;
import com.zopnote.android.merchant.products.editproduct.EditProductViewModel;
import com.zopnote.android.merchant.registermerchant.RegisterViewModel;
import com.zopnote.android.merchant.reports.collection.CollectionViewModel;
import com.zopnote.android.merchant.reports.draftinvoice.DraftInvoiceReportViewModel;
import com.zopnote.android.merchant.reports.onboarding.OnboardViewModel;
import com.zopnote.android.merchant.reports.ordersummary.OrderSummaryReportViewModel;
import com.zopnote.android.merchant.reports.ordersummarycustomerdetails.OrderSummaryCustomerDetailsReportViewModel;
import com.zopnote.android.merchant.reports.payments.PaymentsViewModel;
import com.zopnote.android.merchant.reports.settlement.SettlementReportViewModel;
import com.zopnote.android.merchant.reports.subscription.SubscriptionsReportViewModel;
import com.zopnote.android.merchant.search.SearchViewModel;
import com.zopnote.android.merchant.updateinvoice.UpdateInvoiceViewModel;
import com.zopnote.android.merchant.util.Installation;
import com.zopnote.android.merchant.vendor.VendorViewModel;
import com.zopnote.android.merchant.viewcustomer.ViewCustomerViewModel;
import com.zopnote.android.merchant.viewinvoicehistory.ViewModelInvoiceHistory;

/**
 * Created by nmohideen on 26/12/17.
 */

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Application mApplication;

    private final Repository mRepository;

    public static ViewModelFactory getInstance(Application application) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(application,
                            Injection.provideRepository(Installation.getVendorUid(application)));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private ViewModelFactory(Application application, Repository repository) {
        mApplication = application;
        mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            //noinspection unchecked
            return (T) new HomeViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            //noinspection unchecked
            return (T) new LoginViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(CustomersViewModel.class)) {
            //noinspection unchecked
            return (T) new CustomersViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(AddCustomerViewModel.class)) {
            //noinspection unchecked
            return (T) new AddCustomerViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(IndentViewModel.class)) {
            //noinspection unchecked
            return (T) new IndentViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ViewCustomerViewModel.class)) {
            //noinspection unchecked
            return (T) new ViewCustomerViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(ProductsViewModel.class)) {
            return (T) new ProductsViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(InvoiceViewModel.class)) {
            return (T) new InvoiceViewModel(mApplication, mRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            return (T) new SearchViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(VendorViewModel.class)) {
            return (T) new VendorViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditCustomerViewModel.class)) {
            return (T) new EditCustomerViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(AddSubscriptionViewModel.class)) {
            return (T) new AddSubscriptionViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(OnboardViewModel.class)) {
            return (T) new OnboardViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(PaymentsViewModel.class)) {
            return (T) new PaymentsViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(MoveCustomerViewModel.class)) {
            return (T) new MoveCustomerViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(ManageSubscriptionsViewModel.class)) {
            return (T) new ManageSubscriptionsViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditSubscriptionViewModel.class)) {
            return (T) new EditSubscriptionViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditPauseViewModel.class)) {
            return (T) new EditPauseViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditInvoiceViewModel.class)) {
            return (T) new EditInvoiceViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(SubscriptionsReportViewModel.class)) {
            return (T) new SubscriptionsReportViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(DraftInvoiceReportViewModel.class)) {
            return (T) new DraftInvoiceReportViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditDraftInvoiceViewModel.class)) {
            return (T) new EditDraftInvoiceViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(SettlementReportViewModel.class)) {
            return (T) new SettlementReportViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(OrderSummaryReportViewModel.class)) {
            return (T) new OrderSummaryReportViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(OrderSummaryCustomerDetailsReportViewModel.class)) {
             return (T) new OrderSummaryCustomerDetailsReportViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(ViewCustomizationViewModel.class)) {
            return (T) new ViewCustomizationViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(AddCustomizationViewModel.class)) {
            return (T) new AddCustomizationViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(EditProductViewModel.class)) {
            return (T) new EditProductViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(AddProductViewModel.class)) {
            return (T) new AddProductViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(CollectionViewModel.class)) {
            return (T) new CollectionViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(ViewModelInvoiceHistory.class)) {
            return (T) new ViewModelInvoiceHistory(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(UpdateInvoiceViewModel.class)) {
            return (T) new UpdateInvoiceViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(mApplication);
        }else if (modelClass.isAssignableFrom(AddOnDemandViewModel.class)) {
            return (T) new AddOnDemandViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(InboxViewModel.class)) {
            return (T) new InboxViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(AgreementViewModel.class)) {
            return (T) new AgreementViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(NotificationDashboardViewModel.class)) {
            return (T) new NotificationDashboardViewModel(mApplication,mRepository);
        } else if (modelClass.isAssignableFrom(AddRouteViewModel .class)) {
            return (T) new AddRouteViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(DailyIndentViewModel.class)) {
            return (T) new DailyIndentViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(ActivateMerchantViewModel.class)) {
            return (T) new ActivateMerchantViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(AddAreaViewModel.class)) {
            return (T) new AddAreaViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(MerchantBankInfoViewModel.class)) {
            return (T) new MerchantBankInfoViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(MerchantKYCViewModel.class)) {
            return (T) new MerchantKYCViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(MerchantProfileViewModel.class)) {
            return (T) new MerchantProfileViewModel(mApplication, mRepository);
        }else if (modelClass.isAssignableFrom(ProductSetupViewModel.class)) {
            return (T) new ProductSetupViewModel(mApplication,mRepository);
        }else if (modelClass.isAssignableFrom(ShopSetupViewModel.class)) {
            return (T) new ShopSetupViewModel(mApplication,mRepository);
        }


        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
