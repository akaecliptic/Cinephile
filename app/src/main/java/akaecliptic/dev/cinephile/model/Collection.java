package akaecliptic.dev.cinephile.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

// CONSIDER: 2023-03-07 Renaming class, completely forgot about java collections...
public class Collection implements Serializable {

    private int _id;
    private String name;
    private Cover cover;
    private Set<Integer> members;

    public Collection(int _id, String name, Cover cover) {
        this._id = _id;
        this.name = name;
        this.cover = cover;
        this.members = new HashSet<>();
    }

    public Collection(int _id, String name) {
        this._id = _id;
        this.name = name;
        this.cover = Cover.DEFAULT;
        this.members = new HashSet<>();
    }

    public int getId() {
        return _id;
    }

    public void setId(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cover getCover() {
        return cover;
    }

    public void setCover(Cover cover) {
        this.cover = cover;
    }

    public Set<Integer> getMembers() {
        return members;
    }

    public void setMembers(Set<Integer> members) {
        this.members = members;
    }

    public enum Cover {
        DEFAULT("default"),
        HEART("heart"),
        SPACE("space"),
        GOOD("good"),
        BAD("bad");

        private final String value;

        Cover(String value) {
            this.value = value;
        }

        public static Cover parse(String cover) {
            for (Cover value : Cover.values()) {
                if (value.toString().equals(cover)) return value;
            }

            return Cover.DEFAULT;
        }

        @NonNull
        @Override
        public String toString() {
            return this.value;
        }
    }
}
