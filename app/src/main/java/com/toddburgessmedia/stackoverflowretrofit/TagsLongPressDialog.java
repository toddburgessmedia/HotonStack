package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class TagsLongPressDialog extends DialogFragment {

    TagsLongPressDialogListener listener;
    String tagname;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (TagsLongPressDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(tagname)
                .setItems(R.array.tag_longpress_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.longPresspositiveClick(TagsLongPressDialog.this, which);
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

    public interface TagsLongPressDialogListener {
        public void longPresspositiveClick(DialogFragment fragment, int which);

        public void longPresstnegativeClick(DialogFragment fragment, int which);
    }
}
