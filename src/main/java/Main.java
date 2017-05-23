import Controllers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;


import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class Main extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        loadLibrary(); // Load Opencv for Jar
        SystemController.initSystem();
        //Parent root = FXMLLoader.load(getClass().getResource("Views/RootLayout.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Views/RootLayout.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("VideoText Extractor");
        primaryStage.setScene(new Scene(root, 1500, 750));
        primaryStage.show();

        ViewController.initViewController(loader);

        // Terminating all Threads when the application is closed.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent e)
            {
                SystemController.closeSystem();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void loadLibrary() {

        try {
            InputStream in = null;
            File fileOut = null;
            String osName = System.getProperty("os.name");
            System.out.println(osName);
            if(osName.startsWith("Windows"))
            {
                int bitness = Integer.parseInt(System.getProperty("sun.arch.data.model"));
                if(bitness == 32){
                    System.out.println("32 bit detected");
                    in = Main.class.getResourceAsStream("opencv/opencv_java320_x32.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                }
                else if (bitness == 64){
                    System.out.println("64 bit detected");
                    in = ClassLoader.getSystemResourceAsStream("opencv/opencv_java320_x64.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                }
                else{
                    System.out.println("Unknown bit detected - trying with 32 bit");
                    in = Main.class.getResourceAsStream("opencv/opencv_java320_x32.dll");
                    fileOut = File.createTempFile("lib", ".dll");
                }
            }
            else if(osName.equals("Mac OS X")){
                in = Main.class.getResourceAsStream("opencv/opencv_java320_x32.dll");
                fileOut = File.createTempFile("lib", ".dylib");
            }


            OutputStream out = FileUtils.openOutputStream(fileOut);
            IOUtils.copy(in,out);
            in.close();
            out.close();
            System.load(fileOut.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load opencv native library", e);
        }
    }
}
