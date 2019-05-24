package aka_ecliptic.com.cinephile.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

//TODO: Possible change to my own implementation of List
public class MediaObjectHelper {

    private static final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    public static List<String> asList(Media m){

        ArrayList<String> list = new ArrayList<>();

        list.add(convertSeen(m.isSeen()));
        list.add(Integer.toString(m.getReleaseDate()));
        list.add(m.getTitle());
        list.add(Integer.toString(m.getRating()));
        list.add(m.getGenre().toString());

        return list;
    }

    public static Movie fromList(int id, List<String> list){
        return new Movie(
            id,
            convertSeen(list.get(0)),
            Integer.parseInt(list.get(1)),
            list.get(2),
            Integer.parseInt(list.get(3)),
            Genre.valueOf(list.get(4))
            );
    }

    public static String convertSeen(boolean s){
        if(s){
            return "1";
        }else {
            return "0";
        }
    }

    public static boolean convertSeen(String s){
        return s.equals("1");
    }

    public static boolean releaseDateEquals(Date date1, Date date2){
        return formatDate(date1).equals(formatDate(date2));
    }

    public static String formatDate(Date date){
        return format.format(date);
    }

    public static Date parseDate(String date){
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
