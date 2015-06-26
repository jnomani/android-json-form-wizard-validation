package com.vijay.jsonwizard.presenters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.customviews.CheckBox;
import com.vijay.jsonwizard.customviews.RadioButton;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interactors.JsonFormInteractor;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.views.JsonFormFragmentView;
import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

/**
 * Created by vijay on 5/14/15.
 */
public class JsonFormFragmentPresenter extends MvpBasePresenter<JsonFormFragmentView<JsonFormFragmentViewState>>{
    private static final String TAG = "FormFragmentPresenter";
    private static final int RESULT_LOAD_IMG = 1;
    private String mStepName;
    private JSONObject mStepDetails;
    private String mCurrentKey;
    private JsonFormInteractor mJsonFormInteractor = JsonFormInteractor.getInstance();

    public JsonFormFragmentPresenter(){

    }

    public void addFormElements() {
        mStepName = getView().getArguments().getString("stepName");
        JSONObject step = getView().getStep(mStepName);
        try {
            mStepDetails = new JSONObject(step.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<View> views = mJsonFormInteractor.fetchFormElements(mStepName, getView().getContext(), mStepDetails,
                getView().getCommonListener());



        getView().addFormElements(views);
    }

    public void setUpToolBar() {
        if (!mStepName.equals(JsonFormConstants.FIRST_STEP_NAME)) {
            getView().setUpBackButton();
        }
        getView().setActionBarTitle(mStepDetails.optString("title"));
        if (mStepDetails.has("next")) {
            getView().updateVisibilityOfNextAndSave(true, false);
        } else {
            getView().updateVisibilityOfNextAndSave(false, true);
        }
        getView().setToolbarTitleColor(R.color.white);
    }

    public void onBackClick() {
        getView().hideKeyBoard();
        getView().backClick();
    }

    public void onNextClick(LinearLayout mainView) {
        int childCount = mainView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = mainView.getChildAt(i);
            String key = (String) childAt.getTag(R.id.key);
            if (childAt instanceof MaterialEditText) {
                MaterialEditText editText = (MaterialEditText) childAt;
                getView().writeValue(mStepName, key, editText.getText().toString());
            } else if (childAt instanceof ImageView) {
                Object imagePath = childAt.getTag(R.id.imagePath);
                if (imagePath instanceof String) {
                    getView().writeValue(mStepName, key, (String) imagePath);
                }
            } else if (childAt instanceof CheckBox) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                        String.valueOf(((CheckBox) childAt).isChecked()));
            } else if (childAt instanceof RadioButton) {
                String parentKey = (String) childAt.getTag(R.id.key);
                String childKey = (String) childAt.getTag(R.id.childKey);
                if (((RadioButton) childAt).isChecked()) {
                    getView().writeValue(mStepName, parentKey, childKey);
                }
            }
        }
        JsonFormFragment next = JsonFormFragment.getFormFragment(mStepDetails.optString("next"));
        getView().hideKeyBoard();
        getView().transactThis(next);
    }

    public void onSaveClick() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("json", getView().getCurrentJsonState());
        getView().finishWithResult(returnIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // No need for null check on cursor
            Cursor cursor = getView().getContext().getContentResolver()
                    .query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            getView().updateRelevantImageView(BitmapFactory.decodeFile(imagePath), imagePath, mCurrentKey);
            cursor.close();
        }
    }

    public void onClick(View v) {
        String key = (String) v.getTag(R.id.key);
        String type = (String) v.getTag(R.id.type);
        if (JsonFormConstants.CHOOSE_IMAGE.equals(type)) {
            getView().hideKeyBoard();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            mCurrentKey = key;

            getView().startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton instanceof CheckBox) {
            String parentKey = (String) compoundButton.getTag(R.id.key);
            String childKey = (String) compoundButton.getTag(R.id.childKey);
            getView().writeValue(mStepName, parentKey, JsonFormConstants.OPTIONS_FIELD_NAME, childKey,
                    String.valueOf(((CheckBox) compoundButton).isChecked()));
        } else if (compoundButton instanceof RadioButton) {
            if (isChecked) {
                String parentKey = (String) compoundButton.getTag(R.id.key);
                String childKey = (String) compoundButton.getTag(R.id.childKey);
                getView().unCheckAllExcept(parentKey, childKey);
                getView().writeValue(mStepName, parentKey, childKey);
            }
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String parentKey = (String) parent.getTag(R.id.key);
        if (position > 0) {
            String value = (String) parent.getItemAtPosition(position + 1);
            getView().writeValue(mStepName, parentKey, value);
        }
    }

}
