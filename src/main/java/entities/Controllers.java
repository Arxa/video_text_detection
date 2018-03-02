package entities;

import controllers.LogController;
import controllers.MainController;
import controllers.SettingsController;

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

    public static void setMainController(MainController mainController)
    {
        Controllers.mainController = mainController;
    }

    public static void setSettingsController(SettingsController settingsController) {
        Controllers.settingsController = settingsController;
    }

    public static void setLogController(LogController logController) {
        Controllers.logController = logController;
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
