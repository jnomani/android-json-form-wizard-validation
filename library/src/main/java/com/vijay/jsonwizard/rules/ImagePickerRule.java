package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.widget.ImageView;

import com.mobsandgeeks.saripaar.QuickRule;
import com.vijay.jsonwizard.R;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class ImagePickerRule extends QuickRule<ImageView> {

    @Override
    /* If the ImageView does not have the imagePath tag set,
    we assume the user has not picked an image*/
    public boolean isValid(ImageView view) {
        Object tag = view.getTag(R.id.imagePath);
        return tag != null;
    }

    @Override
    public String getMessage(Context context) {
        return "You must pick an image!";
    }
}
