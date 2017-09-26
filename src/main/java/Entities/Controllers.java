package Entities;

import ViewControllers.MainController;
import ViewControllers.PreferencesController;
import javafx.fxml.FXMLLoader;


/**
 * @author Nikiforos Archakis
 *         Date: 11/5/2017
 *         email: nikiarch@teiser.gr
 */

public class Controllers
{
    private static MainController mainController;
    private static PreferencesController preferencesController;

    public static void setMainController(FXMLLoader loader)
    {
        mainController = loader.getController();
    }

    public static void setPreferencesController(FXMLLoader loader) {
        preferencesController = loader.getController();
    }


    public static MainController getMainController() {
        return mainController;
    }

    public static PreferencesController getPreferencesController() {
        return preferencesController;
    }
}
