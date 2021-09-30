package com.zopnote.android.merchant.analytics;

import java.util.Map;

/**
 * Created by nmohideen on 01/03/18.
 */

public interface IAnalytics {

    void logEvent(String eventName, Map<String, Object> params);

}
