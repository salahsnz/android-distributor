package com.zopnote.android.merchant.data.repository;

import android.arch.lifecycle.LiveData;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.model.Subscription;

import java.util.Date;
import java.util.List;

/**
 * Created by nmohideen on 22/06/18.
 */

public interface DataSource {

    LiveData<Merchant> getMerchant();

    LiveData<Customer> getCustomer(String customerId);
    LiveData<List<Customer>> getCustomers();
    LiveData<List<Customer>> getCustomers(String addressLine2);

    LiveData<List<Product>> getProducts();

    LiveData<List<Invoice>> getInvoices(String customerId);
    LiveData<List<Invoice>> getPendingInvoices(String customerId);
    LiveData<List<Invoice>> getPendingInvoices();
    LiveData<List<Invoice>> getAllInvoices(String status);

    LiveData<List<Subscription>> getChangedSubscriptions(Date date);
    LiveData<List<Subscription>> getAllSubscriptions();
    LiveData<List<Subscription>> getSubscriptions(String customerId);

    LiveData<List<Product>> getNewsPapers();
    LiveData<List<Product>> getMagazines();
    LiveData<List<Product>> getOfferedProduct(String productType);

    LiveData<Invoice> getInvoice(String invoiceId);

    LiveData<List<Merchant>> getMerchants();

    LiveData<Subscription> getSubscription(String subscriptionId);

    LiveData<Product> getProduct(String productId);

    LiveData<List<GenericProduct>> getAllMagazines();
    LiveData<List<GenericProduct>> getAllNewsPapers();
    LiveData<List<GenericProduct>> getAllProduct(String productType);
}
