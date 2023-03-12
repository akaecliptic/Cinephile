package akaecliptic.dev.cinephile.dialog;

import static akaecliptic.dev.cinephile.fragment.WatchlistFragment.FAV;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.adapter.CollectionsArrayAdapter;
import akaecliptic.dev.cinephile.base.BaseFragment;
import akaecliptic.dev.cinephile.data.ViewModel;
import akaecliptic.dev.cinephile.interaction.listener.OnCollectionCreated;
import akaecliptic.dev.cinephile.interaction.listener.OnCollectionSelected;
import dev.akaecliptic.models.Movie;

public class AddCollectionDialog extends DialogFragment {

    private final Movie working;
    private final BaseFragment parent;

    private OnCollectionSelected onCollectionSelected;
    private OnCollectionCreated onCollectionCreated;
    private CollectionsArrayAdapter adapter;
    private ViewModel viewModel;

    public AddCollectionDialog(Movie movie, BaseFragment parent) {
        this.working = movie;
        this.parent = parent;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_collection, null);

        Dialog dialog = builder.setView(view).create();

        ListView list = view.findViewById(R.id.add_collection_dialog_list_collections);
        adapter = new CollectionsArrayAdapter(requireContext(), this.working.getId());

        adapter.setOnCollectionSelected(this.onCollectionSelected);
        list.setAdapter(adapter);

        Button createNew = view.findViewById(R.id.add_collection_dialog_button_new);
        Button done = view.findViewById(R.id.add_collection_dialog_button_done);

        createNew.setOnClickListener(v -> {
            NewCollectionDialog newCollectionDialog = new NewCollectionDialog(working.getId());
            newCollectionDialog.setOnCollectionAdded(this.onCollectionCreated);
            newCollectionDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
        });
        done.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this.parent.requireActivity()).get(ViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.viewModel.collections().observe(this.parent.getViewLifecycleOwner(), collections -> {
            collections.sort((c1, c2) -> {
                if (c1.getName().equals(FAV)) return -1;
                if (c2.getName().equals(FAV)) return 1;
                return c1.getName().compareTo(c2.getName());
            });
            this.adapter.setItems(collections);
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void setOnCollectionSelected(OnCollectionSelected onCollectionSelected) {
        this.onCollectionSelected = onCollectionSelected;
    }

    public void setOnCollectionCreated(OnCollectionCreated onCollectionCreated) {
        this.onCollectionCreated = onCollectionCreated;
    }

    public static class NewCollectionDialog extends DialogFragment {

        private final int working;
        private OnCollectionCreated onCollectionCreated;

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

            confirm.setOnClickListener(v -> onCollectionCreated.add(working, title.getText().toString(), this::dismiss));
            cancel.setOnClickListener(v -> dialog.dismiss());

            return dialog;
        }

        public void setOnCollectionAdded(OnCollectionCreated onCollectionCreated) {
            this.onCollectionCreated = onCollectionCreated;
        }
    }
}
