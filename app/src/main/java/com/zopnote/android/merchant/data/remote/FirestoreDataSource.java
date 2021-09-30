package com.zopnote.android.merchant.data.remote;

import android.arch.lifecycle.LiveData;
import android.support.annotation.VisibleForTesting;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.Invoice;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;
import com.zopnote.android.merchant.data.model.GenericProduct;
import com.zopnote.android.merchant.data.model.Merchant;
import com.zopnote.android.merchant.data.model.Product;
import com.zopnote.android.merchant.data.model.Subscription;
import com.zopnote.android.merchant.data.repository.DataSource;

import java.util.Date;
import java.util.List;

/**
 * Created by nmohideen on 22/06/18.
 */

public class FirestoreDataSource implements DataSource {

    private static final boolean DEBUG = false;

    private static volatile FirestoreDataSource INSTANCE;

    private FirebaseFirestore db;
    private String merchantId;

    private FirestoreDataSource(String merchantId) {
        db = FirebaseFirestore.getInstance();
        this.merchantId = merchantId;
    }

    public static FirestoreDataSource getInstance(String merchantId) {
        if (INSTANCE == null) {
            synchronized (FirestoreDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FirestoreDataSource(merchantId);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private DocumentReference getMerchantDoc() {
        return db.collection("merchants")
                .document(merchantId);
    }

    @Override
    public LiveData<Merchant> getMerchant() {
        return new FirestoreLiveData<Merchant>(getMerchantDoc(), Merchant.class);
    }

    @Override
    public LiveData<Customer> getCustomer(String customerId) {
        return new FirestoreLiveData<>(getMerchantDoc().collection("customers").document(customerId), Customer.class);
    }

    @Override
    public LiveData<List<Customer>> getCustomers() {
        Query query = getMerchantDoc().collection("customers")
                .orderBy("addressLine2")
                .orderBy("doorNumber");
        return new FirestoreListLiveData<Customer>(query, Customer.class);
    }

    @Override
    public LiveData<List<Customer>> getCustomers(String route) {
        Query query = getMerchantDoc().collection("customers")
                .whereEqualTo("route", route)
                .orderBy("routeSequenceNumber");
        return new FirestoreListLiveData<>(query, Customer.class);
    }

    @Override
    public LiveData<List<Product>> getProducts() {
        Query query = getMerchantDoc().collection("offeredProducts").orderBy("name");
        return new FirestoreListLiveData<Product>(query, Product.class);
    }

    @Override
    public LiveData<List<Invoice>> getInvoices(String customerId) {
        Query query = getMerchantDoc().collection("invoices")
                .whereEqualTo("customer.id", customerId);
        return new FirestoreListLiveData<>(query, Invoice.class);
    }

    @Override
    public LiveData<List<Invoice>> getPendingInvoices(String customerId) {
        Query query = getMerchantDoc().collection("invoices")
                .whereEqualTo("customer.id", customerId)
                .whereEqualTo("status", InvoiceStatusEnum.OPEN.toString());
        return new FirestoreListLiveData<>(query, Invoice.class);
    }

    public LiveData<List<Invoice>> getPendingInvoices() {
        Query query = getMerchantDoc().collection("invoices")
                .whereEqualTo("status","OPEN");
        return new FirestoreListLiveData<>(query, Invoice.class);
    }

    @Override
    public LiveData<List<Invoice>> getAllInvoices(String status) {
        Query query = getMerchantDoc().collection("invoices")
                .whereEqualTo("status", status)
                .orderBy("customer.addressLine2")
                .orderBy("customer.doorNumber");;
        return new FirestoreListLiveData<>(query, Invoice.class);    }

    @Override
    public LiveData<List<Subscription>> getChangedSubscriptions(Date date) {
        Query query = getMerchantDoc().collection("subscriptions").whereGreaterThanOrEqualTo("pauseEndDate", date).orderBy("pauseEndDate");
        return new FirestoreListLiveData<>(query, Subscription.class);
    }

    @Override
    public LiveData<List<Subscription>> getAllSubscriptions() {
        Query query = getMerchantDoc().collection("subscriptions").orderBy("product.name");
        return new FirestoreListLiveData<Subscription>(query, Subscription.class);
    }

    @Override
    public LiveData<List<Subscription>> getSubscriptions(String customerId) {
        Query query = getMerchantDoc().collection("subscriptions")
                .whereEqualTo("customerId", customerId)
                .orderBy("product.name");
        return new FirestoreListLiveData<Subscription>(query, Subscription.class);
    }

    @Override
    public LiveData<List<Product>> getNewsPapers() {
        Query query = getMerchantDoc().collection("offeredProducts").whereEqualTo("type","Newspaper").orderBy("name");
        return new FirestoreListLiveData<Product>(query, Product.class);
    }

    @Override
    public LiveData<List<Product>> getMagazines() {
        Query query = getMerchantDoc().collection("offeredProducts").whereEqualTo("type","Magazine").orderBy("name");
        return new FirestoreListLiveData<Product>(query, Product.class);
    }
    @Override
    public LiveData<List<Product>> getOfferedProduct(String productType) {
        Query query = getMerchantDoc().collection("offeredProducts").whereEqualTo("type",productType).orderBy("name");
        return new FirestoreListLiveData<Product>(query, Product.class);
    }
    @Override
    public LiveData<List<GenericProduct>> getAllMagazines() {
        Query query = db.collection("products").whereEqualTo("type","Magazine").orderBy("name");
        return new FirestoreListLiveData<GenericProduct>(query, GenericProduct.class);
    }

    @Override
    public LiveData<List<GenericProduct>> getAllNewsPapers() {
        Query query = db.collection("products").whereEqualTo("type","Newspaper").orderBy("name");
        return new FirestoreListLiveData<GenericProduct>(query, GenericProduct.class);
    }

    @Override
    public LiveData<List<GenericProduct>> getAllProduct(String productType) {
        Query query = db.collection("products").whereEqualTo("type",productType).orderBy("name");
        return new FirestoreListLiveData<GenericProduct>(query, GenericProduct.class);
    }

    @Override
    public LiveData<Invoice> getInvoice(String invoiceId) {
        return new FirestoreLiveData<>(getMerchantDoc().collection("invoices").document(invoiceId), Invoice.class);
    }

    @Override
    public LiveData<List<Merchant>> getMerchants() {
        Query query = db.collection("merchants").orderBy("name");
        return new FirestoreListLiveData<Merchant>(query, Merchant.class);
    }

    @Override
    public LiveData<Subscription> getSubscription(String subscriptionId) {
        return new FirestoreLiveData<>(getMerchantDoc().collection("subscriptions").document(subscriptionId), Subscription.class);
    }

    @Override
    public LiveData<Product> getProduct(String productId) {
        return new FirestoreLiveData<>(getMerchantDoc().collection("offeredProducts").document(productId), Product.class);
    }
}
