package Entities;

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
    public static String FOLDERPATH;

    /***
     * Sets the application's directory paths differently whether it is a JAR file calling or an IDE
     */
    public static void setApplicationPaths()
    {
        final File jarFile = new File(ApplicationPaths.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        if(jarFile.isFile()) {
            if (FilenameUtils.getExtension(jarFile.getPath()).equals("exe")){
                CALLER = Caller.EXE;
                System.out.println("exe called");
                USER_DIR = System.getProperty("user.dir").replace("\\", "/");
                System.out.print("Executing at =>"+USER_DIR);
            }

            if (FilenameUtils.getExtension(jarFile.getPath()).equals("jar")){
                CALLER = Caller.JAR;
                System.out.println("jar called");
                USER_DIR = jarFile.getParent();
                System.out.println("Executing at =>"+USER_DIR);
            }

            RESOURCES = USER_DIR;
            RESOURCES_NATIVES = USER_DIR + "\\Natives";
            RESOURCES_OCR = USER_DIR + "\\OCR";
            RESOURCES_OCR_TESSDATA = USER_DIR + "\\OCR\\tessdata";
            RESOURCES_OCR_LANGUAGES = USER_DIR + "\\OCR\\languages\\";
            FOLDERPATH = USER_DIR + "\\Outputs\\";

        } else {
            CALLER = Caller.IDE;
            try {
                IDE_DIR = new File(".").getCanonicalPath() + "\\src\\main\\resources\\";
                RESOURCES = IDE_DIR;
                RESOURCES_NATIVES = IDE_DIR + "Natives";
                RESOURCES_OCR = IDE_DIR + "OCR";
                RESOURCES_OCR_TESSDATA = IDE_DIR + "OCR\\tessdata";
                RESOURCES_OCR_LANGUAGES = "src/main/resources/OCR/languages/";
                FOLDERPATH = "src\\main\\resources\\Outputs\\";
            } catch (IOException e) {
                System.out.println("Couldn't create IDE directories");
            }
        }
    }

    public static void checkCaller(){
        switch (CALLER){
            case EXE:
                System.out.println("EXE called");
                Controllers.getLogController().logTextArea.appendText("EXE called\n");
                break;
            case JAR:
                System.out.println("JAR called");
                Controllers.getLogController().logTextArea.appendText("JAR called\n");
                break;
            case IDE:
                System.out.println("IDE called");
                Controllers.getLogController().logTextArea.appendText("IDE called\n");
                break;
            default:
                System.out.println("UNKNOWN called");
                Controllers.getLogController().logTextArea.appendText("UNKNOWN called\n");
        }

        System.out.println("USER_DIR: " + USER_DIR);
        Controllers.getLogController().logTextArea.appendText("USER_DIR: " + USER_DIR + "\n");
    }
}
