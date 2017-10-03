package Entities;

import java.io.File;
import java.io.IOException;

public class ApplicationPaths
{
    public static String RESOURCES;
    public static String RESOURCES_NATIVES;
    public static String RESOURCES_OCR;
    public static String RESOURCES_OCR_TESSDATA;
    public static String RESOURCES_OCR_LANGUAGES;
    public static String FOLDERPATH;

    /***
     * Sets the application's directory paths differently whether it is a JAR file calling or an IDE
     */
    public static void setApplicationPaths()
    {
        final File jarFile = new File(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {  // JAR
            String jarPath = jarFile.toString().replace("VideoText_Extractor-all.jar","");
            RESOURCES = jarPath;
            RESOURCES_NATIVES = jarPath + "Natives";
            RESOURCES_OCR = jarPath + "OCR";
            RESOURCES_OCR_TESSDATA = jarPath + "OCR\\tessdata";
            RESOURCES_OCR_LANGUAGES = jarPath + "OCR\\languages\\";
            FOLDERPATH = jarPath + "Outputs\\";
        } else { // IDE
            try {
                String idePath = new File(".").getCanonicalPath() + "\\src\\main\\resources\\";
                RESOURCES = idePath;
                RESOURCES_NATIVES = idePath + "Natives";
                RESOURCES_OCR = idePath + "OCR";
                RESOURCES_OCR_TESSDATA = idePath + "OCR\\tessdata";
                RESOURCES_OCR_LANGUAGES = "src/main/resources/OCR/languages/";
                FOLDERPATH = "src\\main\\resources\\Outputs\\";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
