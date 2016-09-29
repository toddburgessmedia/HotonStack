package com.toddburgessmedia.stackoverflowretrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.toddburgessmedia.stackoverflowretrofit.eventbus.StackExchangeRankingMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class StackExchangeRankingDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tags_dialog_ranking)
                .setItems(R.array.tags_ranking, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new StackExchangeRankingMessage(which));
                    }
                });
        return builder.create();

    }

}
