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
