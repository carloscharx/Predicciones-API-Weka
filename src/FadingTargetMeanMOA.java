/**
 * Created by carloscharx on 13/02/2017.
 */


import com.github.javacliparser.FloatOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.rules.functions.FadingTargetMean;
import moa.classifiers.trees.ORTO;
import moa.streams.ArffFileStream;

import java.io.PrintWriter;

import static java.lang.Double.doubleToRawLongBits;
import static java.lang.Double.isNaN;

/**
 * Created by carloscharx on 02/02/2017.
 */
public class FadingTargetMeanMOA {


    public static void main(String[] args) {

        try {
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultados" + "Fading1horatrain" + ".txt");

            int links_num = 979; // Número de enlaces final obtenido
            int instances_total = 2016;
            int instances_num = 2004; // 1 día de test
            double[][] errores_acumulados = new double[instances_total][links_num];

            for (int w = 0; w < links_num; w++) {
                FadingTargetMean learner = new FadingTargetMean();
                learner.fadingFactorOption= new FloatOption("hola",'i',"hola",0.4);
                ArffFileStream stream = new ArffFileStream("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOA/link" + w + ".arff", -1);
                stream.prepareForUse();
                learner.prepareForUse();

                double[] error_enlace = new double[instances_total];
                int i = 0;
                Instance trainInst = stream.nextInstance().getData();
                while(stream.hasMoreInstances()){
                    Instance testInst=trainInst;
                    learner.trainOnInstance(trainInst);
                    trainInst = stream.nextInstance().getData();
                    if (i>=instances_total - instances_num && i<=instances_total){
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
                for (int k = 0; k < instances_total; k++) {
                    if (!isNaN(error_enlace[k])) {
                        mae += error_enlace[k];
                    }

                }
                mae = mae/instances_num;
                System.out.println("La mae del enlace " + w + " es " + mae);

            }

            double maeTotal = 0;
            int fallos=0;
            for (int k = 0; k < instances_total; k++) {
                for (int j = 0; j < 979; j++) {
                    if (!isNaN(errores_acumulados[k][j])) {
                        maeTotal += errores_acumulados[k][j];
                    }
                    else{
                        fallos++;
                    }
                }
            }
            double divisor = instances_num * 979-fallos;
            maeTotal = maeTotal / divisor;
            System.out.println("La mae total es " + maeTotal);
            System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

            double[] evolucion_error = new double[instances_total];
            for (int w = 0; w < instances_total; w++) {
                evolucion_error[w] = 0;
                for (int k = 0; k < links_num; k++) {
                    if (!isNaN(errores_acumulados[w][k])) {
                        evolucion_error[w] += errores_acumulados[w][k];
                    }

                }
                evolucion_error[w] = evolucion_error[w] / links_num;
                System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
                writer.println(evolucion_error[w]);
            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
