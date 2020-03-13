package todolist.com;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import todolist.com.data.ItemData;
import todolist.com.data.TodoData;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class Controller {

    @FXML
    private ListView<ItemData> listView;

    @FXML
    private TextArea descriptionField;

    @FXML
    private Label deadlineDate;

    @FXML
    private BorderPane mainWindowBorderPane;

    @FXML
    private ContextMenu contextMenu;

    @FXML ContextMenu fullContextMenu;

    @FXML
    private ToggleButton onlyTodayToggleButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private FilteredList<ItemData> filteredList;
    private Predicate<ItemData> wantOnlyTodayTasks;
    private Predicate<ItemData> wantAllTasks;

    public void initialize(){


        SortedList<ItemData> sortedList = new SortedList<>(TodoData.getInstance().getListTodo(), new Comparator<ItemData>() {
            @Override
            public int compare(ItemData t0, ItemData t1) {
                return t0.getDeadLine().compareTo(t1.getDeadLine());
            }
        });

        wantOnlyTodayTasks = new Predicate<ItemData>() {
            @Override
            public boolean test(ItemData itemData) {
                  return itemData.getDeadLine().toLocalDate().equals(LocalDateTime.now().toLocalDate());
            }
        };
        wantAllTasks = new Predicate<ItemData>() {
            @Override
            public boolean test(ItemData itemData) {
                return true;
            }
        };

        filteredList = new FilteredList<>(sortedList, wantAllTasks);

        listView.setItems(filteredList);
        contextMenu = new ContextMenu();
        fullContextMenu = new ContextMenu();

        MenuItem deleteItemMenu = new MenuItem("Delete");
        deleteItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteItem(listView.getSelectionModel().getSelectedItem());
            }
        });

        MenuItem addItemMenu = new MenuItem("Add item");
        addItemMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                openNewItemDialog();
            }
        });

        contextMenu.getItems().addAll(addItemMenu);
        fullContextMenu.getItems().addAll(deleteItemMenu);
        fullContextMenu.getItems().addAll(addItemMenu);

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ItemData>() {
            @Override
            public void changed(ObservableValue<? extends ItemData> observableValue, ItemData oldValue, ItemData newValue) {
                if (newValue != null){
                    ItemData selectedItem = listView.getSelectionModel().getSelectedItem();
                    descriptionField.setText(selectedItem.getDescription());
                    deadlineDate.setText("Due: "+ selectedItem.getDeadLine().format(DateTimeFormatter.ofPattern("d MMMM YYYY, HH : m")));
                    editButton.setDisable(false);
                    deleteButton.setDisable(false);
                }
                else{
                    descriptionField.setText("");
                    deadlineDate.setText("");
                    deleteButton.setDisable(true);
                    editButton.setDisable(true);
                }
            }
        });
        listView.getSelectionModel().selectFirst();

        listView.setCellFactory(new Callback<ListView<ItemData>, ListCell<ItemData>>() {

            @Override
            public ListCell<ItemData> call(ListView<ItemData> itemDataListView) {
                ListCell<ItemData> cell = new ListCell<>(){

                    @Override
                    protected void updateItem(ItemData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (isEmpty()){
                            setText(null);
                        } else{
                            setText(item.getTitle());
                            if (item.getDeadLine().isBefore(LocalDateTime.now())) {
                                setTextFill(Color.RED);
                            } else {
                                setTextFill(Color.BLACK);
                            }

                        }
                    }
                };

                cell.emptyProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasEmpty, Boolean isNowEmpty) {
                        if (isNowEmpty){
                            cell.setContextMenu(contextMenu);


                        }else {
                            cell.setContextMenu(fullContextMenu);

                        }

                    }
                });

                cell.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                        if (observableValue == null){

                        }else{

                        }
                    }
                });

               return cell;
            }
        });

    }

    public void deleteItem(ItemData item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete menu");
        alert.setHeaderText("Do you want to delete " + item.getTitle());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            TodoData.getInstance().deleteItem(item);
        }

    }

    @FXML
    public void handleClickTodoList(){
        ItemData selectedItem = listView.getSelectionModel().getSelectedItem();
       // descriptionField.setText(selectedItem.getDescription() + "\n\n\n" + "Due to: "+selectedItem.getDeadLine());
        descriptionField.setText(selectedItem.getDescription());
        deadlineDate.setText("Due: "+ selectedItem.getDeadLine().format(DateTimeFormatter.ofPattern("MM dd YYYY")));
    }

    @FXML
    public void handleTodayToggleButtonPressed(){
        ItemData selectedItem = listView.getSelectionModel().getSelectedItem();
        if (onlyTodayToggleButton.isSelected()){
            filteredList.setPredicate(wantOnlyTodayTasks);
            if (filteredList.contains(selectedItem)){
                listView.getSelectionModel().select(selectedItem);
            }else{
                descriptionField.setText("");
                deadlineDate.setText("");
            }
        }else{
            filteredList.setPredicate(wantAllTasks);
            listView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void openNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainWindowBorderPane.getScene().getWindow()); //what for?
        dialog.setTitle("Add new item to do");
        dialog.setHeaderText("Use this dialog to create new item");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dialogwindow.fxml"));
        try {
            Parent root = loader.load();
            dialog.getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result= dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            System.out.println("Ok button was pressed");
            DialogWindowController dialogController = loader.getController();
            ItemData newItem = dialogController.proceedResult();
          // listView.getItems().setAll(TodoData.getInstance().getListTodo()); //no longer need in case of setting observable list
            listView.getSelectionModel().select(newItem);
        }else {
            System.out.println("Cancel was pressed");
        }
    }

    @FXML
    public void onDeleteKeyReleased(KeyEvent keyEvent) {
        ItemData item = listView.getSelectionModel().getSelectedItem();
        if (item != null && keyEvent.getCode() == KeyCode.DELETE){
            deleteItem(item);
        }
    }

    @FXML
    public void onDeleteButton() {
        ItemData item = listView.getSelectionModel().getSelectedItem();
        if (item != null){
            deleteItem(item);
        }
    }


    @FXML
    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    public void openEditItemDialog(ActionEvent actionEvent) {
        ItemData selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null){
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mainWindowBorderPane.getScene().getWindow());
            dialog.setTitle("Edit task");
            dialog.setHeaderText("Edit task here");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("dialogwindow.fxml"));
            try {
                Parent root = loader.load();
                dialog.getDialogPane().setContent(root);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            DialogWindowController dialogWindowController = loader.getController();
            dialogWindowController.initializeItem(selectedItem, dialog);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get().equals(ButtonType.OK)){
                dialogWindowController.proceedResult();
                listView.refresh();
            }
        }


    }

    @FXML
    public void exportTodoList(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("txt", "*.txt")
        );
        File file = fileChooser.showSaveDialog(mainWindowBorderPane.getScene().getWindow());
        try {
            TodoData.getInstance().storeData(file.getPath());
            System.out.println("Export was completed to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
    }

    @FXML
    public void importTodoList(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("txt", "*.txt")
        );

        File file = fileChooser.showOpenDialog(mainWindowBorderPane.getScene().getWindow());
        if (file!=null){
            Dialog<ButtonType> dialog= new Dialog<>();
            dialog.setTitle("Import dialog");
            dialog.setHeaderText("Do you want to replace all tasks?");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try {
                    TodoData.getInstance().loadData(file.getPath());
                    System.out.println("Import complete");
                    listView.refresh();
                } catch (IOException e) {
                    System.out.println("Import failed");
                    e.printStackTrace();
                }
            } else{
                System.out.println("Import was canceled");
            }
        }
    }


}
