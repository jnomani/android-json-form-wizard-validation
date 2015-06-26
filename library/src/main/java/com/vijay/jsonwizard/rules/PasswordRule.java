package com.vijay.jsonwizard.rules;

import android.content.Context;
import android.widget.EditText;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Juzer Nomani on 6/25/2015.
 */
public class PasswordRule extends QuickRule<EditText> {


    /* This is the same map found in the AnnotationRule equivalent of PasswordRule.
    * We use it to determine the Password Scheme*/
    private final Map<Password.Scheme, String> SCHEME_PATTERNS =
            new HashMap<Password.Scheme, String>() {{
                put(Password.Scheme.ANY, ".+");
                put(Password.Scheme.ALPHA, "\\w+");
                put(Password.Scheme.ALPHA_MIXED_CASE, "(?=.*[a-z])(?=.*[A-Z]).+");
                put(Password.Scheme.NUMERIC, "\\d+");
                put(Password.Scheme.ALPHA_NUMERIC, "(?=.*[a-zA-Z])(?=.*[\\d]).+");
                put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).+");
                put(Password.Scheme.ALPHA_NUMERIC_SYMBOLS,
                        "(?=.*[a-zA-Z])(?=.*[\\d])(?=.*([^\\w])).+");
                put(Password.Scheme.ALPHA_NUMERIC_MIXED_CASE_SYMBOLS,
                        "(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*([^\\w])).+");
            }};

    private String mScheme;
    private int minLength;

    /**
     * You must supply a scheme, as well as a minimum length (which can be set to zero if not required)
     * @param scheme Password Scheme
     * @param minLength Minimum password length
     */
    public PasswordRule(Password.Scheme scheme, int minLength){
        mScheme = SCHEME_PATTERNS.get(scheme);
        this.minLength = minLength;
    }

    public boolean isValid(EditText view) {
        String pwd = view.getText().toString();
        return pwd.matches(mScheme) && pwd.length() >= minLength;
    }

    @Override
    public String getMessage(Context context) {
        return "Invalid password";
    }
}
