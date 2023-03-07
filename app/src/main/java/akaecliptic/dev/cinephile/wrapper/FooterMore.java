package akaecliptic.dev.cinephile.wrapper;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import akaecliptic.dev.cinephile.R;
import akaecliptic.dev.cinephile.base.BaseMovieAdapter.BaseMovieViewHolder;

// CONSIDER: Making this reusable for other footers in future.
public class FooterMore extends BaseMovieViewHolder {

    private final View parent;
    private Button button;

    public FooterMore(@NonNull View parent) {
        super(parent);
        this.parent = parent;
        init();
    }

    private void init() {
        button = parent.findViewById(R.id.footer_button);
    }

    public View getView() {
        return this.parent;
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.button.setOnClickListener(clickListener);
    }
}
