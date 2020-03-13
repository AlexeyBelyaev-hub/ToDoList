package todolist.com;

import com.browniebytes.javafx.control.DateTimePicker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import todolist.com.data.ItemData;
import todolist.com.data.TodoData;

import java.time.LocalDateTime;

public class DialogWindowController {

    private ItemData item;

    @FXML
    private TextField shortDescription;

    @FXML
    private TextArea detailDescription;

    @FXML
    private DateTimePicker deadlinePicker;

    private boolean changed;

    public ItemData proceedResult(){

        String title = shortDescription.getText().trim();
        String description =  detailDescription.getText().trim();
        LocalDateTime deadline = deadlinePicker.dateTimeProperty().get();

        if (item==null) {
            ItemData newItem = new ItemData(title, description, deadline);
            TodoData.getInstance().getListTodo().add(newItem);
            return newItem;
        }else {
            item.setTitle(title);
            item.setDescription(description);
            item.setDeadLine(deadline);
            return item;
        }
    }

    public void initializeItem(ItemData itemToLoad, Dialog dialog){
        item = itemToLoad;
        shortDescription.setText(item.getTitle());
        detailDescription.setText(item.getDescription());
        deadlinePicker.dateTimeProperty().set(item.getDeadLine());

        shortDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setChangedSign(dialog);
            }
        });

        detailDescription.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                setChangedSign( dialog);
            }
        });

        deadlinePicker.dateTimeProperty().addListener(new ChangeListener<LocalDateTime>() {
            @Override
            public void changed(ObservableValue<? extends LocalDateTime> observableValue, LocalDateTime localDateTime, LocalDateTime t1) {
                setChangedSign(dialog);
            }
        });
    }

    public void setChangedSign(Dialog dialog){
        if (!changed){
            changed = true;
            dialog.setHeaderText(dialog.getHeaderText()+"*");
        }
    }


}
