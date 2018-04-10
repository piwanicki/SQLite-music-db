package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import sample.model.DataSource;

/**
 * Unused class in this project -> Song,Artist,ArtistsSongs.
 * That was needed in other project with query in console.
 * The same is with some methods in DataSource class.
 *
 */


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root  = loader.load();
        primaryStage.setTitle("Music Database explorer");
        Scene mainScene = new Scene(root,700,700);
        primaryStage.setScene(mainScene);
        primaryStage.setAlwaysOnTop(false);
        primaryStage.toBack();
        primaryStage.show();

        Controller controller = loader.getController();
        //controller.listArtists();


    }

    @Override
    public void init() throws Exception {
        super.init();

        DataSource.getInstance().open();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        DataSource.getInstance().close();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
