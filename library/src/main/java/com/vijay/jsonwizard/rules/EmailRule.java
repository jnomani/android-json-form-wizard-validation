package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.QuickRule;

import commons.validator.routines.EmailValidator;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class EmailRule extends QuickRule<EditText> {
    @Override
    /* Replicate behavior of AnnotationRule equivalent. Validates email using apache Email Validator */
    public boolean isValid(EditText view) {
        EmailValidator ev = EmailValidator.getInstance(false);
        String email = view.getText().toString();
        return ev.isValid(email);
    }

    @Override
    public String getMessage(Context context) {
        return "Invalid E-mail";
    }
}
