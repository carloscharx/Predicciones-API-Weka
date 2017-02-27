import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.meta.WEKAClassifier;
import moa.classifiers.trees.ORTO;
import moa.options.WEKAClassOption;
import moa.streams.ArffFileStream;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static java.lang.Double.isNaN;




/**
 * Created by carloscharx on 08/02/2017.
 */
public class MOAWeka {

    public static void main(String[] args) {

        try {
            int links_num = 979; // Número de enlaces final obtenido
            int instances_num = 2016; // 7 días
            double[][] errores_acumulados = new double[instances_num-1][links_num];

            for (int w = 0; w < links_num; w++) {
                WEKAClassifier learner = new WEKAClassifier();
                learner.baseLearnerOption.setValueViaCLIString("weka.classifiers.trees.M5P -R -M 4.0");
                learner.sampleFrequencyOption.setValueViaCLIString("1");
                learner.widthOption.setValueViaCLIString("288");
                System.out.println(learner.baseLearnerOption.getValueAsCLIString());
                System.out.println(learner.sampleFrequencyOption.getValueAsCLIString());
                System.out.println(learner.widthInitOption.getValueAsCLIString());
                System.out.println(learner.widthOption.getValueAsCLIString());

                ArffFileStream stream = new ArffFileStream("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOA/link" + w + ".arff", -1);
                stream.prepareForUse();
                learner.prepareForUse();

                double[] error_enlace = new double[2016];
                int i = 0;
                Instance trainInst = stream.nextInstance().getData();
                while(stream.hasMoreInstances()){
                    Instance testInst=trainInst;
                    learner.trainOnInstance(trainInst);
                    trainInst = stream.nextInstance().getData();
                    if (i<instances_num-1){
                        double prediccion= learner.getPredictionForInstance(testInst).getVote(0,0);
                        if(prediccion >1)
                            prediccion=1;
                        else if(prediccion <0)
                            prediccion=0;
                        double error = Math.abs(prediccion-trainInst.value(0));

                        error_enlace[i]=error;
                        errores_acumulados[i][w]=error;
                    }


                    i++;
                }
                double mae = 0;
                for (int k = 0; k < 2015; k++) {
                    if (!isNaN(error_enlace[k])) {
                        mae += error_enlace[k];
                    }

                }
                mae = mae / 2016;
                System.out.println("La mae del enlace " + w + " es " + mae);

            }

            double maeTotal = 0;
            for (int k = 0; k < 2015; k++) {
                for (int j = 0; j < 979; j++) {
                    if (!isNaN(errores_acumulados[k][j])) {
                        maeTotal += errores_acumulados[k][j];
                    }
                }
            }
            double divisor = 2015 * 979;
            maeTotal = maeTotal / divisor;
            System.out.println("La mae total es " + maeTotal);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
