package svm;

import entities.ApplicationPaths;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.opencv.core.Mat;
import libsvm.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Support Vector Machine LIBSVM
 */
public class SVM {

    private static svm_problem prob = new svm_problem();
    private static svm_parameter param = new svm_parameter();
    private static svm_model model;
    private static final int PREDICTION_THRESHOLD = 5;

    static {
        try {
            model = svm.svm_load_model(Paths.get(ApplicationPaths.RESOURCES_MODELS,"10000.model").toFile().getAbsolutePath());
        } catch (IOException e) {
            Platform.runLater(()->{
                new Alert(Alert.AlertType.ERROR, "Failed to load SVM model!", ButtonType.OK).showAndWait();
                Platform.exit();
            });
        }
    }

    public static void setParameters()
    {
        param.svm_type = svm_parameter.ONE_CLASS;
        param.nu = 0.01;
        param.kernel_type = svm_parameter.LINEAR;
        param.probability = 1;
    }

    public static void createProblem(double[][] train, double[] labels)
    {
        int dataCount = train.length;
        prob.l = dataCount;
        prob.x = new svm_node[dataCount][];
        for (int i = 0; i < dataCount; i++)
        {
            double[] features = train[i];
            prob.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++)
            {
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j] = node;
            }
        }
        prob.y = Arrays.copyOf(labels,labels.length);
    }

    public static svm_model train() {
        System.out.println(svm.svm_check_parameter(prob,param));
        return svm.svm_train(prob, param); // returns the model
    }

    public static void save(String path, svm_model model)
    {
        try {
            svm.svm_save_model(path, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double evaluate(double[] features, svm_model model)
    {
        svm_node[] nodes = new svm_node[features.length];
        for (int i = 0; i < features.length; i++)
        {
            svm_node node = new svm_node();
            node.index = i;
            node.value = features[i];
            nodes[i] = node;
        }
        return svm.svm_predict(model,nodes);
    }

    public static boolean blockContainsText(Mat block){
        int negativeRegions = 0;
        for (Mat subregion : Training.getSubregions(block)){
            int[] intData = LocalBinaryPattern.get_ULBP_Features(subregion);
            double[] doubleData = Training.intArrayToDouble(intData);
            Training.normalizeArray(doubleData);
            double score = SVM.evaluate(doubleData,model);
            if (Double.compare(score,-1.0) == 0) negativeRegions++;
        }
        return negativeRegions <= PREDICTION_THRESHOLD;
    }
}
