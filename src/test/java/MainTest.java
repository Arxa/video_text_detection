
import entities.ApplicationPaths;
import entities.Controllers;
import org.testfx.util.WaitForAsyncUtils;
import processors.FileProcessor;
import controllers.LogController;
import controllers.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.loadui.testfx.GuiTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.loadui.testfx.Assertions.verifyThat;

/**
 * @author Nikiforos Archakis
 *         Date: 2/5/2017
 *         email: nikiarch@teiser.gr
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainTest extends GuiTest
{
    private MainController mainController;
    private LogController logController;

    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            ApplicationPaths.setApplicationPaths();
            FXMLLoader loader = new FXMLLoader(Paths.get
                    (ApplicationPaths.RESOURCES_VIEWS,"main.fxml")
                    .toFile().toURI().toURL());
            MainController.setMainStage(stage);
            MainController.getMainStage().setWidth(680);
            MainController.getMainStage().setHeight(450);
            parent = loader.load();
            Controllers.setMainController(loader.getController());
            return parent;
        } catch (IOException ex) {
            System.out.println("Failed to Initialize TestFX");
        }
        return parent;
    }

    @BeforeClass
    public static void beforeTests() {
    }

    @AfterClass
    public static void afterTests() {
    }

    @Before
    public void beforeTest() throws Exception {
        FileProcessor.loadLibraries();
        mainController = Controllers.getMainController();
        logController = Controllers.getLogController();
    }

    @After
    public void afterTest() {
    }

    @Test
    public void a_testInitialState() {
        verifyThat(mainController.progressIndicator, (Node b) -> !b.isVisible());
        verifyThat(mainController.progressBar, (Node b) -> !b.isVisible());
        verifyThat(mainController.videoPane, (Node b) -> ((Pane)b).getChildren().size() == 1);
        verifyThat(mainController.processButton, (Node b) -> !b.isVisible());
        verifyThat(mainController.textArea, (Node b) -> !b.isVisible());
        verifyThat(mainController.videoIcon, (Node b) -> b.isVisible());
    }


    @Test
    public void b_testKorea()
    {
        Platform.runLater(()->{
            File videoFile = Paths.get(ApplicationPaths.TEST_RESOURCES,"korea.mp4").toFile();
            Controllers.getMainController().loadThisFile(videoFile);
        });
        sleep(500);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(mainController.processButton, (Node s) -> s.isVisible());
        System.out.println(logController.logTextArea.getText());
        click(mainController.processButton);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(mainController.processButton, (Node s) -> !s.isVisible());
        waitUntil(mainController.processButton, (Node s) -> s.isVisible(),100);
    }

    @Test
    public void c_testMegaman()
    {
        Platform.runLater(()->{
            File videoFile = Paths.get(ApplicationPaths.TEST_RESOURCES,"stable_and_moving.mp4").toFile();
            Controllers.getMainController().loadThisFile(videoFile);
        });
        sleep(500);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(mainController.processButton, (Node s) -> s.isVisible());
        System.out.println(logController.logTextArea.getText());
        click(mainController.processButton);
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(mainController.processButton, (Node s) -> !s.isVisible());
        waitUntil(mainController.processButton, (Node s) -> s.isVisible(),100);

    }

}
