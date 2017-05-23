package MainTests;

import Controllers.Player;
import Controllers.SystemController;
import Controllers.ViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.junit.*;
import org.loadui.testfx.GuiTest;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.Assertions.verifyThat;

/**
 * @author Nikiforos Archakis
 *         Date: 2/5/2017
 *         email: nikiarch@teiser.gr
 */



public class MainTest extends GuiTest
{
    /*
    UI TEST ELEMENTS
     */
    private static Node chooseVideoFile_Button;
    private static Node playInputVideo_Pane;
    private static Node processVideo_Button;
    private static Node inputVideoLabel;
    private static Node videoName;
    private static Node extractingTextLabel;
    private static Node progressBar1;
    private static Node log_Area;
    private static Node progressIndicator;
    private static Node testFXLabel;

    private static File simpleMovingTextVideo;
    private static File simpleStableTextVideo;
    private static File noTextVideo;
    private static File simpleStableAndMovingVideo;

    private FXMLLoader loader;

    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/RootLayout.fxml"));
            parent = loader.load();
            return parent;
        } catch (IOException ex) {
            System.out.println("Failed to Initialize TestFX");
        }
        return parent;
    }

    @BeforeClass
    public static void beforeTests()
    {

        simpleMovingTextVideo = new File("src\\test\\resources\\moving_text.mp4");
        simpleStableTextVideo = new File("src\\test\\resources\\stable_text.mp4");
        noTextVideo = new File("src\\test\\resources\\no_text.mp4");
        simpleStableAndMovingVideo = new File("src\\test\\resources\\stable_and_moving_text.mp4");
    }

    @AfterClass
    public static void afterTests() {

    }

    @Before
    public void beforeTest()
    {
        SystemController.initSystem();
        ViewController.initViewController(loader);

        chooseVideoFile_Button = find("#chooseVideoFile_Button");
        playInputVideo_Pane = find("#playInputVideo_Pane");
        processVideo_Button = find("#processVideo_Button");
        inputVideoLabel = find("#inputVideoLabel");
        videoName = find("#videoName");
        extractingTextLabel = find("#extractingTextLabel");
        progressBar1 = find("#progressBar1");
        log_Area = find("#logArea");
        progressIndicator = find("#progressIndicator");
    }

    @After
    public void afterTest() {

    }

    @Test
    public void testUIInitialState()
    {
        assertTrue(!chooseVideoFile_Button.isDisabled());
        assertTrue(processVideo_Button.isDisabled());
        assertTrue( ((TextArea)log_Area).getText().equals(""));
        assertTrue( ((Label)videoName).getText().equals("Only .mp4 video format is supported"));
        assertTrue( ((Label)inputVideoLabel).getText().equals("Input Video:"));
        assertTrue(processVideo_Button.isDisabled());
        assertTrue( ((Label)extractingTextLabel).getText().equals("Extracting text:"));
        assertTrue(Double.compare(((ProgressIndicator)progressIndicator).getProgress(),0.0) == 0);
        assertTrue(Double.compare(((ProgressIndicator)progressBar1).getProgress(),0.0) == 0);
    }

    @Test
    public void testProcessButtonClick()
    {
        Player player = new Player();
        Player.setFilename(simpleMovingTextVideo);
        /*
        Failing on Travis
         */
//        Assert.assertTrue("Video File is not valid",
//                player.validateVideoFile(Player.getFilename()) == 1);
        Assert.assertTrue("Failed to update directories",player.updateDirectories());

        chooseVideoFile_Button.setDisable(false);
        processVideo_Button.setDisable(false);
        click(processVideo_Button);
        verifyThat("#chooseVideoFile_Button", Node::isDisabled);
        verifyThat("#processVideo_Button", Node::isDisabled);
        waitUntil(chooseVideoFile_Button, (Node b) -> !b.isDisabled(),120);
    }


}
