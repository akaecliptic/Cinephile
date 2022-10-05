package akaecliptic.dev.cinephile.Super;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import akaecliptic.dev.cinephile.Architecture.ViewModel;

/**
 * A personal implementation of Base class extending {@link Fragment} class.
 * This is an attempt to abstract some of the common methods being reused.
 */
public abstract class BaseFragment extends Fragment {

    protected final String TAG = getClass().getSimpleName();
    protected ViewModel viewModel;
    @LayoutRes protected int resource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResource();
        return inflater.inflate(resource, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initAdapter(view);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModel.class);
    }

    public abstract void initAdapter(View view);
    public abstract void setResource();
}
