package sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import sample.model.Album;
import sample.model.Artist;
import sample.model.DataSource;

import java.io.IOException;

import java.util.List;
import java.util.Optional;


public class Controller {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label info;

    @FXML
    private TableView artistsTable;

    @FXML
    private ProgressBar progBar;


//    private void showDialogWindow(){
//         Dialog<ButtonType> dialog = new Dialog<>();
//         FXMLLoader loader = new FXMLLoader();
//         DialogController dialogController = loader.getController();
//        dialog.initOwner(mainBorderPane.getScene().getWindow());
//        dialog.setTitle("Update a record");
//
//        loader.setLocation(getClass().getResource("UpdateDialogPane.fxml"));
//
//        try{
//            dialog.getDialogPane().setContent(loader.load());
//        } catch (IOException e){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("ERROR");
//            alert.setHeaderText("Error with loading dialogPane");
//            alert.showAndWait();
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
//        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//
//    }


    public void initialize() {

        if (DataSource.getInstance().open()) {
            info.setText("Baza danych zostala zaladowana");
            info.setVisible(true);


        } else {
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setHeaderText("Błąd wczytywania bazy danych");
            alert1.setTitle("WARNING");
            alert1.showAndWait();
            System.out.println("Error with loading a database");
        }

    }


    class getAllArtists extends Task {

        @Override
        public ObservableList<Artist> call() throws Exception {
            return FXCollections.observableArrayList(DataSource.getInstance().queryArtists(DataSource.ORDER_BY_ASC));
        }
    }

    @FXML
    public void listArtists() {
        Task<ObservableList<Artist>> task = new getAllArtists();
        artistsTable.itemsProperty().bind(task.valueProperty());

        progBar.progressProperty().bind(task.progressProperty());

        progBar.setVisible(true);

        task.setOnSucceeded(event -> progBar.setVisible(false));
        task.setOnFailed(event -> progBar.setVisible(false));

        new Thread(task).start();
    }

    @FXML
    public void showListOfAlbums() {

        final Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();

        if (artist == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFORMACJA");
            alert.setHeaderText("Jesli chcesz wyswietlic albumy, wybierz artyste z listy");
            alert.showAndWait();
        } else {
            Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
                @Override
                protected ObservableList<Album> call() throws Exception {
                    return FXCollections.observableArrayList(DataSource.getInstance().queryAlbumForArtistID(artist.getId()));
                }
            };
            artistsTable.itemsProperty().bind(task.valueProperty());

            new Thread(task).start();
        }
    }

    @FXML
    public void updateArtist() {

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Update a record");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("UpdateDialogPane.fxml"));
        DialogController dialogController = loader.getController();

        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Error with loading dialogPane");
            alert.showAndWait();
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();
        List<Album> albums = DataSource.getInstance().queryAlbumForArtistID(artist.getId());
        dialog.showAndWait();


        String artistName = artist.getName();
        Album album = albums.get(0);

        System.out.println(artistName +" , "+album.getName());
}


        @FXML
        public void addNewRecord(){

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.setTitle("Update a record");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("UpdateDialogPane.fxml"));
            DialogController dialogController = loader.getController();

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            try{
                dialog.getDialogPane().setContent(loader.load());
            } catch (IOException e) {
                System.out.println("Error with dialog pane " + e.getMessage());
            }


        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){

            System.out.println(dialogController.getAlbumNameLabel().getText());
            System.out.println(dialogController.getNameLabel().getText());

        }
        }

    }

