package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.widget.Spinner;

import com.mobsandgeeks.saripaar.QuickRule;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class SpinnerRule extends QuickRule<Spinner> {


    /*
    In the future, it would be helpful for each spinner to have its own custom
    error messages so that the user is given contextual advice rather than a generic message.

        private String mMessage;
        public SpinnerRule(String message){
            mMessage = message
        }

     */

    @Override
    /* A hint supplied in MaterialSpinner is given the index 0.
     * Thus we want the index of the item to be greater than the index of the hint */
    public boolean isValid(Spinner view) {
        return view.getSelectedItemPosition() > 0;
    }

    @Override
    public String getMessage(Context context) {
        // return mMessage;
        return "You must select a spinner option!";
    }
}
