package com.zopnote.android.merchant;

import android.content.Context;
import android.support.annotation.NonNull;

import com.zopnote.android.merchant.analytics.FirebaseBasedAnalytics;
import com.zopnote.android.merchant.analytics.IAnalytics;
import com.zopnote.android.merchant.data.remote.FirestoreDataSource;
import com.zopnote.android.merchant.data.repository.Repository;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by nmohideen on 26/12/17.
 */

public class Injection {

    public static Repository provideRepository(@NonNull String merchantId) {
        checkNotNull(merchantId);
        return Repository.getInstance(FirestoreDataSource.getInstance(merchantId));
    }

    public static IAnalytics provideAnalytics(@NonNull Context context) {
        checkNotNull(context);
        return new FirebaseBasedAnalytics(context);
    }
}
