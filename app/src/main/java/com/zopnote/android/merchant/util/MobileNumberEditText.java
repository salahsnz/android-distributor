package com.zopnote.android.merchant.util;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.Toast;

public class MobileNumberEditText extends AppCompatAutoCompleteTextView {

    public MobileNumberEditText(Context context) {
        super(context);
    }

    public MobileNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MobileNumberEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            case android.R.id.paste:
                String pasteData = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard.hasPrimaryClip()) {
                        if (clipboard.getPrimaryClipDescription().hasMimeType(android.content.ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                            android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                            pasteData = item.getText().toString();
                        }
                    }
                } else {
                    // deprecated class
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard.hasText()) {
                        pasteData = clipboard.getText().toString();
                    }
                }
                if (pasteData != null) {
                    String cleanedMobileNumber = getCleanedMobileNumber(pasteData);
                    if (cleanedMobileNumber != null) {
                        this.setText(cleanedMobileNumber);
                        // reset error if any
                        this.setError(null);
                    } else {
                        Toast.makeText(getContext(), "Clipboard does not contain a valid 10 digit mobile number.", Toast.LENGTH_SHORT).show();
                    }
                }
                // processing complete
                return true;
            default:
                return super.onTextContextMenuItem(id);
        }
    }

    public String getCleanedMobileNumber(String input) {
        if (input != null && input.trim().length() != 0) {
            String cleanedNumber = input.trim().replaceAll("^\\+91", "").replaceAll("^0", "").replaceAll("[\\s\\-()]", "");
            if (isValidMobileNumber(cleanedNumber)) {
                return cleanedNumber;
            }
        }
        return null;
    }

    public boolean isValidMobileNumber(String input) {
        // starts with 6,7,8,9 and contains 10 digits total
        return input.matches("^[6789]\\d{9}$");
    }
}