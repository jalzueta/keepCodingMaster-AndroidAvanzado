package com.fillingapps.twittnearby.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fillingapps.twittnearby.callbacks.OnInfoDialogCallback;

import java.lang.ref.WeakReference;

public class InfoDialogFragment extends DialogFragment {

    protected WeakReference<OnInfoDialogCallback> mOnInfoDialogCallback;

    public static final String ARG_TITLE_ID = "com.fillingapps.imq.fragments.dialogs.InfoDialogFragment.ARG_TITLE_ID";
    public static final String ARG_MESSAGE_ID = "com.fillingapps.imq.fragments.dialogs.InfoDialogFragment.ARG_MESSAGE_ID";
    public static final String ARG_BUTTON_TEXT_ID = "com.fillingapps.imq.fragments.dialogs.InfoDialogFragment.ARG_BUTTON_TEXT_ID";

    private int mTitleId;
    private int mMessageId;
    private int mButtonTextId;
    private String mButtonText;

    public static InfoDialogFragment newInstance(int titleId, int messageId, int buttonTextId) {
        InfoDialogFragment f = new InfoDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_ID, titleId);
        args.putInt(ARG_MESSAGE_ID, messageId);
        args.putInt(ARG_BUTTON_TEXT_ID, buttonTextId);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitleId = getArguments().getInt(ARG_TITLE_ID);
        mMessageId = getArguments().getInt(ARG_MESSAGE_ID);
        mButtonTextId = getArguments().getInt(ARG_BUTTON_TEXT_ID);

        mButtonText = getResources().getString(mButtonTextId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

        dialog.setTitle(mTitleId);
        dialog.setMessage(mMessageId);

        dialog.setPositiveButton(mButtonTextId, null);

        return dialog.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setText(mButtonText);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInfoDialogCallback.get().onInfoDialogClosed(InfoDialogFragment.this);
                }
            });
        }
    }

    public void setOnInfoDialogCallback(OnInfoDialogCallback onInfoDialogCallback) {
        mOnInfoDialogCallback = new WeakReference<>(onInfoDialogCallback);
    }
}
