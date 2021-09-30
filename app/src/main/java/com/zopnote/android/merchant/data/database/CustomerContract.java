package com.zopnote.android.merchant.data.database;

import android.provider.BaseColumns;

public class CustomerContract {

    private CustomerContract() { }

    public static class CustomerEntry implements BaseColumns{
        public static final String FTS_VIRTUAL_TABLE = "FTS";

        public static final String COLUMN_NAME_CUSTOMER_ID = "cid";
        public static final String COLUMN_NAME_CREATED = "created";
        public static final String COLUMN_NAME_MOBILE_NUMBER = "mobileNumber";
        public static final String COLUMN_NAME_FIRST_NAME = "firstName";
        public static final String COLUMN_NAME_LAST_NAME = "lastName";
        public static final String COLUMN_NAME_EMAIL_ID = "emailId";
        public static final String COLUMN_NAME_DOOR_NUMBER = "doorNumber";
        public static final String COLUMN_NAME_ADDRESS_LINE_1 = "addressLine1";
        public static final String COLUMN_NAME_ADDRESS_LINE_2 = "addressLine2";
        public static final String COLUMN_NAME_TOTAL_DUE = "totalDue";
        public static final String COLUMN_NAME_INVOICE_STATUS = "invoiceStatus";

    }

    public static final String SQL_CREATE_VIRTUAL_TABLE =
            "CREATE VIRTUAL TABLE " + CustomerEntry.FTS_VIRTUAL_TABLE +
                    " USING fts4 (" +
                    CustomerEntry._ID + " INTEGER AUTOINCREMENT," +
                    CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " STRING PRIMARY KEY," +
                    "notindexed="+ CustomerEntry.COLUMN_NAME_CUSTOMER_ID + "," +
                    CustomerEntry.COLUMN_NAME_CREATED + " TEXT," +
                    "notindexed="+ CustomerEntry.COLUMN_NAME_CREATED + "," +
                    CustomerEntry.COLUMN_NAME_MOBILE_NUMBER + " TEXT," +
                   // "notindexed="+ CustomerEntry.COLUMN_NAME_MOBILE_NUMBER + "," +
                    CustomerEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    CustomerEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
                    CustomerEntry.COLUMN_NAME_EMAIL_ID + " TEXT," +
                    CustomerEntry.COLUMN_NAME_DOOR_NUMBER + " TEXT," +
                    CustomerEntry.COLUMN_NAME_ADDRESS_LINE_1 + " TEXT," +
                    CustomerEntry.COLUMN_NAME_ADDRESS_LINE_2 + " TEXT," +
                    CustomerEntry.COLUMN_NAME_TOTAL_DUE + " TEXT," +
                    "notindexed="+ CustomerEntry.COLUMN_NAME_TOTAL_DUE + "," +
                    CustomerEntry.COLUMN_NAME_INVOICE_STATUS + " TEXT," +")";

    public static final String SQL_DELETE_FTS_ENTRIES =
            "DROP TABLE IF EXISTS " + CustomerEntry.FTS_VIRTUAL_TABLE;
}
