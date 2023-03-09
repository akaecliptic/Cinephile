package akaecliptic.dev.cinephile.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.List;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.adapter.CollectionsArrayAdapter;
import akaecliptic.dev.cinephile.interaction.listener.OnCollectionSelected;
import akaecliptic.dev.cinephile.model.Collection;
import dev.akaecliptic.models.Movie;

public class AddCollectionDialog extends DialogFragment {

    private final Movie working;
    private final List<Collection> collections;

    private OnCollectionSelected onCollectionSelected;

    public AddCollectionDialog(Movie movie, List<Collection> collections) {
        this.working = movie;
        this.collections = collections;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_collection, null);

        Dialog dialog = builder.setView(view).create();

        TextView title = view.findViewById(R.id.add_collection_dialog_text_title);
        ListView list = view.findViewById(R.id.add_collection_dialog_list_collections);

        CollectionsArrayAdapter adapter = new CollectionsArrayAdapter(requireContext(), this.collections, this.working.getId());
        adapter.setOnCollectionSelected(this.onCollectionSelected);
        list.setAdapter(adapter);

        Button createNew = view.findViewById(R.id.add_collection_dialog_button_new);
        Button done = view.findViewById(R.id.add_collection_dialog_button_done);

        title.setText(requireContext().getText(R.string.dialog_title_add_collection).toString());

        createNew.setOnClickListener(v -> {
            // TODO: 2023-03-09 Add Functionality
            System.out.println("New Collection");
            dialog.dismiss();
        });
        done.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public void setOnCollectionSelected(OnCollectionSelected onCollectionSelected) {
        this.onCollectionSelected = onCollectionSelected;
    }
}
