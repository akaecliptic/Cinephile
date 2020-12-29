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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import aka_ecliptic.com.cinephile.Adapter.MyListAdapter;
import aka_ecliptic.com.cinephile.Architecture.MediaViewModel;
import aka_ecliptic.com.cinephile.MainActivity;
import aka_ecliptic.com.cinephile.Model.Movie;
import aka_ecliptic.com.cinephile.R;

import static aka_ecliptic.com.cinephile.Architecture.Repository.Sort;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private static List<NotifyMyList> subscribers = new ArrayList<>();

    private List<String> collectionHeadings;

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";

    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getHeadingsFromViewModel();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_mylist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyListCollectionAdapter myListCollectionAdapter = new MyListCollectionAdapter(this);
        ViewPager2 viewPager = view.findViewById(R.id.mylist_pager);
        viewPager.setAdapter(myListCollectionAdapter);

        TabLayout tabLayout = view.findViewById(R.id.mylist_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(collectionHeadings.get(position));
        }).attach();
    }

    private void getHeadingsFromViewModel() {
        collectionHeadings = new ViewModelProvider(requireActivity()).get(MediaViewModel.class).getCollectionHeadings();
    }

    public static void updateCacheMyList(){
        subscribers.forEach(NotifyMyList::onNotify);
    }

    public class MyListCollectionAdapter extends FragmentStateAdapter {
        MyListCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = new CollectionsFragment();
            Bundle b = new Bundle();

            b.putString(CollectionsFragment.INSTANCE_NAME, collectionHeadings.get(position));
            fragment.setArguments(b);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return collectionHeadings.size();
        }
    }

    public static class CollectionsFragment extends Fragment {
        static final String INSTANCE_NAME = "INSTANCE";

        private String fragName;

        private MediaViewModel mediaViewModel;
        private Sort currentSort = Sort.DEFAULT;
        private List<Movie> fragList;

        private MyListAdapter adapter;
        private TextView emptyPrompt;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setUpViewModelLink();
            assignInstanceName();
            return inflater.inflate(R.layout.fragment_collections, container, false);
        }


        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            setUpRecycler();
        }

        @Override
        public void onResume() {
            MainActivity.setSortClickListener(() -> {
                currentSort = mediaViewModel.cycleSort(currentSort);
                fragList = mediaViewModel.sortList(fragList, currentSort);

                Toast.makeText(requireActivity(), currentSort.getSortType(), Toast.LENGTH_SHORT).show();

                adapter.setItems(fragList);
            });

            super.onResume();
        }

        private void setUpViewModelLink() {
            mediaViewModel = new ViewModelProvider(requireActivity()).get(MediaViewModel.class);
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
            if(fragName.equals("All")){
                fragList = new ArrayList<>(mediaViewModel.reCacheItems());
            }else {
                fragList = mediaViewModel.getItemsInCollection(fragName);
            }

            RecyclerView recyclerView = requireView().findViewById(R.id.collections_recycler);
            adapter = new MyListAdapter(requireContext(), fragList);

            emptyPrompt = requireView().findViewById(R.id.collections_text_empty_prompt);
            checkPrompt();

            setUpAdapterListeners();

            subscribers.add(() -> {
                if(fragName.equals("All")){
                    fragList = new ArrayList<>(mediaViewModel.reCacheItems());
                }else {
                    fragList = mediaViewModel.getItemsInCollection(fragName);
                }

                adapter.setItems(fragList);
                checkPrompt();
            });

            mediaViewModel.addSubscriber((update, destructive) -> {
                int index = fragList.indexOf(update);
                if(index >= 0){
                    if(destructive){
                        fragList.removeIf(m -> m.getId() == update.getId());
                        adapter.notifyItemRemoved(index);
                    }else {
                        fragList.set(index, update);
                        adapter.notifyItemChanged(index);
                    }
                }
                checkPrompt();
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        private void setUpAdapterListeners() {
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
                mediaViewModel.notifyClones(m, false);
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
                    fragList.remove(m);
                    adapter.notifyItemRemoved(p);
                    mediaViewModel.notifyClones(m, true);

                    dialog.dismiss();
                });

                cancel.setOnClickListener(view -> dialog.cancel());

                return true;
            });
        }
    }

    public interface NotifyMyList{
        void onNotify();
    }
}
