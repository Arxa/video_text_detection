package Controllers;

import javafx.fxml.FXMLLoader;


/**
 * @author Nikiforos Archakis
 *         Date: 11/5/2017
 *         email: nikiarch@teiser.gr
 */

public class References
{
    private static MainController mainController;

    public static void setController(FXMLLoader loader)
    {
        mainController = loader.getController();
    }

    public static MainController getMainController() {
        return mainController;
    }

}
