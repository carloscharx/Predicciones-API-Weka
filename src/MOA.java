import com.github.javacliparser.IntOption;
import moa.classifiers.lazy.kNNwithPAWandADWIN;
import moa.classifiers.rules.functions.Perceptron;
import moa.classifiers.meta.RandomRules;
import moa.classifiers.trees.*;
import moa.streams.ArffFileStream;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.lazy.kNN;

import static java.lang.Double.isNaN;


/**
 * Created by carloscharx on 17/01/2017.
 */
public class MOA {

    public static void main(String[] args) {


        int links_num = 979; // Número de enlaces final obtenido
        int instances_num = 2015; // 7 días
        double[][] errores_acumulados = new double[instances_num][links_num];

        for(int w=0; w<links_num;w++){
            //FIMTDD learner= new FIMTDD();
            //HoeffdingTree learner = new HoeffdingTree();
            //RandomRules learner= new RandomRules();
            //kNN learner = new kNN();
            //learner.kOption=new IntOption("valor",'k',"valor",10);
            kNN learner = new kNN();
            ArffFileStream stream = new ArffFileStream("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOA/link" + w + ".arff",-1);
            stream.prepareForUse();
            learner.setModelContext(stream.getHeader());
            learner.prepareForUse();


            double[] error_enlace = new double[2015];
            int i = -1;
            while(stream.hasMoreInstances()){
                Instance trainInst = stream.nextInstance().getData();
                if(i!=-1)
                {

                    double prediccion= learner.getVotesForInstance(trainInst)[0];
                    if(prediccion >1)
                        prediccion=1;
                    else if(prediccion <0)
                        prediccion=0;
                    double error = Math.abs(prediccion-trainInst.value(0));

                    error_enlace[i]=error;
                    errores_acumulados[i][w]=error;
                }

                learner.trainOnInstanceImpl(trainInst);
                i++;
            }
            double mae=0;
            for(int k=0;k<2015;k++){
                if(!isNaN(error_enlace[k])){
                    mae+=error_enlace[k];
                }

            }
            mae=mae/2015;
            System.out.println("La mae del enlace " + w + " es " + mae);

        }

        double maeTotal=0;
        for(int k=0;k<2015;k++){
            for(int j=0;j<979;j++){
                if(!isNaN(errores_acumulados[k][j])){
                    maeTotal+=errores_acumulados[k][j];
                }
            }
        }
        double divisor=2015*979;
        maeTotal=maeTotal/divisor;
        System.out.println("La mae total es " + maeTotal);

    }
}
