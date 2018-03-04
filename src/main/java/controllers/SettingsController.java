package controllers;

import entities.ApplicationPaths;
import entities.Controllers;
import javafx.stage.Stage;
import processors.ImageWriter;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import org.apache.commons.io.FileUtils;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class SettingsController {

    public ComboBox ocrLanguage_combobox;
    public CheckBox extractUniqueWords_checkbox;
    public CheckBox includeSpecialCharacters_checkbox;
    public CheckBox exportImages_checkbox;
    public Label moreLanguagesLabel;
    public CheckBox extractTextCheckBox;

    // Stores language names along with their 3-character ISO 639-2 language codes
    private static Map<String,String> languageMap;
    private static Stage settingsStage;

    public void initialize() throws IOException
    {
        Controllers.setSettingsController(this);

        moreLanguagesLabel.setCursor(Cursor.HAND);

        moreLanguagesLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            // Showing an alert with a clickable hyperlink inside
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Download the .traineddata language files(s) you wish to add from the following link and place the file(s) project's directory: src\\main\\resources\\ocr\\tessdata");
            alert.setTitle("How to add more languages");
            FlowPane fp = new FlowPane();
            Label lbl = new Label("Download the .traineddata language files(s) you wish to add from the following link \n" +
                    "and place the file(s) in the project's directory: ocr\\tessdata \n" +
                    "Restart the application and you should see the new language options in Tools->Settings");
            Hyperlink link = new Hyperlink("https://github.com/tesseract-ocr/tessdata/tree/3.04.00");
            link.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/tesseract-ocr/tessdata/tree/3.04.00"));
                } catch (IOException | URISyntaxException e) {
                    MainController.showError("Failed to open website link!\n");
                }
            });
            fp.getChildren().addAll( lbl, link);
            alert.getDialogPane().contentProperty().set(fp);
            alert.showAndWait();
        });

        Path lang_codes = Paths.get(ApplicationPaths.RESOURCES_OCR_LANGUAGES, "lang_codes.txt");

        // Read each line of lang_codes file, parse it, and store the languageName - languageCode information to the map
        languageMap = new HashMap<>();
        List<String> lines = Files.readAllLines(lang_codes);
        for(String line : lines){
            line = line.replaceAll("\\(","").replaceAll("\\)","");
            String[] tokens = line.split(" ");
            languageMap.put(tokens[1],tokens[0]);
        }

        // Check which traineddata files exist in the tessdata directory and make that language(s) available to the ocr languages combobox.
        Collection files = FileUtils.listFiles(new File(ApplicationPaths.RESOURCES_OCR_TESSDATA), null, false);
        for(Object f : files){
            if (languageMap.values().contains(((File)f).getName().split("\\.")[0])){
                ocrLanguage_combobox.getItems().add(getKeyByValue(languageMap,((File)f).getName().split("\\.")[0]));
            }
        }

        // Set default ocr language selection to English
        ocrLanguage_combobox.getSelectionModel().select("English");

        // Saves the characters that are being typed in the checkbox's list and jumps you to the matching language
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

        // Refresh StringBuilder
        ocrLanguage_combobox.addEventFilter(javafx.scene.input.KeyEvent.KEY_RELEASED, new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE && sb.length() > 0) {
                    sb.delete(0, sb.length());
                }
            }
        });

        // Add a focus listener such that if not in focus, reset the filtered typed keys
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

//        includeSpecialCharacters_checkbox.selectedProperty().addListener(event -> {
//            OcrProcessor.setIncludeSpecialCharacters(includeSpecialCharacters_checkbox.isSelected());
//        });
//
//        extractUniqueWords_checkbox.selectedProperty().addListener(event -> {
//            OcrProcessor.setExtractUniqueWords(extractUniqueWords_checkbox.isSelected());
//        });
    }

    /**
     *  Get a Key from a Map providing a Value
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<String, String> getLanguageMap() {
        return languageMap;
    }


    public static void setSettingsStage(Stage settingsStage) {
        SettingsController.settingsStage = settingsStage;
    }

    public static void showSettingsStage(){
        settingsStage.show();
    }
}
