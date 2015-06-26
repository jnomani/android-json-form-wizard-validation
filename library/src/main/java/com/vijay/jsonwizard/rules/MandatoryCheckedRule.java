package com.vijay.jsonwizard.rules;

import android.content.Context;

import com.mobsandgeeks.saripaar.QuickRule;
import com.vijay.jsonwizard.customviews.CheckBox;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class MandatoryCheckedRule extends QuickRule<CheckBox> {
    @Override
    public boolean isValid(CheckBox view) {
        return view.isChecked();
    }

    @Override
    public String getMessage(Context context) {
        return "This item must be checked!";
    }
}
