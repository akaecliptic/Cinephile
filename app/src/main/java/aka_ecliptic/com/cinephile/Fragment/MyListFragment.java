package aka_ecliptic.com.cinephile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import aka_ecliptic.com.cinephile.Adapter.MyListAdapter;
import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private MediaViewModel mediaViewModel;

    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mylist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpViewModelLink();
        setUpRecycler();
    }

    private void setUpViewModelLink() {
        mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = requireActivity().findViewById(R.id.mylist_recycler);
        MyListAdapter adapter = new MyListAdapter(requireContext(), mediaViewModel.getItems());

        adapter.setClickListener((v, p) -> {
            Toast.makeText(requireContext(), adapter.getItem(p).getTitle().concat(" Clicked"), Toast.LENGTH_SHORT).show();
        });

        adapter.setCheckBoxListener((v, p) -> {
            Movie m = adapter.getItem(p);
            m.setSeen(!m.isSeen());
            mediaViewModel.updateItem(m);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
