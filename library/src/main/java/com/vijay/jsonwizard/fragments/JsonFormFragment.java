package com.vijay.jsonwizard.fragments;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.mvp.MvpFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;
import com.vijay.jsonwizard.rules.EmailRule;
import com.vijay.jsonwizard.rules.ImagePickerRule;
import com.vijay.jsonwizard.rules.MandatoryCheckedRule;
import com.vijay.jsonwizard.rules.NotEmptyRule;
import com.vijay.jsonwizard.rules.PasswordRule;
import com.vijay.jsonwizard.rules.SpinnerRule;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import commons.validator.routines.EmailValidator;

/**
 * Created by vijay on 5/7/15.
 */
public class JsonFormFragment extends MvpFragment<JsonFormFragmentPresenter, JsonFormFragmentViewState> implements
        CommonListener, JsonFormFragmentView<JsonFormFragmentViewState>, Validator.ValidationListener {
    private static final String TAG = "JsonFormFragment";
    private LinearLayout mMainView;
    private Menu mMenu;
    private JsonApi mJsonApi;
    private Validator mFormValidator;
    private int mValidatedViews = 0;

    @Override
    public void onAttach(Activity activity) {
        mJsonApi = (JsonApi) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mFormValidator = new Validator(this);
        mFormValidator.setValidationListener(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_json_wizard, null);
        mMainView = (LinearLayout) rootView.findViewById(R.id.main_layout);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.addFormElements();
    }

    @Override
    protected JsonFormFragmentViewState createViewState() {
        return new JsonFormFragmentViewState();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        menu.clear();
        inflater.inflate(R.menu.menu_toolbar, menu);
        presenter.setUpToolBar();
    }

    @Override
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            presenter.onBackClick();
            return true;
        } else if (item.getItemId() == R.id.action_next) {
            // Instead of calling next, we first validate the input if there is input to be validated.
            if (mValidatedViews > 0)
                mFormValidator.validate();
            else
                presenter.onNextClick(mMainView);
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            presenter.onSaveClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        presenter.onClick(v);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDetach() {
        mJsonApi = null;
        super.onDetach();
    }

    @Override
    public void updateRelevantImageView(Bitmap bitmap, String imagePath, String currentKey) {
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);
            if (view instanceof ImageView) {
                ImageView imageView = (ImageView) view;
                String key = (String) imageView.getTag(R.id.key);
                if (key.equals(currentKey)) {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setTag(R.id.imagePath, imagePath);
                }
            }
        }
    }

    @Override
    public void writeValue(String stepName, String key, String s) {
        try {
            mJsonApi.writeValue(stepName, key, s);
        } catch (JSONException e) {
            // TODO - handle
            e.printStackTrace();
        }
    }

    @Override
    public void writeValue(String stepName, String prentKey, String childObjectKey, String childKey, String value) {
        try {
            mJsonApi.writeValue(stepName, prentKey, childObjectKey, childKey, value);
        } catch (JSONException e) {
            // TODO - handle
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject getStep(String stepName) {
        return mJsonApi.getStep(stepName);
    }

    @Override
    public String getCurrentJsonState() {
        return mJsonApi.currentJsonState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter();
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public CommonListener getCommonListener() {
        return this;
    }

    @Override
    public void addFormElements(List<View> views) {
        for (View view : views) {
            /*
            Here is where things get interesting. Since we do not know what views are present in the layout
            until runtime, it is not feasible to use Annotation Rules. Instead we must use quick rules.
            Unfortunately, the library itself does not have any Quick Rules, so we must create our own.
            I simply replicate the behavior of the corresponding Annotation Rules. See the Rules package.
             */

            if(view instanceof EditText) {
                switch ((String) view.getTag(R.id.key)) {
                /*
                Currently I determine the rule to use based on the key. However, it would probably be more
                proper to include a field in the JSON for "input-type" and base the rule off of that.
                 */
                    case "email":
                        mFormValidator.put((EditText) view, new EmailRule());
                        mValidatedViews++;
                        break;
                    case "password":
                        mFormValidator.put((EditText) view, new PasswordRule(
                                /* You can change this according to the scheme
                                See PasswordRule for more schemes */
                                Password.Scheme.ALPHA_MIXED_CASE,
                                /* Minimum length of password */ 6));
                        mValidatedViews++;
                        break;
                    default:
                        // For demonstration purpose, require every input field mandatory.
                        mFormValidator.put((EditText) view, new NotEmptyRule());
                        mValidatedViews++;
                }
            }else if(view instanceof Spinner){
                //By default Spinner choice is required, however we can further filter this by adding a field in JSON
                mFormValidator.put((Spinner) view, new SpinnerRule());
                mValidatedViews++;
            }else if(view instanceof CheckBox){
                mFormValidator.put((CheckBox) view, new MandatoryCheckedRule());
                mValidatedViews++;
            }else if(view instanceof ImageView){
                String key = (String) view.getTag(R.id.type);
                if(key != null && key.equals("choose_image")) {
                    mFormValidator.put((ImageView) view, new ImagePickerRule());
                    mValidatedViews++;
                }
            }
            mMainView.addView(view);
        }
    }

    @Override
    public ActionBar getSupportActionBar() {
        return ((JsonFormActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public Toolbar getToolbar() {
        return ((JsonFormActivity) getActivity()).getToolbar();
    }

    @Override
    public void setToolbarTitleColor(int colorId) {
        getToolbar().setTitleTextColor(getContext().getResources().getColor(colorId));
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        mMenu.findItem(R.id.action_next).setVisible(next);
        mMenu.findItem(R.id.action_save).setVisible(save);
    }

    @Override
    public void hideKeyBoard() {
        super.hideSoftKeyboard();
    }

    @Override
    public void backClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void unCheckAllExcept(String parentKey, String childKey) {
        int childCount = mMainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mMainView.getChildAt(i);
            if (view instanceof RadioButton) {
                RadioButton radio = (RadioButton) view;
                String parentKeyAtIndex = (String) radio.getTag(R.id.key);
                String childKeyAtIndex = (String) radio.getTag(R.id.childKey);
                if (parentKeyAtIndex.equals(parentKey) && !childKeyAtIndex.equals(childKey)) {
                    radio.setChecked(false);
                }
            }
        }
    }

    @Override
    public String getCount() {
        return mJsonApi.getCount();
    }

    @Override
    public void finishWithResult(Intent returnIntent) {
        getActivity().setResult(Activity.RESULT_OK, returnIntent);
        getActivity().finish();
    }

    @Override
    public void setUpBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void transactThis(JsonFormFragment next) {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left,
                        R.anim.exit_to_right).replace(R.id.container, next)
                .addToBackStack(next.getClass().getSimpleName()).commit();
    }

    public static JsonFormFragment getFormFragment(String stepName) {
        JsonFormFragment jsonFormFragment = new JsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString("stepName", stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        presenter.onCheckedChanged(buttonView, isChecked);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        presenter.onItemSelected(parent, view, position, id);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    /* This method is automatically called by the validator if
    all the views pass validation. For now, we just move to the next step, and show a Toast */
    public void onValidationSucceeded() {
        Toast.makeText(getContext(), "Validation succeeded!", Toast.LENGTH_SHORT).show();
        presenter.onNextClick(mMainView);
    }

    @Override
    /* For now, I used the default error handling that was on the Git.
    This behavior can be customized for each view if we choose. */
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
