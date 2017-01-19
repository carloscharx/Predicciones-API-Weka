import com.github.javacliparser.IntOption;
import moa.classifiers.functions.Perceptron;
import moa.classifiers.meta.RandomRules;
import moa.classifiers.trees.*;
import moa.streams.ArffFileStream;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.lazy.kNN;

import static java.lang.Double.isNaN;


/**
 * Created by carloscharx on 18/01/2017.
 */
public class MOAorto {

    public static void main(String[] args) {


        int links_num = 979; // Número de enlaces final obtenido
        int instances_num = 2016; // 7 días
        double[][] errores_acumulados = new double[instances_num][links_num];

        for(int w=0; w<links_num;w++){
            ORTO learner = new ORTO();
            ArffFileStream stream = new ArffFileStream("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOA/link" + w + ".arff",-1);
            stream.prepareForUse();
            learner.prepareForUse();

            double[] error_enlace = new double[2016];
            int i = 0;
            while(stream.hasMoreInstances()){

                Instance trainInst = stream.nextInstance().getData();
                double prediccion= learner.getVotesForInstance(trainInst)[0];
                if(prediccion >1)
                    prediccion=1;
                else if(prediccion <0)
                    prediccion=0;
                double error = Math.abs(prediccion-trainInst.value(0));

                error_enlace[i]=error;
                errores_acumulados[i][w]=error;
                learner.trainOnInstance(trainInst);
                i++;
            }
            double mae=0;
            for(int k=0;k<2016;k++){
                if(!isNaN(error_enlace[k])){
                    mae+=error_enlace[k];
                }

            }
            mae=mae/2016;
            System.out.println("La mae del enlace " + w + " es " + mae);

        }

        double maeTotal=0;
        for(int k=0;k<2016;k++){
            for(int j=0;j<979;j++){
                if(!isNaN(errores_acumulados[k][j])){
                    maeTotal+=errores_acumulados[k][j];
                }
            }
        }
        double divisor=2016*979;
        maeTotal=maeTotal/divisor;
        System.out.println("La mae total es " + maeTotal);

    }
}
