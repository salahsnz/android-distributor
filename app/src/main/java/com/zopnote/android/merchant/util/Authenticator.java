package com.zopnote.android.merchant.util;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Authenticator {

    private static final String HMAC_ALGORITHM = "HmacMD5";
    private static final String DIGEST_ALGORITHM = "MD5";
    private static final String AUTH_HEADER = "x-ireff-1";
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";

    private static final String SECRET_ALGO_1 = "1";
    private static final String ID_KEY_1 = "1";

    private static final String LOG_TAG = Authenticator.class.getSimpleName();
    private static final boolean DEBUG = false;

    private String baseUri;
    private SortedMap<String, String> paramsMap;
    private String timestamp;
    // default for GET; this is what we get from the InputStream on the server side
    private String body = "";
    private String lastModified;

    public Authenticator(String baseUri) {
        this.baseUri = baseUri;
    }

    public Authenticator addParameter(String query, String value) {
        if (paramsMap == null) {
            paramsMap = new TreeMap<String, String>();
        }
        paramsMap.put(query, value);
        return this;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUri() {
        StringBuilder buf = new StringBuilder();
        buf.append(baseUri);
        String queryString = getQueryString();
        if (queryString != null) {
            buf.append("?");
            buf.append(queryString);
        }
        return buf.toString();
    }

    public Map<String, String> getVolleyHttpHeaders() {
        Map<String, String> headers = new HashMap<String, String>(1);
        String authHeaderValue = getAuthHeaderValue();
        headers.put(AUTH_HEADER, authHeaderValue);
        if (DEBUG) Log.d(LOG_TAG, AUTH_HEADER + ": " + authHeaderValue);
        if (lastModified != null) {
            headers.put(HEADER_IF_MODIFIED_SINCE, lastModified);
            if (DEBUG) Log.d(LOG_TAG, HEADER_IF_MODIFIED_SINCE + ": " + lastModified);
        }
        return headers;
    }

    private String getAuthHeaderValue() {
        return SECRET_ALGO_1 + " " + ID_KEY_1 + " " + getTimestamp() + " "  + getSignature();
    }

    private String getQueryString() {
        if (paramsMap == null) {
            return null;
        }

        try {
            StringBuilder buf = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                buf.append(URLEncoder.encode(entry.getKey(), UTF8_CHARSET));
                buf.append("=");
                buf.append(URLEncoder.encode(entry.getValue(), UTF8_CHARSET));
                if (iterator.hasNext()) {
                    buf.append("&");
                }
            }
            return buf.toString();
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
    }

    private String getTimestamp() {
        if (timestamp == null) {
            Calendar cal = Calendar.getInstance();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            timestamp = dateFormat.format(cal.getTime());
        }
        return timestamp;
    }

    private native String getKey1();
    private String getSignature() {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(getKey1().getBytes(UTF8_CHARSET), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(getCanonicalSigningString().getBytes(UTF8_CHARSET));
            String base64Hmac = Base64.encodeToString(rawHmac, Base64.DEFAULT | Base64.NO_WRAP);
            return base64Hmac;
        } catch (Exception e) {
            if (DEBUG) Log.d(LOG_TAG, "Error getting signature: " + e.getMessage());
            return "";
        }
    }

    private String getCanonicalSigningString() {
        StringBuilder buf = new StringBuilder();

        // query string
        String queryString = getQueryString();
        if (queryString != null) {
            buf.append(queryString);
        }
        buf.append("\n");

        // timestamp
        buf.append(getTimestamp());
        buf.append("000"); // lame obfuscation!
        buf.append("\n");

        // path
        try {
            String path = new URI(baseUri).getRawPath();
            if (path != null) {
                buf.append(path);
            }
            buf.append("\n");
        } catch (URISyntaxException e) {
            if (DEBUG) Log.d(LOG_TAG, "Error getting path: " + e.getMessage());
        }

        // body
        try {
            byte[] rawBodyHash = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(body.getBytes(UTF8_CHARSET));
            String bodyHash = Base64.encodeToString(rawBodyHash, Base64.DEFAULT | Base64.NO_WRAP);
            buf.append(bodyHash);
        } catch (NoSuchAlgorithmException e) {
            if (DEBUG) Log.d(LOG_TAG, "Error getting body hash: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            if (DEBUG) Log.d(LOG_TAG, "Error getting body hash: " + e.getMessage());
        }

        String canonical = buf.toString();
        if (DEBUG) Log.d(LOG_TAG, "canonical signing string: " + canonical);
        return canonical;
    }
}