package ControllersTests;

import Controllers.Player;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

/**
 * @author Nikiforos Archakis
 *         Date: 2/5/2017
 *         email: nikiarch@teiser.gr
 */



public class PlayerTests extends GuiTest
{
    private static final String simpleMovingTextVideo = "src\\test\\resources\\moving_text.mp4";
    private static final String simpleStableTextVideo = "src\\test\\resources\\stable_text.mp4";
    private static final String noTextVideo = "src\\test\\resources\\no_text.mp4";
    private static final String simpleStableAndMovingVideo = "src\\test\\resources\\stable_and_moving_text.mp4";

    @Override
    protected Parent getRootNode() {
        Parent parent = null;
        try {
            parent = FXMLLoader.load(getClass().getResource("Views/RootLayout.fxml"));
            return parent;
        } catch (IOException ex) {
            System.out.println("Failed to Initialize TestFX");
        }
        return parent;
    }

    @Test
    public void setBothnamesAndCheckEnabledSearchButton() {
        TextField firstname = find("#firstname");
        firstname.setText("bennet");
        verifyThat("#firstname", hasText("bennet"));

        TextField lastname = find("#lastname");
        lastname.setText("schulz");
        verifyThat("#lastname", hasText("schulz"));

        Button search = find("#search");
        assertFalse(search.disableProperty().get());
    }

    @BeforeClass
    public static void initToolkit()
            throws InterruptedException
    {
//        final CountDownLatch latch = new CountDownLatch(1);
//        SwingUtilities.invokeLater(() -> {
//            new JFXPanel(); // initializes JavaFX environment
//            latch.countDown();
//        });
//
//        // That's a pretty reasonable delay... Right?
//        if (!latch.await(5L, TimeUnit.SECONDS))
//            throw new ExceptionInInitializerError();
    }


    @AfterClass
    public static void afterTests() {

    }

    @Before
    public void beforeTest()
    {

    }

    @After
    public void afterTest() {

    }

    /*
    Just opening and closing the choose file dialog.
    It should behave as the input filename is null.
     */
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


}
