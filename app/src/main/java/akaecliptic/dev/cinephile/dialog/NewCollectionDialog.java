package akaecliptic.dev.cinephile.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.interaction.listener.OnCollectionAdded;

public class NewCollectionDialog extends DialogFragment {

    private final int working;
    private OnCollectionAdded onCollectionAdded;

    public NewCollectionDialog(int movie) {
        this.working = movie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_collection, null);

        Dialog dialog = builder.setView(view).create();

        EditText title = view.findViewById(R.id.new_collection_dialog_edit_title);

        Button confirm = view.findViewById(R.id.new_collection_dialog_button_confirm);
        Button cancel = view.findViewById(R.id.new_collection_dialog_button_cancel);

        confirm.setOnClickListener(v -> {
            if (onCollectionAdded.add(working, title.getText().toString())) dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public void setOnCollectionAdded(OnCollectionAdded onCollectionAdded) {
        this.onCollectionAdded = onCollectionAdded;
    }
}
