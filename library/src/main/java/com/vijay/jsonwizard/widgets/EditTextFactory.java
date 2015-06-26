package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

/**
 * Created by vijay on 24-05-2015.
 */
public class EditTextFactory implements FormWidgetFactory {


    /*
    In order to facilitate passwords, and make it easier to get correct input from users, we can specify the input type
    of the EditText in the JSON.

    Supported types:
        E-mail,
        Confirm E-mail,
        Password,
        Confirm Password,
        Name,
        Postal Address
     */
    private static final Map<String, Integer> sInputType = new HashMap<String, Integer>(){{
        put("email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        put("confirm_email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        put("password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        put("confirm_password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        put("name", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        put("postal", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        put("", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        put(null, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
    }};



    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject, CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                R.layout.item_edit_text, null);
        editText.setHint(jsonObject.getString("hint"));
        editText.setFloatingLabelText(jsonObject.getString("hint"));
        editText.setId(ViewUtil.generateViewId());
        editText.setTag(R.id.key, jsonObject.getString("key"));
        editText.setTag(R.id.type, jsonObject.getString("type"));
        if (!TextUtils.isEmpty(jsonObject.optString("value"))) {
            editText.setText(jsonObject.optString("value"));
        }
        // Get the input type from the JSON Object and set it in the EditText:
        editText.setInputType(sInputType.get(jsonObject.optString("input-type")));
        editText.addTextChangedListener(new GenericTextWatcher(stepName, editText));
        views.add(editText);
        return views;
    }
}
