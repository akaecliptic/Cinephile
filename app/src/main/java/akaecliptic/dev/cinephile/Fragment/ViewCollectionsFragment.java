package akaecliptic.dev.cinephile.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.List;

import akaecliptic.dev.cinephile.Adapter.ViewCollectionsAdapter;
import akaecliptic.dev.cinephile.Architecture.MovieViewModel;
import akaecliptic.dev.cinephile.R;

import static akaecliptic.dev.cinephile.Fragment.CollectionsFragment.INSTANCE_NAME;

@Deprecated
public class ViewCollectionsFragment extends Fragment {
    private String fragName;

    private MovieViewModel viewModel;
    private List<String> fragList;

    private ViewCollectionsAdapter adapter;
    private TextView emptyPrompt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUpViewModelLink();
        assignInstanceName();
        return inflater.inflate(R.layout.old_fragment_view_collections, container, false);
    }

    @Override
    public void onResume() {
//        MainActivity.setSortClickListener(() -> { });

        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpRecycler();
    }

    private void setUpViewModelLink() {
        viewModel = new ViewModelProvider(requireActivity()).get(MovieViewModel.class);
    }

    private void assignInstanceName() {
        if (getArguments() != null) {
            fragName = getArguments().getString(INSTANCE_NAME);
        }else {
            fragName = "";
        }
    }

    private void checkPrompt(){
        emptyPrompt.setVisibility((fragList.isEmpty()) ? View.VISIBLE : View.GONE);
    }

    private void setUpRecycler() {
        fragList = viewModel.getCollectionNames();
        Collections.reverse(fragList);

        GridView gridView = requireView().findViewById(R.id.view_collections_grid);
        adapter = new ViewCollectionsAdapter(requireContext(), fragList);

        emptyPrompt = requireView().findViewById(R.id.view_collections_text_empty_prompt);
        checkPrompt();

        FloatingActionButton addCollection = requireView().findViewById(R.id.view_collections_button_add);
        addCollection.setOnClickListener(this::createAddCollectionDialog);

        setUpAdapterListeners();

        gridView.setAdapter(adapter);
    }

    private void createAddCollectionDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_new_collection, (ViewGroup) requireView(), false));

        AlertDialog newCollection = builder.create();
        newCollection.show();

        Button confirm = newCollection.findViewById(R.id.new_collection_dialog_button_confirm);
        Button cancel = newCollection.findViewById(R.id.new_collection_dialog_button_cancel);

        confirm.setOnClickListener(v -> {
            EditText collection = newCollection.findViewById(R.id.new_collection_dialog_text_collection_title);
            String toCreate = collection.getText().toString();

            boolean allowed = !(viewModel.getCollectionHeadings().contains(toCreate) || viewModel.getCollectionNames().contains(toCreate));

            if(allowed){
                viewModel.addCollection(toCreate);
                fragList.add(toCreate);
                adapter.notifyDataSetChanged();
                newCollection.dismiss();
            }else {
                Toast.makeText(requireContext(), "Collection name is already in use. Try another.", Toast.LENGTH_LONG).show();
            }
        });

        cancel.setOnClickListener(v -> newCollection.cancel());
    }

    private void setUpAdapterListeners() {
        adapter.setCollectionClick(position -> {
            Bundle b = new Bundle();

            b.putString(CollectionsFragment.INSTANCE_NAME, fragList.get(position));
//            Navigation.findNavController(requireView())
//                    .navigate(R.id.action_mylist_fragment_to_collections_fragment, b);

            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                    "Opening '" + fragList.get(position) + "'",
                    Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
            snackbar.show();
        });

        adapter.setCollectionLongClick(this::createDeleteCollectionDialog);
    }

    private boolean createDeleteCollectionDialog(int position) {
        String target = adapter.getItem(position);

        if (target.equals("Favourites"))
            return true;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_delete, (ViewGroup) requireView(), false));

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView title = dialog.findViewById(R.id.delete_dialog_text_title);
        TextView movieTitle = dialog.findViewById(R.id.delete_dialog_text_target_title);

        title.setText(R.string.dialog_title_delete);
        movieTitle.setText(target);

        Button confirm = dialog.findViewById(R.id.delete_dialog_button_confirm);
        Button cancel = dialog.findViewById(R.id.delete_dialog_button_cancel);

        confirm.setOnClickListener(view -> {
            viewModel.deleteCollection(target);
            fragList.remove(target);
            adapter.notifyDataSetChanged();

            dialog.dismiss();
        });

        cancel.setOnClickListener(view -> dialog.cancel());

        return true;
    }
}
