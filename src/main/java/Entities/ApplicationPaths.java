package Entities;

import javafx.application.Platform;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

public class ApplicationPaths
{
    public static Caller CALLER;
    public static String USER_DIR = "";
    public static String IDE_DIR = "";
    public static String RESOURCES;
    public static String RESOURCES_NATIVES;
    public static String RESOURCES_OCR;
    public static String RESOURCES_OCR_TESSDATA;
    public static String RESOURCES_OCR_LANGUAGES;
    public static String FOLDER_PATH;
    public static String UNIQUE_FOLDER_NAME;
    public static String FILE_SEPERATOR;

    /***
     * Sets the application's directory paths differently whether it is a JAR file calling or an IDE
     */
    public static void setApplicationPaths()
    {
        final File jarFile = new File(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {
            if (FilenameUtils.getExtension(jarFile.getPath()).equals("jar")){
                CALLER = Caller.JAR;
                USER_DIR = jarFile.getParent();
            }

            RESOURCES = USER_DIR;
            RESOURCES_NATIVES = USER_DIR + FILE_SEPERATOR + "Natives";
            RESOURCES_OCR = USER_DIR + FILE_SEPERATOR + "OCR";
            RESOURCES_OCR_TESSDATA = USER_DIR + FILE_SEPERATOR + "OCR" + FILE_SEPERATOR + "tessdata";
            RESOURCES_OCR_LANGUAGES = USER_DIR + FILE_SEPERATOR + "OCR" + FILE_SEPERATOR + "languages" + FILE_SEPERATOR;
            FOLDER_PATH = USER_DIR + FILE_SEPERATOR + "Outputs";

        } else {
            CALLER = Caller.IDE;
            try {
                IDE_DIR = new File(".").getCanonicalPath() + FILE_SEPERATOR + "src" +
                        FILE_SEPERATOR + "main" + FILE_SEPERATOR + "resources" + FILE_SEPERATOR;
                RESOURCES = IDE_DIR;
                RESOURCES_NATIVES = IDE_DIR + "Natives";
                RESOURCES_OCR = IDE_DIR + "OCR";
                RESOURCES_OCR_TESSDATA = IDE_DIR + "OCR" + FILE_SEPERATOR + "tessdata";
                RESOURCES_OCR_LANGUAGES = "src/main/resources/OCR/languages/";
                FOLDER_PATH = "src" + FILE_SEPERATOR + "main" + FILE_SEPERATOR +
                        "resources" + FILE_SEPERATOR + "Outputs";
            } catch (IOException e) {
                System.out.println("Couldn't create IDE directories");
                Platform.exit();
                System.exit(1);
            }
        }
    }

    public static void checkCaller(){
        switch (CALLER){
            case EXE:
                Controllers.getLogController().logTextArea.appendText("EXE called\n");
                Controllers.getLogController().logTextArea.appendText("USER_DIR: " + USER_DIR + "\n");
                break;
            case JAR:
                Controllers.getLogController().logTextArea.appendText("JAR called\n");
                Controllers.getLogController().logTextArea.appendText("USER_DIR: " + USER_DIR + "\n");
                break;
            case IDE:
                Controllers.getLogController().logTextArea.appendText("IDE called\n");
                break;
            default:
                Controllers.getLogController().logTextArea.appendText("UNKNOWN called\n");
        }
    }
}
