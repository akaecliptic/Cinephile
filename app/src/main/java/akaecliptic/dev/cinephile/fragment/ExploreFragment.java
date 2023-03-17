package akaecliptic.dev.cinephile.fragment;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.adapter.explore.ExploreSectionAdapter;
import akaecliptic.dev.cinephile.adapter.explore.ExploreSectionAdapter.Section;
import akaecliptic.dev.cinephile.base.BaseFragment;

public class ExploreFragment extends BaseFragment {

    @Override
    protected void setResource() {
        this.resource = R.layout.fragment_explore;
    }

    @Override
    protected void initViews(View view) {
        ExploreSectionAdapter adapter = new ExploreSectionAdapter(this);

        TabLayout tabLayout = view.findViewById(R.id.explore_tab_layout);
        ViewPager2 pager = view.findViewById(R.id.explore_pager);

        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, (tab, position) ->
                tab.setText(Section.get(position).toString())
        ).attach();
    }
}
