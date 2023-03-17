package akaecliptic.dev.cinephile.adapter.explore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import akaecliptic.dev.cinephile.fragment.ExploreFragment;
import akaecliptic.dev.cinephile.fragment.ExploreSectionFragment;

public class ExploreSectionAdapter extends FragmentStateAdapter {

    public ExploreSectionAdapter(ExploreFragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ExploreSectionFragment(Section.get(position));
    }

    @Override
    public int getItemCount() {
        return Section.size();
    }

    public enum Section {
        PLAYING("now playing"),
        UPCOMING("upcoming"),
        POPULAR("popular"),
        RATED("top rated");

        private final String title;

        Section(String title) {
            this.title = title;
        }

        public static Section get(int index) {
            return values()[index];
        }

        public static int size() {
            return values().length;
        }

        @NonNull
        @Override
        public String toString() {
            return this.title;
        }
    }
}
