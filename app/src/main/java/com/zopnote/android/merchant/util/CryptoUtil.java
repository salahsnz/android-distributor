package com.zopnote.android.merchant.util;

import com.crashlytics.android.Crashlytics;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Ravindra on 4/10/2016.
 */
public class CryptoUtil {

    private static native String getKey1();
    public static String decrypt(byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfKey = md.digest(getKey1().getBytes("UTF-8"));
            final byte[] keyBytes = Arrays.copyOf(digestOfKey, 24);
            for (int j = 0, k = 16; j < 8; ) {
                keyBytes[k++] = keyBytes[j++];
            }
            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            final byte[] plainText = decipher.doFinal(data);
            return new String(plainText, "UTF-8");
        } catch (Throwable t) {
            Crashlytics.getInstance().logException(t);
        }
        return null;
    }
}
