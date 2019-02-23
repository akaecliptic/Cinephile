package aka_ecliptic.com.cinephile.Helper;

import java.util.ArrayList;
import java.util.List;

import aka_ecliptic.com.cinephile.Model.Media;
import aka_ecliptic.com.cinephile.Model.Movie;

//TODO: Possible change to my own implementation of List
public class MediaListConverter {

    public static List<String> asList(Media m){

        ArrayList<String> list = new ArrayList<>();

        list.add(convertSeen(m.isSeen()));
        list.add(Integer.toString(m.getYear()));
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
            Media.Genre.valueOf(list.get(4))
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
}
