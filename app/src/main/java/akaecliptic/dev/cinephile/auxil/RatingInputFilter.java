package akaecliptic.dev.cinephile.auxil;

import android.text.InputFilter;
import android.text.Spanned;

public class RatingInputFilter implements InputFilter {

    private final int min;
    private final int max;

    public RatingInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned destination, int destinationStart, int destinationEnd) {
        if (destination.toString().equals("0")) return "";

        try {
            int input = Integer.parseInt(destination + source.toString());
            if (inRange(input)) return null;
        } catch (NumberFormatException ignored) { }

        return "";
    }

    private boolean inRange(int value) {
        return min <= value && value <= max;
    }
}
