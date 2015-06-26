package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.QuickRule;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class NotEmptyRule extends QuickRule<EditText> {
    @Override
    /* If this rule is applied, the EditText cannot have empty input*/
    public boolean isValid(EditText view) {
        String data = view.getText().toString();
        return data != null &&  !data.isEmpty();
    }

    @Override
    public String getMessage(Context context) {
        return "This field cannot be empty";
    }
}
