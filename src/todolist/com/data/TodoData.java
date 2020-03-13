package todolist.com.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static TodoData instance = new TodoData();
    private ObservableList<ItemData> listTodo;
    private static String dbFilePath = "todoFile.txt";
    private DateTimeFormatter formatter;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm");
    }

    public void loadData(String pathFile) throws IOException{
        if (listTodo == null){
            listTodo = FXCollections.observableArrayList();
        } else {
            listTodo.removeAll();
        }

        Path path = Paths.get(pathFile);
        BufferedReader br = Files.newBufferedReader(path);

        String input;
        try {
            while ( (input = br.readLine())!=null){
                String[] lines = input.split("\t");
                String title = lines[0];
                String description = lines[1];
                LocalDateTime localDateTime = LocalDateTime.parse(lines[2], formatter);
                ItemData newItem = new ItemData(title, description, localDateTime);
                listTodo.add(newItem);
            }
        }finally {
            if (br != null){
                br.close();
            }
        }
    }

    public void loadData() throws IOException{
        loadData(dbFilePath);
    }

    public void storeData(String pathFile) throws IOException{
        Path path = Paths.get(pathFile);
        BufferedWriter bw = Files.newBufferedWriter(path);
        Iterator<ItemData> itr =  listTodo.iterator();
        try {
            while (itr.hasNext()){
                ItemData item = itr.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getTitle(),
                        item.getDescription(),
                        item.getDeadLine().format(formatter)));
                bw.newLine();
            }
        } finally {
            if (bw!=null){
                bw.close();
            }
        }
    }

    public void storeData() throws IOException{
        storeData(dbFilePath);
    }



    public ObservableList<ItemData> getListTodo() {
        return listTodo;
    }

    public void deleteItem(ItemData item){
        listTodo.remove(item);
    }

}
