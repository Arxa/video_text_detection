package controllers;

import entities.Controllers;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LogController {
    public TextArea logTextArea;
    private static Stage logStage;

    public void initialize(){
        Controllers.setLogController(this);
        VBox.setVgrow(logTextArea, Priority.ALWAYS);
        HBox.setHgrow(logTextArea,Priority.ALWAYS);
    }

    public static void showLogStage(){
        logStage.show();
    }

    public static void setLogStage(Stage logStage){
        LogController.logStage = logStage;
    }
}
