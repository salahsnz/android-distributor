package com.zopnote.android.merchant.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.Validatable;

/**
 * Created by nmohideen on 11/09/17.
 */

// extending android.app.Fragment because AccountKitCustomUIManager requires this
public class MobileNumberFragment extends Fragment implements Validatable {

    public static MobileNumberFragment newInstance() {
        MobileNumberFragment fragment = new MobileNumberFragment();
        return fragment;
    }
    public MobileNumberFragment() {
        // Requires empty public constructor
    }
    private EditText mobileNumber;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.mobile_number_fragment, container, false);
        }

        mobileNumber = view.findViewById(R.id.mobileNumber);
        return view;
    }

    @Override
    public boolean validate() {
        return validateFields();
    }


    private boolean validateFields() {
        if (validate(mobileNumber)) {
            return true;
        }
        return false;
    }

    public boolean validate(EditText editText) {
        LoginActivity.viewmodel.mobileNumber = editText.getText().toString().trim();

         if (LoginActivity.viewmodel.mobileNumber.isEmpty() ) {
            return false;
        } else if (LoginActivity.viewmodel.mobileNumber.matches("^[6789]\\d{9}$")) {
            return true;
        } else {
            editText.setError(getResources().getString(R.string.mobile_number_error_message));
            editText.requestFocus();
            return false;
        }
    }
}
