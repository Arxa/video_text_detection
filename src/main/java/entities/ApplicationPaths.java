package entities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ApplicationPaths
{
    public static String APP_DIR;
    public static String TEST_RESOURCES;
    public static String RESOURCES_NATIVES;
    public static String RESOURCES_OCR;
    public static String RESOURCES_OCR_TESSDATA;
    public static String RESOURCES_OCR_LANGUAGES;
    public static String RESOURCES_OUTPUTS;
    public static String RESOURCES_VIEWS;
    public static String RESOURCES_MODELS;
    public static String RESOURCES_ICONS;
    public static String RESOURCES_CSS;
    public static String UNIQUE_OUTPUT_FOLDER_NAME = "";

    /***
     * Sets the application's directory paths differently whether it is a JAR file calling or an IDE
     */
    public static void setApplicationPaths() throws IOException {

        final File jarFile = new File(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {
            String jarPath = jarFile.getParentFile().getPath();
            APP_DIR = Paths.get(jarPath,"resources").toString();
        } else {
            APP_DIR = Paths.get(new File(".").getCanonicalPath(),"src","main","resources").toFile().getPath();
            TEST_RESOURCES = Paths.get(new File(".").getCanonicalPath(),"src","test","resources").toFile().getPath();
        }

        RESOURCES_NATIVES = Paths.get(APP_DIR, "natives").toAbsolutePath().toString();
        RESOURCES_OCR = Paths.get(APP_DIR, "ocr").toAbsolutePath().toString();
        RESOURCES_OCR_TESSDATA = Paths.get(APP_DIR, "ocr","tessdata").toAbsolutePath().toString();
        RESOURCES_OCR_LANGUAGES = Paths.get(APP_DIR, "ocr","languages").toAbsolutePath().toString();
        RESOURCES_OUTPUTS = Paths.get(APP_DIR, "outputs").toAbsolutePath().toString();
        RESOURCES_VIEWS = Paths.get(APP_DIR, "views").toAbsolutePath().toString();
        RESOURCES_MODELS = Paths.get(APP_DIR, "models").toAbsolutePath().toString();
        RESOURCES_ICONS = Paths.get(APP_DIR, "icons").toAbsolutePath().toString();
        RESOURCES_CSS = Paths.get(APP_DIR, "css").toAbsolutePath().toString();
    }
}
