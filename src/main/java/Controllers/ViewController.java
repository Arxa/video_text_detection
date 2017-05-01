package Controllers;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * Created by arxa on 26/2/2017.
 */

public class ViewController
{
    public static void updateVideoNameLabel(String name, Label label)
    {
        label.setText(name);
    }

}
