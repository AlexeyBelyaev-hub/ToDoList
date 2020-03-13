package todolist.com;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import todolist.com.data.TodoData;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setTitle("To do list");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }


    @Override
    public void stop() throws Exception {
        TodoData todoData= TodoData.getInstance();
        todoData.storeData();
    }

    @Override
    public void init() throws Exception {
        try {
            TodoData.getInstance().loadData();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
