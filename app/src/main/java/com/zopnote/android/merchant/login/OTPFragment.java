package com.zopnote.android.merchant.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.util.Validatable;

public class OTPFragment extends Fragment implements TextWatcher, Validatable {


    public static OTPFragment newInstance() {
        OTPFragment fragment = new OTPFragment();
        return fragment;
    }
    public OTPFragment() {
        // Requires empty public constructor
    }

    private EditText editText_one,editText_two,editText_three,editText_four;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view == null) {
            view = inflater.inflate(R.layout.otp_fragment, container, false);
        }

        editText_one = view.findViewById(R.id.editTextone);
        editText_one.addTextChangedListener(this);

        editText_two = view.findViewById(R.id.editTexttwo);
        editText_two.addTextChangedListener(this);

        editText_three = view.findViewById(R.id.editTextthree);
        editText_three.addTextChangedListener(this);

        editText_four = view.findViewById(R.id.editTextfour);
        editText_four.addTextChangedListener(this);

        view.findViewById(R.id.resendOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.viewmodel.reSendOTP();
            }
        });

        view.findViewById(R.id.changeNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.viewmodel.isOTPSend = false;
                LoginActivity.viewmodel.step.setValue(3);
            }
        });

        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (editText_one.length() == 1) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 1) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 1) {
                editText_four.requestFocus();
            }
        } else if (editable.length() == 0) {
            if (editText_four.length() == 0) {
                editText_three.requestFocus();
            }
            if (editText_three.length() == 0) {
                editText_two.requestFocus();
            }
            if (editText_two.length() == 0) {
                editText_one.requestFocus();
            }
        }
    }


    @Override
    public boolean validate() {
        return validateFields();
    }

    private boolean validateFields() {
        if (validate(editText_one)&& validate(editText_two) && validate(editText_three) && validate(editText_four)) {
            LoginActivity.viewmodel.otp = editText_one.getText().toString().trim() + editText_two.getText().toString().trim()+
                    editText_three.getText().toString().trim()+ editText_four.getText().toString().trim();
            return true;
        }
        Toast.makeText(getContext(),"Enter OTP",Toast.LENGTH_LONG).show();
        return false;
    }


    public boolean validate(EditText editText) {
        if(editText.equals(editText_one)){
            if (editText_one.getText().toString().trim().length() == 1) {
                return true;
            } else {
                return false;
            }
        }

        if(editText.equals(editText_two)){
            if (editText_two.getText().toString().trim().length() == 1) {
                return true;
            } else {
                return false;
            }
        }

        if(editText.equals(editText_three)){
            if (editText_three.getText().toString().trim().length() == 1) {
                return true;
            } else {
                return false;
            }
        }

        if(editText.equals(editText_four)){
            if (editText_four.getText().toString().trim().length() == 1) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
