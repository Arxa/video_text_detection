package ViewControllers;

import Processors.ImageWriter;
import Processors.OcrProcessor;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PreferencesController {

    public ComboBox ocrLanguage_combobox;
    public CheckBox extractUniqueWords_checkbox;
    public CheckBox includeSymbols_checkbox;
    public CheckBox exportImages_checkbox;

    private static Map<String,String> languageMap;

    public void initialize() throws IOException {

        Path file = Paths.get("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\OCR\\language_codes\\lang_codes");

        languageMap = new HashMap<>();
        List<String> lines = Files.readAllLines(file, Charset.defaultCharset());
        for(String line : lines){
            line = line.replaceAll("\\(","").replaceAll("\\)","");
            String[] tokens = line.split(" ");
            languageMap.put(tokens[1],tokens[0]);
            ocrLanguage_combobox.getItems().add(tokens[1]);
        }

//        Iterator it = FileUtils.iterateFiles(new File("C:\\Users\\310297685\\Desktop\\tessdata-3.04.00"), null, false);
//        while(it.hasNext()){
//            File temp = (File) it.next();
//            if (!languageMap.values().contains(temp.getName().split("\\.")[0])){
//                temp.delete();
//                System.out.println("deleted");
//            }
//        }


        ocrLanguage_combobox.getSelectionModel().select("English");

        StringBuilder sb = new StringBuilder();
        ocrLanguage_combobox.addEventHandler(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.UP || event.getCode() == KeyCode.TAB) {
                return;
            }
            else if (event.getCode() == KeyCode.BACK_SPACE && sb.length() > 0) {
                sb.deleteCharAt(sb.length()-1);
            }
            else {
                sb.append(event.getText());
            }

            if (sb.length() == 0)
                return;

            boolean found = false;
            ObservableList items = ocrLanguage_combobox.getItems();
            for (int i=0; i<items.size(); i++) {
                if (event.getCode() != KeyCode.BACK_SPACE && items.get(i).toString().toLowerCase().startsWith(sb.toString().toLowerCase())) {
                    ListView lv = ((ComboBoxListViewSkin) ocrLanguage_combobox.getSkin()).getListView();
                    lv.getSelectionModel().clearAndSelect(i);
                    lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
                    found = true;
                    break;
                }
            }

            if (!found && sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
        });



        ocrLanguage_combobox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ListView lv = ((ComboBoxListViewSkin) ocrLanguage_combobox.getSkin()).getListView();
                lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
            }
        });



        ocrLanguage_combobox.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE && sb.length() > 0) {
                    sb.delete(0, sb.length());
                }
            }
        });


        // add a focus listener such that if not in focus, reset the filtered typed keys
        ocrLanguage_combobox.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue)
                sb.delete(0, sb.length());
            else {
                ListView lv = ((ComboBoxListViewSkin) ocrLanguage_combobox.getSkin()).getListView();
                lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
            }
        });



        exportImages_checkbox.selectedProperty().addListener(event -> {
            ImageWriter.setWritingEnabled(exportImages_checkbox.isSelected());
        });

        includeSymbols_checkbox.selectedProperty().addListener(event -> {
            OcrProcessor.setIncludeSymbols(includeSymbols_checkbox.isSelected());
        });

        extractUniqueWords_checkbox.selectedProperty().addListener(event -> {
            OcrProcessor.setExtractUniqueWords(extractUniqueWords_checkbox.isSelected());
        });
    }

    public static Map<String, String> getLanguageMap() {
        return languageMap;
    }
}
