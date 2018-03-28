package sample.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


public class Album {

    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleIntegerProperty artistID;

    public Album(){
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.artistID = new SimpleIntegerProperty();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getArtistID() {
        return artistID.get();
    }

    public void setArtistID(int artistID) {
        this.artistID.set(artistID);
    }




}
