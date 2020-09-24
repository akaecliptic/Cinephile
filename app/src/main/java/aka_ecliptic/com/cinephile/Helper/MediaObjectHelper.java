package aka_ecliptic.com.cinephile.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MediaObjectHelper {

    private static final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
    private static final Calendar calendar = new GregorianCalendar();

    public static int isSeen(boolean s){
        return s ? 1 : 0;
    }

    public static boolean isSeen(int s){
        return s == 1;
    }

    public static boolean releaseDateEquals(Date date1, Date date2){
        return dateToString(date1).equals(dateToString(date2));
    }

    public static String dateToString(Date date){
        return format.format(date);
    }

    public static Date stringToDate(String date){
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

    public static String dateYear(Date date){
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.UK);
        return year.format(date);
    }

    public static String dateYearVert(Date date){
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.UK);
        String before = year.format(date);
        String after = "";
        for (int i = 0; i < before.length(); i++) {
            after = after.concat(before.charAt(i) + "\n");
        }

        return after;
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
