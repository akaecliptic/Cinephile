package aka_ecliptic.com.cinephile.Model;

import java.io.Serializable;

public class Descriptor implements Serializable {
    private String description;
    private String studio;

    public Descriptor(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}