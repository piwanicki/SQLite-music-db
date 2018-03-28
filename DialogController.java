package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import sample.model.Album;
import sample.model.Artist;
import sample.model.DataSource;

import java.sql.SQLException;

public class DialogController {

    @FXML
    private Label labelName;

    @FXML
    private Label labelAlbumName;

    @FXML
    private TableView<Album> albumTableView;

    public void updateRecord(Artist artist, Album album){

        labelName.setText(artist.getName());
        labelAlbumName.setText(album.getName());

        String newArtist = labelName.getText();
        String newAlbum = labelAlbumName.getText();

        artist.setName(newArtist);
        album.setName(newAlbum);

    }

    public void addNewRecorddc() {

        String artistName = labelName.getText().trim();
        String albumName = labelAlbumName.getText().trim();

        Artist artist = new Artist();
        artist.setName(artistName);

        Album album = new Album();

        try {
            int id = DataSource.getInstance().insertArtist(artistName);
            int albumId = DataSource.getInstance().insertAlbum(artistName,id);
            artist.setId(id);
            album.setName(albumName);
            album.setArtistID(id);
            album.setId(albumId);

        } catch (SQLException e) {
            System.out.println("Error with adding a new record : " + e.getMessage());
        }
    }


    public Label getNameLabel() {
        return labelName;
    }


    public Label getAlbumNameLabel() {
        return labelAlbumName;
    }


    public TableView<Album> getAlbumTableView() {
        return albumTableView;
    }


}
