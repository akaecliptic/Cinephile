package akaecliptic.dev.cinephile.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import akaecliptic.dev.cinephile.Auxiliary.RatingInputFilter;
import akaecliptic.dev.cinephile.Interface.Listener.MovieChangeListener;
import akaecliptic.dev.cinephile.R;
import dev.akaecliptic.models.Movie;

public class RatingDialog extends DialogFragment {

    private final Movie working;
    private MovieChangeListener movieChangeListener;

    public RatingDialog(Movie movie) {
        this.working = movie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rating, null);

        Dialog dialog = builder.setView(view).create();

        TextView title = view.findViewById(R.id.rating_dialog_text_title);
        EditText rating = view.findViewById(R.id.rating_dialog_edit_rating);

        Button confirm = view.findViewById(R.id.rating_dialog_button_confirm);
        Button cancel = view.findViewById(R.id.rating_dialog_button_cancel);

        title.setText(requireContext().getText(R.string.dialog_title_rating));
        rating.setText(String.valueOf(working.getUserRating()));
        rating.setFilters(new InputFilter[]{ new RatingInputFilter(0, 100) });

        confirm.setOnClickListener(v -> {
            int value = Integer.parseInt(rating.getText().toString());
            working.setUserRating(value);

            movieChangeListener.onChange(working);
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }

    public void setMovieChangeListener(MovieChangeListener movieChangeListener) {
        this.movieChangeListener = movieChangeListener;
    }
}
