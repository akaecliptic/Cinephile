package akaecliptic.dev.cinephile.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import akaecliptic.dev.cinephile.data.ViewModel;

/**
 * A personal implementation of Base class extending {@link Fragment} class.
 * This is an attempt to abstract some of the common methods being reused.
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    protected ViewModel viewModel;
    @LayoutRes protected int resource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResource();
        beforeViews();
        return inflater.inflate(resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        afterViews(view);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
    }

    protected void beforeViews() { }
    protected void afterViews(View view) { }

    protected abstract void initViews(View view);
    protected abstract void setResource();
}
