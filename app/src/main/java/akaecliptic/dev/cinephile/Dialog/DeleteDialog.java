package akaecliptic.dev.cinephile.Dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import akaecliptic.dev.cinephile.Interface.MovieChangeListener;
import akaecliptic.dev.cinephile.R;
import dev.akaecliptic.models.Movie;

public class DeleteDialog extends DialogFragment {

    private final Movie working;
    private MovieChangeListener movieChangeListener;

    public DeleteDialog(Movie movie) {
        this.working = movie;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);

        Dialog dialog = builder.setView(view).create();

        TextView title = view.findViewById(R.id.delete_dialog_text_title);
        TextView target = view.findViewById(R.id.delete_dialog_text_target_title);

        Button confirm = view.findViewById(R.id.delete_dialog_button_confirm);
        Button cancel = view.findViewById(R.id.delete_dialog_button_cancel);

        title.setText(requireContext().getText(R.string.dialog_title_delete));
        target.setText(String.format("'%s'", working.getTitle()));

        confirm.setOnClickListener(v -> {
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
