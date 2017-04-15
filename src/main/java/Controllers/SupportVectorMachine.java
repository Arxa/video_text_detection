package Controllers;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import libsvm.*;
import net.sf.javaml.core.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by arxa on 9/4/2017.
 */

public class SupportVectorMachine
{
    private static svm_problem prob = new svm_problem();
    private static svm_parameter param = new svm_parameter();


    /*
    public static svm_parameter gridSearch(double[][] train, svm_parameter param)
    {
        Dataset dataset = new DefaultDataset();
        for (int i = 0; i < Training.getNumber_of_text_data() * Training.SUBREGIONS; i++)
        {
            Instance instance = new SparseInstance();
            for (int y = 0; y < train[i].length; y++)
            {
                instance.put(y, train[i][y]);
            }
            instance.setClassValue("text");
            dataset.add(instance);
        }

        for (int i = Training.getNumber_of_text_data() * Training.SUBREGIONS; i < train.length; i++)
        {
            Instance instance = new SparseInstance();
            for (int y = 0; y < train[i].length; y++)
            {
                instance.put(y, train[i][y]);
            }
            instance.setClassValue("background");
            dataset.add(instance);
        }

        double[] C = new double[11];
        double[] Y = new double[11];

        for (int i = 0; i <= 10; i++) {
            C[i] = Math.pow(2.0,(double)i);
        }

        int v = 11;
        for (int i = 0; i <= 10; i++) {
            Y[i] = 1.0 / Math.pow(2.0,(double)v--);
        }

        //Instances data = new Instances("myData",vector,3000);

        LibSVM libsvm = new LibSVM();
        //libsvm.buildClassifier(dataset);
        //libsvm.setParameters(param);

        GridSearch gr = new GridSearch(libsvm,dataset,10);
        System.out.println("ok");
        return gr.search(libsvm.getParameters(),C,Y);
    }
    */

    public static void setParameters()
    {
        //param.C = 1;
        param.cache_size = 20000;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        //param.gamma = 1;
       // param.gamma = 0.015625; // Y parameter of RBF kernel
    }

    public static void testGridSearch() throws Exception {
        double[] C = new double[11];
        double[] Y = new double[11];

        // C: 2^0...2^10
        for (int i = 0; i <= 10; i++) {
            C[i] = Math.pow(2.0,(double)i);
        }

        // Y: 2^-11...2^-1
        int v = 11;
        for (int i = 0; i <= 10; i++) {
            Y[i] = 1.0 / Math.pow(2.0,(double)v--);
        }
        int result;
        for (int c=1; c <= 10; c++ ) {
            for (int y = 0; y <= 10; y++) {

                final int finalC = c;
                final int finalY = y;

                TimeLimiter limiter = new SimpleTimeLimiter();

                String res = limiter.callWithTimeout(new Callable<String>() {
                    public String call() {
                        return gridSearch_test(finalC,finalY);
                    }
                }, 5, TimeUnit.SECONDS, false);
                //String r = gridSearch_test(finalC,finalY);
                System.out.printf("Calculated c:"+c+", y:"+y+", accuracy: "+res);
            }
        }

    }

    public static void gridSearch()
    {
        double[] target = new double[prob.y.length];
        double[] C = new double[11];
        double[] Y = new double[11];

        // C: 2^0...2^10
        for (int i = 0; i <= 10; i++) {
            C[i] = Math.pow(2.0,(double)i);
        }

        // Y: 2^-11...2^-1
        int v = 11;
        for (int i = 0; i <= 10; i++) {
            Y[i] = 1.0 / Math.pow(2.0,(double)v--);
        }

        double bestC = 0, bestY = 0;
        int accuracy = 0;
        for (int c=0; c <= 10; c++ )
        {
            for (int y=0; y <= 10; y++ )
            {
                System.out.printf("Calculating c="+c+", y="+y);
                param.C = C[c];
                param.gamma = Y[y];
                svm.svm_cross_validation(prob,param,3,target);
                int correctCounter = 0;
                for (int k=0; k < target.length; k++)
                {
                    if (Double.compare(target[k],prob.y[k]) == 0) {
                        correctCounter++;
                    }
                }
                if (correctCounter > accuracy)
                {
                    accuracy = correctCounter;
                    bestC = C[c];
                    bestY = Y[y];
                }
            }
        }
        System.out.println("Best C: "+bestC+" Best Y: "+bestY+" Accuracy: "+accuracy);
    }

    public static String gridSearch_test(int c, int y)
    {
        boolean flag = false;
        double[] target = new double[prob.y.length];

        double bestC = 0, bestY = 0;
        int accuracy = 0;
        param.C = c;
        param.gamma = y;
        svm.svm_cross_validation(prob,param,3,target);
        int correctCounter = 0;
        for (int k=0; k < target.length; k++)
        {
            if (Double.compare(target[k],prob.y[k]) == 0) {
                correctCounter++;
            }
        }
        if (correctCounter > accuracy) {
            accuracy = correctCounter;
        }
        flag = true;
        if (flag){
            return String.valueOf(accuracy);
        }
        else return "-1";
    }

    public static void createProblem(double[][] train, double[] labels)
    {
        int dataCount = train.length;
        prob.y = new double[dataCount];
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
        prob.y = Arrays.copyOf(labels,dataCount); // check this.
    }

    public static svm_model train()
    {
        return svm.svm_train(prob, param); // returns the model
    }

    public static void save(String filename,svm_model model)
    {
        try {
            svm.svm_save_model("src\\main\\resources\\SVM_Files\\"+filename, model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static svm_model load(String filepath) throws IOException
    {
        return svm.svm_load_model(filepath);
    }

    public double evaluate(double[] features, svm_model model)
    {
        svm_node[] nodes = new svm_node[features.length];

        for (int i = 0; i < features.length; i++)
        {
            svm_node node = new svm_node();
            node.index = i;
            node.value = features[i];

            nodes[i] = node;
        }

        int totalClasses = 2;
        int[] labels = new int[totalClasses];
        svm.svm_get_labels(model,labels);

        double[] prob_estimates = new double[totalClasses];
        double v = svm.svm_predict_probability(model, nodes, prob_estimates);

        for (int i = 0; i < totalClasses; i++){
            System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ")");
        }
        System.out.println("(Actual:" + features[0] + " Prediction:" + v + ")");

        return v;
    }


    public static int predict( )
    {
        //return (int) svm.predict(test);
        return 0;
    }

    public static svm_parameter getParam() {
        return param;
    }
}
