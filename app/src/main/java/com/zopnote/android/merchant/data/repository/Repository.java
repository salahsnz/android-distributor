package com.zopnote.android.merchant.data.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.VisibleForTesting;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.model.Subscription;

import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nmohideen on 22/06/18.
 */

public class Repository implements DataSource {

    private static final boolean DEBUG = false;

    private volatile static Repository INSTANCE = null;

    private final DataSource dataSource;

    private Repository(DataSource dataSource) {
        this.dataSource = checkNotNull(dataSource);
    }

    public static Repository getInstance(DataSource dataSource) {
        if (INSTANCE == null) {
            synchronized (Repository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Repository(dataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public LiveData<Merchant> getMerchant() {
        return dataSource.getMerchant();
    }

    @Override
    public LiveData<Customer> getCustomer(String customerId) {
        return dataSource.getCustomer(customerId);
    }

    @Override
    public LiveData<List<Customer>> getCustomers() {
        return dataSource.getCustomers();
    }

    @Override
    public LiveData<List<Customer>> getCustomers(String route) {
        return dataSource.getCustomers(route);
    }

    @Override
    public LiveData<List<Product>> getProducts() {
        return dataSource.getProducts();
    }

    @Override
    public LiveData<List<Invoice>> getInvoices(String customerId) {
        return dataSource.getInvoices(customerId);
    }

    @Override
    public LiveData<List<Invoice>> getPendingInvoices(String customerId) {
        return dataSource.getPendingInvoices(customerId);
    }

    @Override
    public LiveData<List<Invoice>> getAllInvoices(String status) {
        return dataSource.getAllInvoices(status);
    }

    @Override
    public LiveData<List<Invoice>> getPendingInvoices() {
        return dataSource.getPendingInvoices();
    }

    @Override
    public LiveData<List<Subscription>> getChangedSubscriptions(Date date) {
        return dataSource.getChangedSubscriptions(date);
    }

    @Override
    public LiveData<List<Subscription>> getAllSubscriptions() {
        return dataSource.getAllSubscriptions();
    }

    @Override
    public LiveData<List<Subscription>> getSubscriptions(String customerId) {
        return dataSource.getSubscriptions(customerId);
    }

    public LiveData<List<Product>> getNewsPapers() {
        return dataSource.getNewsPapers();
    }

    @Override
    public LiveData<List<Product>> getMagazines() {
        return dataSource.getMagazines();
    }

    @Override
    public LiveData<List<Product>> getOfferedProduct(String productType) {
        return dataSource.getOfferedProduct(productType);
    }


    @Override
    public LiveData<Invoice> getInvoice(String invoiceId) {
        return dataSource.getInvoice(invoiceId);
    }

    @Override
    public LiveData<List<Merchant>> getMerchants() {
        return dataSource.getMerchants();
    }

    @Override
    public LiveData<Subscription> getSubscription(String subscriptionId) {
        return dataSource.getSubscription(subscriptionId);
    }

    public LiveData<Product> getProduct(String productId) {
        return dataSource.getProduct(productId);
    }

    @Override
    public LiveData<List<GenericProduct>> getAllMagazines() {
        return dataSource.getAllMagazines();
    }

    @Override
    public LiveData<List<GenericProduct>> getAllNewsPapers() {
        return dataSource.getAllNewsPapers();
    }

    @Override
    public LiveData<List<GenericProduct>> getAllProduct(String productType) {
        return dataSource.getAllProduct(productType);
    }
}
