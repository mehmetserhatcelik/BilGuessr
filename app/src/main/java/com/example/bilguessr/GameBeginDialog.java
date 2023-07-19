package com.example.bilguessr;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class GameBeginDialog extends AppCompatDialogFragment {

    boolean isReady;
    HotPursuit context;

    public GameBeginDialog(HotPursuit singlePlayer)
    {
        this.context=singlePlayer;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog, null);

        view.findViewById(R.id.as).setOnClickListener(this::getReady);

        isReady = false;
        builder.setView(view);
        return builder.create();
    }
    public void getReady(View view)
    {
        dismiss();
        isReady = true;
        context.pb();

    }
    public boolean isReady()
    {
        return isReady;
    }
}
