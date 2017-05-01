package Controllers;


import javafx.scene.control.TextArea;

/**
 * @author Nikiforos Archakis
 *         Date: 1/5/2017
 *         email: nikiarch@teiser.gr
 */

public class Log
{
    public static void printLogMessageToGUI(TextArea textArea, String message)
    {
        textArea.setText(textArea.getText() + "\n" + message);
    }
}
