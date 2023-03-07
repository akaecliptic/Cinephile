package akaecliptic.dev.cinephile.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Collection implements Serializable {

    private int _id;
    private String name;
    private String cover;
    private Set<Integer> members;

    public Collection(int _id, String name, String cover, Set<Integer> members) {
        this._id = _id;
        this.name = name;
        this.cover = cover;
        this.members = members;
    }

    public Collection(int _id, String name, String cover) {
        this._id = _id;
        this.name = name;
        this.cover = cover;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Set<Integer> getMembers() {
        return members;
    }

    public void setMembers(Set<Integer> members) {
        this.members = members;
    }
}
