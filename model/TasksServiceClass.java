package sample.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class TasksServiceClass extends Service<ObservableList<Artist>> {

    @Override
    protected Task<ObservableList<Artist>> createTask() {
        return new Task<ObservableList<Artist>>() {
            @Override
            protected ObservableList<Artist> call() throws Exception {

                int  records = DataSource.getInstance().countRecords(DataSource.TABLE_ARTISTS);

                for (int i=0; i<= records; i++){
                    updateProgress(i+1,records);
                    Thread.sleep(5);
            }

            return FXCollections.observableArrayList(DataSource.getInstance().queryArtists(DataSource.ORDER_BY_ASC));
        }

    };
}

}
