package akaecliptic.dev.cinephile.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import akaecliptic.dev.cinephile.Architecture.MovieViewModel;
import akaecliptic.dev.cinephile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyListFragment extends Fragment {

    private static final String COLLECTIONS_TAB_HEADING = "Collections";

    static final String SELECTED_MOVIE = "SELECTED_MOVIE";
    static final String SELECTED_SAVED = "SELECTED_SAVED";
    static final String SELECTED_TYPE = "SELECTED_TYPE";

    private static List<NotifyMyList> subscribers = new ArrayList<>();

    private List<String> collectionHeadings;

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
            String title = (position == collectionHeadings.size()) ?
                    COLLECTIONS_TAB_HEADING : collectionHeadings.get(position);
            tab.setText(title);
        }).attach();
    }

    private void getHeadingsFromViewModel() {
        collectionHeadings = new ViewModelProvider(requireActivity()).get(MovieViewModel.class).getCollectionHeadings();

        //TODO: Change/Remove limit as other media types are implemented.
            collectionHeadings = new ArrayList<>(collectionHeadings);
            int limit = 2;
            collectionHeadings.removeIf(s -> collectionHeadings.indexOf(s) >= limit);
    }

    public static void updateCacheMyList(){
        subscribers.forEach(NotifyMyList::onNotify);
    }

    static void addSubscriber(NotifyMyList notifyMyList){
        subscribers.add(notifyMyList);
    }

    public class MyListCollectionAdapter extends FragmentStateAdapter {
        MyListCollectionAdapter(Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            String heading;

            if(position < collectionHeadings.size()){
                fragment = new CollectionsFragment();
                heading = collectionHeadings.get(position);
            }else{
                fragment = new ViewCollectionsFragment();
                heading = COLLECTIONS_TAB_HEADING;
            }

            Bundle b = new Bundle();

            b.putString(CollectionsFragment.INSTANCE_NAME, heading);
            fragment.setArguments(b);

            return fragment;
        }

        @Override
        public int getItemCount() {
            return collectionHeadings.size() + 1;
        }
    }

    public interface NotifyMyList{
        void onNotify();
    }
}
