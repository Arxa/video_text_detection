package entities;

import controllers.LogController;
import controllers.MainController;
import controllers.SettingsController;
import javafx.fxml.FXMLLoader;

/**
 * @author Nikiforos Archakis
 *         Date: 11/5/2017
 *         email: nikiarch@teiser.gr
 */

/**
 * Stores access references to all the View Controllers
 */
public class Controllers
{
    private static MainController mainController;
    private static SettingsController settingsController;
    private static LogController logController;

    public static void setMainController(FXMLLoader loader)
    {
        mainController = loader.getController();
    }

    public static void setSettingsController(FXMLLoader loader) {
        settingsController = loader.getController();
    }

    public static void setLogController(FXMLLoader loader) {
        logController = loader.getController();
    }

    public static MainController getMainController() {
        return mainController;
    }

    public static SettingsController getSettingsController() {
        return settingsController;
    }

    public static LogController getLogController() {
        return logController;
    }
}
