package akaecliptic.dev.cinephile.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// CONSIDER: 2023-03-07 Renaming class, completely forgot about java collections...
public class Collection implements Serializable {

    private String _name;
    private Cover cover;
    private Set<Integer> members;

    public Collection(String _name, Cover cover) {
        this._name = _name;
        this.cover = cover;
        this.members = new HashSet<>();
    }

    public Collection(String _name) {
        this._name = _name;
        this.cover = Cover.DEFAULT;
        this.members = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Collection { " +
                "name: '" + this._name + '\'' +
                ", cover: '" + this.cover + '\'' +
                ", members: " + this.members +
                " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collection)) return false;

        Collection collection = (Collection) o;
        return this._name.equals(collection._name) &&
                Objects.equals(this.cover, collection.cover) &&
                Objects.equals(this.members, collection.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._name, this.cover, this.members);
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
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

    public boolean hasMember(int id) {
        return this.members.contains(id);
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
