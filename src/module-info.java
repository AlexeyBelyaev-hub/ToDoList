module TodoList {
    requires javafx.fxml;
    requires javafx.controls;
    requires datetime.picker;
    requires javafx.graphics;
    exports todolist.com;
    opens todolist.com;

}