package ControllersTests;

import Controllers.MainController;
import Controllers.Player;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import org.junit.*;
import org.loadui.testfx.GuiTest;
import sun.applet.Main;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.loadui.testfx.Assertions.assertNodeExists;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

/**
 * @author Nikiforos Archakis
 *         Date: 2/5/2017
 *         email: nikiarch@teiser.gr
 */



public class PlayerTests extends GuiTest
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


    private static final String simpleMovingTextVideo = "src\\test\\resources\\moving_text.mp4";
    private static final String simpleStableTextVideo = "src\\test\\resources\\stable_text.mp4";
    private static final String noTextVideo = "src\\test\\resources\\no_text.mp4";
    private static final String simpleStableAndMovingVideo = "src\\test\\resources\\stable_and_moving_text.mp4";

    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getClassLoader().getResource("Views/RootLayout.fxml"));
            return parent;
        } catch (IOException ex) {
            System.out.println("Failed to Initialize TestFX");
        }
        return parent;
    }

    @BeforeClass
    public static void initToolkit() {

    }

    @AfterClass
    public static void afterTests() {

    }

    @Before
    public void beforeTest()
    {
        chooseVideoFile_Button = find("#chooseVideoFile_Button");
        playInputVideo_Pane = find("#playInputVideo_Pane");
        processVideo_Button = find("#processVideo_Button");
        inputVideoLabel = find("#inputVideoLabel");
        videoName = find("#videoName");
        extractingTextLabel = find("#extractingTextLabel");
        progressBar1 = find("#progressBar1");
        log_Area = find("#logArea");
        progressIndicator = find("#progressIndicator");
//        testFXLabel = find("#testFX");
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

        //click("#chooseVideoFile_Button");
        //assertNodeExists( ".dialog" );
        //verifyThat("#chooseVideoFile_Button", Node::isDisabled);

/*
        TextField firstname = find("#firstname");
        firstname.setText("bennet");
        verifyThat("#firstname", hasText("bennet"));

        TextField lastname = find("#lastname");
        lastname.setText("schulz");
        verifyThat("#lastname", hasText("schulz"));

        Button search = find("#search");
        assertFalse(search.disableProperty().get());
        */
    }

    @Test
    public void testProcessButtonClick()
    {
        //testFXLabel.setAccessibleText(simpleMovingTextVideo);
        //click(testFXLabel);
        //MainController.validateVideoFile(MainController.getFilename()), GlobalTest.getViewTest());
        //Assert.assertTrue(Player.validateFileName());
    }

    // TODO CHANGE THE THIS PARAMETER AND MAKE ONLY ONE METHOD TO STORE THE REFERENCE


    /*
    Just opening and closing the choose file dialog.
    It should behave as the input filename is null.
     */

    /*
    @Test
    public void loadAndPlayVideo_Test_noFileChosen()
    {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Player.validateFileName(new File(simpleMovingTextVideo));
                Assert.assertTrue("Process Button should be disabled",processButton.isDisable());
                Assert.assertTrue("No log message should be shown",logArea.getText().equals(""));
                Assert.assertTrue("Filename should be null",Player.getFilename() == null);
            }
        });

    }

    */


}
