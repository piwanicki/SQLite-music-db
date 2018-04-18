package sample;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import sample.model.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javafx.scene.input.MouseEvent;



public class Controller {

    @FXML
    private BorderPane mainBorderPane;


    @FXML
    private TableView artistsTable;

    @FXML
    private ProgressBar progBar;

    @FXML
    private Label infoLabel;

    @FXML
    private ContextMenu contextArtistMenu;

    @FXML
    private ContextMenu contextAlbumMenu;

    @FXML
    private ContextMenu contextSongMenu;

    private Service<ObservableList<Artist>> taskService;

    private ObservableList<Artist> list;

    @FXML
    private Tab databaseTab = new Tab("Database");

    @FXML
    private TableView<Album> albumsTable;

    @FXML
    private TableView<Song> songsTable;

    @FXML
    private WebView webInfo;
    private WebEngine webEngine;



    public void  setInfoLabel(String text){
          infoLabel.setVisible(true);
          infoLabel.setText(text);
    }


    public void initialize() {

        webEngine = webInfo.getEngine();


        webInfo.getEngine().setOnAlert(event -> {
            System.out.println(event.getData());
        });

        webInfo.getEngine().setOnError(event -> {
            System.out.println(event.getMessage());
        });
        this.webEngine.load("http://www.google.pl");



        contextArtistMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteRecord();
            }
        });

        editItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateArtist();
            }
        });



        contextArtistMenu.getItems().addAll(deleteMenuItem,editItem);

        artistsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextArtistMenu.show(artistsTable, event.getScreenX(), event.getScreenY());
                }
            }
        });


        contextAlbumMenu = new ContextMenu();
        MenuItem addNewAlbum = new MenuItem("Add new Album");
        MenuItem deleteAlbumItem = new MenuItem("Delete Album");
        MenuItem editAlbumItem = new MenuItem("Edit Album");

        deleteAlbumItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteAlbum();
            }
        });

        editAlbumItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editAlbum();
            }
        });

        addNewAlbum.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //addNewAlbumToAlbumTable();
            }
        });

        contextAlbumMenu.getItems().addAll(deleteAlbumItem,editAlbumItem,addNewAlbum);
        albumsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    contextAlbumMenu.show(albumsTable,event.getScreenX(),event.getScreenY());
                }
            }
        });


        contextSongMenu = new ContextMenu();
        MenuItem addNewSong = new MenuItem("Add new song");
        MenuItem deleteSong = new MenuItem("Delete song");
        MenuItem editSong = new MenuItem("Edit song");

        deleteSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteSongMethod();
            }
        });

        editSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editSongMethod();
            }
        });

        addNewSong.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               // addNewSongToSongTable();
            }
        });

        contextSongMenu.getItems().addAll(deleteSong,editSong,addNewSong);

        songsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.SECONDARY){
                    contextSongMenu.show(songsTable,event.getScreenX(),event.getScreenY());
                }
            }
        });



        artistsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){
                        albumList();
                }
            }
        });



        artistsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue != null){
                    songsTable.getItems().clear();
                    albumList();
                }
            }
        });

        albumsTable.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() == MouseButton.PRIMARY){
                        songList();
                }
            }
        });




        Label songLabel = new Label("Click on album to see songs");
        songLabel.setWrapText(true);
        songLabel.autosize();

        albumsTable.setPlaceholder(new Label("Click on artist to see albums"));
        songsTable.setPlaceholder(songLabel);


        taskService = new TasksServiceClass();
        progBar.progressProperty().bind(taskService.progressProperty());
        progBar.visibleProperty().bind(taskService.runningProperty());

        //databaseTab.getContent().autosize();

        artistsTable.itemsProperty().bind(taskService.valueProperty());

        taskService.start();

        if (DataSource.getInstance().open()) {
            setInfoLabel("Database loaded");
        } else {
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setHeaderText("Błąd wczytywania bazy danych");
            alert1.setTitle("WARNING");
            alert1.showAndWait();
            System.out.println("Error with loading a database");
        }

        artistsTable.getSelectionModel().selectFirst();
    }

    /**
     * Replaced with TaskService class
     */
//    class GetAllArtists extends Task {
//        //private ObservableList<Artist> list = FXCollections.observableArrayList(DataSource.getInstance().queryArtists(DataSource.ORDER_BY_ASC));
//
//        @Override
//        public ObservableList<Artist> call() throws Exception {
//            return list;
//        }
//    }




    @FXML
    public void listArtists() {

        System.out.println(taskService.getState());

        progBar.progressProperty().bind(taskService.progressProperty());
        progBar.visibleProperty().bind(taskService.runningProperty());

        if(taskService.getState() == Service.State.SUCCEEDED){
            artistsTable.itemsProperty().unbind();
            //artistsTable.getItems().clear();
            artistsTable.itemsProperty().bind(taskService.valueProperty());
        }
        else if(taskService.getState() == Service.State.RUNNING){
            artistsTable.itemsProperty().unbind();
            artistsTable.getItems().clear();
            artistsTable.itemsProperty().bind(taskService.valueProperty());
        }


//       progBar.setVisible(true);
//        taskService.setOnSucceeded(event -> progBar.setVisible(false));
//        taskService.setOnFailed(event -> progBar.setVisible(false));
       // artistsTable.itemsProperty().bind(taskService.valueProperty());
      //  new Thread(task).start();
    }


    @FXML
    public void showListOfAlbums() {

        final Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();

        if (artist == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("INFORMATION");
            alert.setHeaderText("If you want to see artist's albums, you have to select first an artist");
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
        FXMLLoader dialogloader = new FXMLLoader(getClass().getResource("UpdateDialogPane.fxml"));

        try {
            dialog.getDialogPane().setContent(dialogloader.load());
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
        Album album = albums.get(0);

        String artistName = artist.getName();
        System.out.println(artistName +" , "+album.getName());
        DialogController dialogController = dialogloader.getController();

        dialogController.getArtistName().setText(artistName);
        dialogController.getArtistAlbumName().setText(album.getName());


        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get().equals(ButtonType.OK)){
            dialogController.updateRecord(artist,album);
            artistsTable.refresh();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText("Error with updating record");
            alert.showAndWait();
        }
}




        @FXML
        public void addNewRecord() {

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainBorderPane.getScene().getWindow());
            dialog.setTitle("Insert new artist");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("UpdateDialogPane.fxml"));


            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

            try {
                dialog.getDialogPane().setContent(fxmlLoader.load());
            } catch (IOException e) {
                System.out.println("Error with dialog pane " + e.getMessage());
            }

            DialogController dialogController = fxmlLoader.getController();
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Artist artist = dialogController.addNewRecorddc();
                System.out.println("Record added " + dialogController.getArtistName().getText() + ", " + dialogController.getArtistAlbumName().getText());


                System.out.println(taskService.getState());

                if(taskService.getState() == Service.State.SUCCEEDED){
                    artistsTable.itemsProperty().unbind();
                    taskService.restart();
                    artistsTable.getItems().clear();
                    artistsTable.itemsProperty().bind(taskService.valueProperty());
                } else if (taskService.getState() == Service.State.READY){
                    artistsTable.itemsProperty().unbind();
                    artistsTable.getItems().clear();
                    taskService.start();
                }
//                artistsTable.getItems().clear();
//
//                if(taskService.getState() == Service.State.SUCCEEDED){
//                    taskService.reset();
//                    taskService.start();
//                    artistsTable.setItems(taskService.valueProperty().get());
//                    artistsTable.refresh();
//                } else if (taskService.getState() == Service.State.READY){
//                    taskService.start();
//                    artistsTable.setItems(taskService.valueProperty().get());
//                    artistsTable.refresh();
//                }

//                getAllArtists.list.sort(new Comparator<Artist>() {
//                    @Override
//                    public int compare(Artist o1, Artist o2) {
//                        return o1.getName().compareTo(o2.getName());
//                    }
//                });
            }
        }


    @FXML
        public void testUpdatingTable(){

        Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();


        Task<Boolean> task = new Task<Boolean>(){
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().testUpdateArtistName(artist.getId(),"Animalsik");
            }
        };

        task.setOnSucceeded(event -> {
            if(task.valueProperty().get()){
                artist.setName("Animalsik");
                artistsTable.refresh();
            }
        });
       new Thread(task).start();
        }

    public void albumList() {

        Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();

        int artistID = artist.getId();

        Task<ObservableList<Album>> albumList = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(DataSource.getInstance().queryAlbumForArtistID(artistID));
            }
        };
        albumsTable.itemsProperty().bind(albumList.valueProperty());

        new Thread(albumList).start();
    }

    public void songList(){

       Album album = (Album) albumsTable.getSelectionModel().getSelectedItem();

        int albumID = album.getId();

       ObservableList<Song> songs = FXCollections.observableArrayList(DataSource.getInstance().querySongForAlbumID(albumID));

       Task<ObservableList<Song>> songList = new Task<ObservableList<Song>>() {
           @Override
           protected ObservableList<Song> call() throws Exception {
               return songs;
           }
       };


       songsTable.itemsProperty().bind(songList.valueProperty());

       new Thread(songList).start();

    }

    @FXML
    public void deleteAlbum() {

        Album album = (Album) albumsTable.getSelectionModel().getSelectedItem();
        DataSource.getInstance().deleteAlbumRecord(album.getName());
        System.out.println("Album deleted : " + album.getName());
        albumsTable.getItems().remove(album);

    }

    @FXML
    public void editAlbum(){

        Album album = (Album) albumsTable.getSelectionModel().getSelectedItem();
        String newName = editDialog();

        DataSource.getInstance().editAlbumRecord(album.getName(),newName);

        albumsTable.itemsProperty().unbind();
        albumsTable.getItems().clear();

        albumList();

        System.out.println("Album : " + album.getName() + " edited");

    }



    @FXML
    public void deleteSongMethod(){

        Song song = (Song) songsTable.getSelectionModel().getSelectedItem();
        DataSource.getInstance().deleteSongRecord(song.getTitle());
        System.out.println("Song deleted : " + song.getTitle());
        songsTable.getItems().remove(song);

    }

    @FXML
    public void editSongMethod(){

        Song song = (Song) songsTable.getSelectionModel().getSelectedItem();
        String newName = editDialog();


        DataSource.getInstance().editSongRecord(song.getTitle().toString(),newName);
        System.out.println("Song : " + song.getTitle() + " edited");

        songsTable.itemsProperty().unbind();
        songsTable.getItems().clear();
        songList();


    }



       @FXML
    public void deleteRecord(){
           Artist artist = (Artist) artistsTable.getSelectionModel().getSelectedItem();
           DataSource.getInstance().deleteArtistRecord(artist.getName());
           System.out.println("Record deleted: " + artist.getName());
           artistsTable.getItems().remove(artist);
       }



       @FXML
       public void openAbout() {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("About");
            alert.setContentText("Using a program you can browse and searching data in the database.");
            alert.showAndWait();
       }



    @FXML
     public void openGH(){

             try {
                 Desktop.getDesktop().browse(new URI("https://github.com/piwanicki"));
             } catch (IOException e) {
                 e.printStackTrace();
             } catch (URISyntaxException e) {
                 e.printStackTrace();
             }
         }


     private void openFile(File file){

         Desktop desktop = Desktop.getDesktop();

         try{
             desktop.open(file);
         } catch (IOException e){
             System.out.println("Problem with opening file : " + e.getMessage() );
             e.printStackTrace();
         }

     }

     public void openDatabase(){

         FileChooser fc = new FileChooser();
         fc.setTitle("Open database");
         File file = fc.showOpenDialog(mainBorderPane.getScene().getWindow());

         fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Database", "*.db"));
         fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All files","*.*"));

         if(file != null) {
             openFile(file);
         }

     }

     @FXML
     public void countRecords(){

         DataSource.getInstance().countRecords(DataSource.TABLE_ARTISTS);

     }

    public String editDialog(){

        Dialog<ButtonType> editDialog = new Dialog<>();
        editDialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("EditDialog.fxml"));
        editDialog.setTitle("Edit record");
        String newName = "";


        try{
            editDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("something went wrong with loading EditDialog! " + e.getMessage());
            e.printStackTrace();
        }
        editDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        editDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = editDialog.showAndWait();
        EditController controller = fxmlLoader.getController();
        if(result.isPresent() && result.get() == ButtonType.OK){
            newName = controller.getEditLabel().getText();
        }
        return newName;
    }


     }



