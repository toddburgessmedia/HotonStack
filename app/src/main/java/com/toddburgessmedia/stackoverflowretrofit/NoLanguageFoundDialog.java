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

import com.toddburgessmedia.stackoverflowretrofit.eventbus.NoLanguageFoundMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 26/06/16.
 */
public class NoLanguageFoundDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.nothing_dialog_title);
            builder.setMessage(R.string.nothing_dialog_body_github);
            builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EventBus.getDefault().post(new NoLanguageFoundMessage(true));
                        }
                    }
            );
            builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EventBus.getDefault().post(new NoLanguageFoundMessage(false));
                }
            });

            return builder.create();
        }

    }



