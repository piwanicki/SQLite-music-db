package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import sample.model.Album;
import sample.model.Artist;
import sample.model.DataSource;

import java.sql.SQLException;

public class DialogController {


    @FXML
    private TableView<Album> albumTableView;

    @FXML
    private TextField artistName;

    @FXML
    private TextField artistAlbumName;



    public void updateRecord(Artist artist, Album album){


        String newArtist = artistName.getText();
        String newAlbum = artistAlbumName.getText();

        DataSource.getInstance().updateArtistRecord(artist.getName(),newArtist);

    }


    public Artist addNewRecorddc() {

        String artName = artistName.getText().trim();
        String albumName = artistAlbumName.getText().trim();

        Artist artist = new Artist();
        artist.setName(artName);

        Album album = new Album();

        try {
            int id = DataSource.getInstance().insertArtist(artName);
            int albumId = DataSource.getInstance().insertAlbum(artName,id);
            artist.setId(id);
            album.setName(albumName);
            album.setArtistID(id);
            album.setId(albumId);
            return artist;

        } catch (SQLException e) {
            System.out.println("Error with adding a new record : " + e.getMessage());
            return null;
        }
    }

    public TableView<Album> getAlbumTableView() {
        return albumTableView;
    }

    public TextField getArtistName() {
        return artistName;
    }

    public void setArtistName(TextField artistName) {
        this.artistName = artistName;
    }

    public TextField getArtistAlbumName() {
        return artistAlbumName;
    }

    public void setArtistAlbumName(TextField artistAlbumName) {
        this.artistAlbumName = artistAlbumName;
    }
}
