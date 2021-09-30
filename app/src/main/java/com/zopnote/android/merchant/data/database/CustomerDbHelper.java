package com.zopnote.android.merchant.data.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.zopnote.android.merchant.data.model.Customer;
import com.zopnote.android.merchant.data.model.InvoiceStatusEnum;

import java.util.Date;
import java.util.List;

public class CustomerDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = "CustomerDbHelper";
    private static final boolean DEBUG = false;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "customer.db";

    public CustomerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CustomerContract.SQL_CREATE_VIRTUAL_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if(DEBUG) Log.d(LOG_TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL(CustomerContract.SQL_DELETE_FTS_ENTRIES);
        onCreate(db);
    }

    public void cleanDatabase(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(CustomerContract.SQL_DELETE_FTS_ENTRIES);
        onCreate(db);
    }

    public void storeCustomers(List<Customer> customers){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        for (Customer customer: customers) {
            ContentValues values = new ContentValues();
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_CUSTOMER_ID, customer.getId());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_CREATED, customer.getCreated().getTime());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_MOBILE_NUMBER, getDisplayMobileNumber(customer.getMobileNumber()));
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_FIRST_NAME, customer.getFirstName());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_LAST_NAME, customer.getLastName());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_EMAIL_ID, customer.getEmail());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_DOOR_NUMBER, customer.getDoorNumber());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_1, customer.getAddressLine1());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_2, customer.getAddressLine2());
            values.put(CustomerContract.CustomerEntry.COLUMN_NAME_TOTAL_DUE, customer.getTotalDue());

            if(customer.getInvoiceStatus() != null){
                values.put(CustomerContract.CustomerEntry.COLUMN_NAME_INVOICE_STATUS, customer.getInvoiceStatus().name());
            }

            db.insert(CustomerContract.CustomerEntry.FTS_VIRTUAL_TABLE, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private String getDisplayMobileNumber(String mobileNumber) {
        return mobileNumber.replaceAll("^\\+91", "");
    }

    public Cursor getCustomerMatches(String query) {
        String selection;
        String[] selectionArgs;
        if (query.matches("\\d+(?:\\.\\d+)?") && query.length()>5){

            selection =  CustomerContract.CustomerEntry.FTS_VIRTUAL_TABLE + " MATCH ?";
            selectionArgs = new String[] {CustomerContract.CustomerEntry.COLUMN_NAME_MOBILE_NUMBER+":"+
                    query+"*"};
           // SELECT * FROM table WHERE table MATCH 'A:cat OR C:cat'
        }else {
            selection =  CustomerContract.CustomerEntry.FTS_VIRTUAL_TABLE + " MATCH ?";
            selectionArgs = new String[] {CustomerContract.CustomerEntry.COLUMN_NAME_FIRST_NAME+":"+query+ "*" +
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_LAST_NAME+":"+query+"*"+
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_DOOR_NUMBER+":"+query+"*"+
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_1+":"+query+"*"+
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_2+":"+query+"*"+
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_EMAIL_ID+":"+query+"*"+
                    " OR "+ CustomerContract.CustomerEntry.COLUMN_NAME_INVOICE_STATUS+":"+query+"*"};

        }

        return query(selection, selectionArgs, null);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(CustomerContract.CustomerEntry.FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public static Customer fromCursor(Cursor cursor){
        Customer customer = new Customer();
        customer.setId(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_CUSTOMER_ID)));
        customer.setCreated(new Date(cursor.getLong(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_CREATED))));
        customer.setMobileNumber(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_MOBILE_NUMBER)));
        customer.setFirstName(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_FIRST_NAME)));
        customer.setLastName(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_LAST_NAME)));
        customer.setEmail(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_EMAIL_ID)));
        customer.setDoorNumber(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_DOOR_NUMBER)));
        customer.setAddressLine1(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_1)));
        customer.setAddressLine2(cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_ADDRESS_LINE_2)));
        customer.setTotalDue(cursor.getDouble(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_TOTAL_DUE)));

        String invoiceStatus = cursor.getString(cursor.getColumnIndex(CustomerContract.CustomerEntry.COLUMN_NAME_INVOICE_STATUS));
        if(invoiceStatus != null){
            customer.setInvoiceStatus(InvoiceStatusEnum.valueOf(invoiceStatus));
        }
        return customer;
    }
}
