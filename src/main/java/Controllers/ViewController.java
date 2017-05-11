package Controllers;

import javafx.fxml.FXMLLoader;


/**
 * @author Nikiforos Archakis
 *         Date: 11/5/2017
 *         email: nikiarch@teiser.gr
 */

public class ViewController
{
    private static MainController viewController;

    public static void initViewController(FXMLLoader main_loader)
    {
        viewController = main_loader.getController();
    }

    public static MainController getViewController() {
        return viewController;
    }
}
