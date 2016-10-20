/*
 * Copyright 2016 Todd Burgess Media
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.toddburgessmedia.stackoverflowretrofit;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 05/07/16.
 */
public class QuestionPageSizePreference extends EditTextPreference {

    Context context;

    public QuestionPageSizePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.context = context;
    }

    public QuestionPageSizePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    public QuestionPageSizePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public QuestionPageSizePreference(Context context) {
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

        if (tags > 50) {
            Toast.makeText(context, "Maximum Questions is 50", Toast.LENGTH_SHORT).show();
            returnString = "30";
        } else if (tags < 1) {
            Toast.makeText(context, "Minimum Questions is 1", Toast.LENGTH_SHORT).show();
            returnString = "1";
        }

        super.setText(returnString);
    }

}
