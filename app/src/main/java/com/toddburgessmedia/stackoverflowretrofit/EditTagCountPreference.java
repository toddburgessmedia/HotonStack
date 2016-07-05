package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/07/16.
 */
public class EditTagCountPreference extends EditTextPreference {

    Context context;

    public EditTagCountPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;
    }

    public EditTagCountPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    public EditTagCountPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public EditTagCountPreference(Context context) {
        super(context);

        this.context = context;
    }

    @Override
    public void setText(String text) {
        String returnString = text;
        int tags = 0;

        try {
            tags = Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            Toast.makeText(context, "Must be a Number", Toast.LENGTH_SHORT).show();
            returnString = getText();
        }

        if (tags > 100) {
            Toast.makeText(context, "Maximum Tags is 100", Toast.LENGTH_SHORT).show();
            returnString = "100";
        } else if (tags < 1) {
            Toast.makeText(context, "Minimum Tag is 1", Toast.LENGTH_SHORT).show();
            returnString = "1";
        }

        super.setText(returnString);
    }

}
