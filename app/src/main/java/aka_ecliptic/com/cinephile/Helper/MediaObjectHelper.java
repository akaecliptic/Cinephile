package aka_ecliptic.com.cinephile.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import aka_ecliptic.com.cinephile.Model.Genre;
import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

public class MediaObjectHelper {

    private static final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private static final Calendar calendar = new GregorianCalendar();

    public static List<String> movieAsList(Movie m){

        ArrayList<String> list = new ArrayList<>();

        list.add(convertSeen(m.isSeen()));
        list.add(stringDate(m.getReleaseDate()));
        list.add(m.getTitle());
        list.add(Integer.toString(m.getRating()));
        list.add(m.getGenre().toString());
        list.add(m.getSubGenre().toString());
        list.add(m.getMinGenre().toString());

        return list;
    }

    public static Movie movieFromList(int id, List<String> list){
        return new Movie(
            id,
            convertSeen(list.get(0)),
            parseDate(list.get(1)),
            list.get(2),
            Integer.parseInt(list.get(3)),
            Genre.valueOf(list.get(4)),
            Genre.valueOf(list.get(5)),
            Genre.valueOf(list.get(6))
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
        return stringDate(date1).equals(stringDate(date2));
    }

    public static String stringDate(Date date){
        return format.format(date);
    }

    public static String dateYear(Date date){
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.UK);
        return year.format(date);
    }

    public static Date parseDate(String date){
        try {
            if(date.length() > 4)
                return format.parse(date);
            calendar.set(Integer.parseInt(date), 0, 1);
            return calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int checkInt(String in){
        if(in != null && !in.isEmpty()){
            try{
                return Integer.parseInt(in);
            }catch (NumberFormatException e){
                return -1;
            }
        }
        return -1;
    }
}
