import Entities.Controllers;
import Processors.*;
import ViewControllers.MainController;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.sf.javaml.utils.ArrayUtils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;

public class Main extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Views/main.fxml"));
        Parent root = loader.load();
        stage.setTitle("VideoText Extractor");
        stage.setScene(new Scene(root, 680, 390));
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e -> Platform.exit());

        stage.getIcons().add(new Image(Main.class.getResourceAsStream("Icons/app.png")));
        Controllers.setMainController(loader);
        MainController.setMainStage(stage);

        FileProcessor.loadLibraries();

        stage.setOnCloseRequest(e -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.load("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Natives\\opencv_320_64.dll");
//        double[][] train = Training.getTrainingData();
//        SVM.setParameters();
//        SVM.createProblem(train,Training.getLabels(train.length));
//        SVM.save("svm_model_big_urlbp_abs.model",SVM.train());
//        File dir = new File("C:\\Users\\310297685\\IdeaProjects\\Thesis\\VideoText_Extractor\\src\\main\\resources\\Outputs\\MegaMan Thu Nov 02 13-00-20 GMT 2017\\Temp");
//        for (File img : dir.listFiles()){
//            Mat test = Imgcodecs.imread(img.getAbsolutePath());
//            Imgproc.resize(test,test,new Size(100,50));
//            int[] data = URLBP.getURLBFeatures(test);
//            double[] dData = Doubles.toArray(Ints.asList(data));
//            ArrayUtils.normalize(dData);
//            System.out.println(img.getName());
//            SVM.evaluate(dData,SVM.loadModel());
//            System.out.println();
//        }
        //Test.test();

        //URLBP.test();
        launch(args);
    }

}