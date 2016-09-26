package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 25/09/16.
 */

public class StackExchangeSortDialog extends DialogFragment {


    StackExchangeSortDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = (Activity) context;
        listener = (StackExchangeSortDialogListener) a;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setTitle(R.string.tags_dialog_sort)
        .setItems(R.array.tags_sortby, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.stackExchangeSortpositiveClick(StackExchangeSortDialog.this, which);
                }
            });
        return builder.create();

        }

    public interface StackExchangeSortDialogListener {
        void stackExchangeSortpositiveClick(DialogFragment fragment, int which);
    }

}
