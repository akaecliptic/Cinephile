package aka_ecliptic.com.cinephile.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import aka_ecliptic.com.cinephile.Adapter.MyListAdapter;
import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.MainActivity;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;
import aka_ecliptic.com.cinephile.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private static NotifyMyList notify;

    private MediaViewModel mediaViewModel;
    private BottomNavigationView bottomNavigationView;

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";

    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setUpViewModelLink();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_mylist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpRecycler();
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = requireActivity().findViewById(R.id.mylist_recycler);
        MyListAdapter adapter = new MyListAdapter(requireContext(), mediaViewModel.getItems());

        TextView emptyPrompt = requireView().findViewById(R.id.mylist_text_empty_list);
        emptyPrompt.setVisibility((mediaViewModel.getItems().isEmpty()) ? View.VISIBLE : View.GONE);

        adapter.setClickListener((v, p) -> {
            Bundle bundle = new Bundle();

            bundle.putSerializable(SELECTED_MOVIE, adapter.getItem(p));
            bundle.putBoolean(SELECTED_SAVED, true);

            Navigation.findNavController(requireView())
                    .navigate(R.id.action_mylist_fragment_to_movie_profile_fragment, bundle);

            Snackbar snackbar = Snackbar.make(requireActivity().findViewById(R.id.main_coordinator),
                    "Opening '" + adapter.getItem(p).getTitle() + "'",
                    Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(requireActivity().getColor(R.color.colorSecondaryDark));
            snackbar.show();
        });

        adapter.setCheckBoxListener((v, p) -> {
            Movie m = adapter.getItem(p);
            m.setSeen(!m.isSeen());
            mediaViewModel.updateItem(m);
        });

        adapter.setOnLongClickListener((v, p) -> {
            Movie m = adapter.getItem(p);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            builder.setView(inflater.inflate(R.layout.dialog_delete, null));

            AlertDialog dialog = builder.create();
            dialog.show();

            TextView title = dialog.findViewById(R.id.delete_dialog_text_title);
            TextView movieTitle = dialog.findViewById(R.id.delete_dialog_text_movie_title);

            title.setText(R.string.dialog_title_delete);
            movieTitle.setText(m.getTitle());

            Button confirm = dialog.findViewById(R.id.delete_dialog_button_confirm);
            Button cancel = dialog.findViewById(R.id.delete_dialog_button_cancel);

            confirm.setOnClickListener(view -> {
                mediaViewModel.deleteItem(m);
                adapter.notifyItemRemoved(p);

                dialog.dismiss();
            });

            cancel.setOnClickListener(view -> dialog.cancel());

            return true;
        });

        MainActivity.setSortClickListener(() -> {
            mediaViewModel.cycleSort();

            Toast.makeText(requireActivity(), mediaViewModel.getCurrentSort(), Toast.LENGTH_SHORT).show();

            adapter.setItems(mediaViewModel.getItems());
        });

        notify = () -> adapter.setItems(mediaViewModel.reCacheItems());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    public static void updateCacheMyList(){
        notify.onNotify();
    }

    public interface NotifyMyList{
        void onNotify();
    }
}
