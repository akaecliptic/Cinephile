package akaecliptic.dev.cinephile.Model;

@Deprecated
public enum Genre{
    ACTION(28), ADVENTURE(12), ANIMATION(16), COMEDY(35), CRIME(80), DOCUMENTARY(99), DRAMA(18),
    FAMILY(10751), FANTASY(14), HISTORY(36), HORROR(27), MUSIC(10402), MYSTERY(9648), ROMANTIC(10749),
    SCI_FI(878), TV(10770), THRILLER(53), WAR(10752), WESTERN(37), NONE(-1);

    private int genreId;

    Genre(int genreId){
        this.genreId = genreId;
    }

    public static Genre getGenreById(int genreId){
        for (Genre value : Genre.values()) {
            if(genreId == value.getGenreId())
                return value;
        }
        return Genre.NONE;
    }

    public int getGenreId(){
        return this.genreId;
    }
}
