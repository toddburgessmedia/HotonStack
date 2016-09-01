package com.toddburgessmedia.stackoverflowretrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class StackExchangeRankingDialog extends DialogFragment {

    StackExchangeRankingDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a = (Activity) context;
        listener = (StackExchangeRankingDialogListener) a;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tags_dialog_ranking)
                .setItems(R.array.tags_ranking, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.stackExchangeRankingpositiveClick(StackExchangeRankingDialog.this, which);
                    }
                });
        return builder.create();

    }

    public interface StackExchangeRankingDialogListener {
        void stackExchangeRankingpositiveClick(DialogFragment fragment, int which);
    }
}
