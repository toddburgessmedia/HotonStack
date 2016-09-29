package com.toddburgessmedia.stackoverflowretrofit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.toddburgessmedia.stackoverflowretrofit.eventbus.SelectSiteMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 01/07/16.
 */
public class SiteSelectDialog extends DialogFragment {

    SiteSelectDialogListener listener;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
//        Activity a = (Activity) context;
//        listener = (SiteSelectDialogListener) a;
//
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.site_select_title)
                .setItems(R.array.select_select_display, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //listener.siteSelectpositiveClick(SiteSelectDialog.this, which);
                        EventBus.getDefault().post(new SelectSiteMessage(which));
                    }
                });
        return builder.create();

    }

    public interface SiteSelectDialogListener {
        void siteSelectpositiveClick(DialogFragment fragment, int which);
    }
}
