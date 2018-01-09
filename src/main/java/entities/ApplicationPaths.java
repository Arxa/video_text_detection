package entities;

import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ApplicationPaths
{
    public static Caller CALLER;
    public static String APP_DIR;
    public static String TEST_RESOURCES;
    public static String RESOURCES_NATIVES;
    public static String RESOURCES_OCR;
    public static String RESOURCES_OCR_TESSDATA;
    public static String RESOURCES_OCR_LANGUAGES;
    public static String RESOURCES_OUTPUTS;
    public static String UNIQUE_FOLDER_NAME;

    /***
     * Sets the application's directory paths differently whether it is a JAR file calling or an IDE
     */
    public static void setApplicationPaths()
    {
        final File jarFile = new File(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        if(jarFile.isFile()) {
            CALLER = Caller.JAR;
            APP_DIR = jarFile.getParentFile().getPath();
        } else {
            CALLER = Caller.IDE;
            try {
                APP_DIR = Paths.get(new File(".").getCanonicalPath(),"src","main","resources").toFile().getPath();
                TEST_RESOURCES = Paths.get(new File(".").getCanonicalPath(),"src","test","resources").toFile().getPath();
            } catch (IOException e) {
                System.out.println("Couldn't create IDE directories");
                Platform.exit();
                System.exit(1);
            }
        }
        RESOURCES_NATIVES = Paths.get(APP_DIR, "Natives").toAbsolutePath().toString();
        RESOURCES_OCR = Paths.get(APP_DIR, "OCR").toAbsolutePath().toString();
        RESOURCES_OCR_TESSDATA = Paths.get(APP_DIR, "OCR","tessdata").toAbsolutePath().toString();
        RESOURCES_OCR_LANGUAGES = Paths.get(APP_DIR, "OCR","languages").toAbsolutePath().toString();
        RESOURCES_OUTPUTS = Paths.get(APP_DIR, "Outputs").toAbsolutePath().toString();
    }
}
