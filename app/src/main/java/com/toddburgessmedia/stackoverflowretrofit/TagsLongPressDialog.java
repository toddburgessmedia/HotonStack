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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.toddburgessmedia.stackoverflowretrofit.eventbus.MainActivityLongPressMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class TagsLongPressDialog extends DialogFragment {

    String tagname;

    public boolean isTagsearch() {
        return tagsearch;
    }

    public void setTagsearch(boolean tagsearch) {
        this.tagsearch = tagsearch;
    }

    boolean tagsearch;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int searchmenu = 0;
        if (isTagsearch()) {
            searchmenu = R.array.tag_longpress_dialog;
        } else {
            searchmenu = R.array.tag_longpress_dialog_back;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(tagname)
                .setItems(searchmenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //listener.longPresspositiveClick(TagsLongPressDialog.this, which);
                        EventBus.getDefault().post(new MainActivityLongPressMessage(which));
                    }
                });
        return builder.create();

    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

}
