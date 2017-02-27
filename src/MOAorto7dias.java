import com.github.javacliparser.FlagOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.MultiChoiceOption;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.yahoo.labs.samoa.instances.Prediction;
import moa.classifiers.functions.Perceptron;
import moa.classifiers.functions.SGD;
import moa.classifiers.meta.RandomRules;
import moa.classifiers.rules.AMRulesRegressor;
import moa.classifiers.rules.functions.FadingTargetMean;
import moa.classifiers.rules.functions.TargetMean;
import moa.classifiers.rules.meta.RandomAMRules;
import moa.classifiers.trees.*;
import moa.streams.ArffFileStream;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.lazy.kNN;
import moa.core.TimingUtils;

import static java.lang.Double.isNaN;


/**
 * Created by carloscharx on 18/01/2017.
 */
public class MOAorto7dias {

    public static void main(String[] args) {


        int links_num = 979; // Número de enlaces final obtenido
        int instances_num = 2016; // 7 días
        double[][] errores_acumulados = new double[instances_num-1][links_num];

        boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
        long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

        for(int w=0; w<links_num;w++){
            ORTO learner = new ORTO();

//            FlagOption opcion= new FlagOption("regresion",'r',"fijar arbol de regresion");
//            opcion.setValue(true);
//            learner.regressionTreeOption=opcion;

            if(w==0){
                System.out.println(learner.maxTreesOption.getValue());
                System.out.println(learner.maxOptionLevelOption.getValue());
                System.out.println(learner.optionDecayFactorOption.getValue());
                System.out.println(learner.optionFadingFactorOption.getValue());
                System.out.println(learner.gracePeriodOption.getValue());
                System.out.println(learner.splitConfidenceOption.getValue());
                System.out.println(learner.tieThresholdOption.getValue());
                System.out.println(learner.PageHinckleyThresholdOption.getValue());

            }
            learner.maxTreesOption= new IntOption("numeroarboles",'i',"numeroarboles",1);

            learner.maxOptionLevelOption = new IntOption("maximonivel",'j',"maximonivel",10);

            learner.optionDecayFactorOption = new FloatOption("decay",'k',"decay",0.9);

            learner.optionFadingFactorOption = new FloatOption("fading",'b',"fading",0.9995);

            learner.gracePeriodOption = new IntOption("grace",'g',"grace",20000);

            learner.splitConfidenceOption = new FloatOption("conf",'c',"conf",10e-7);

            learner.tieThresholdOption = new FloatOption("tie",'t',"tie",0.05);

            learner.PageHinckleyThresholdOption = new IntOption("Hinckley",'h',"Hinckley",50);


            ArffFileStream stream = new ArffFileStream("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOAatributos/link" + w + ".arff",-1);
            stream.prepareForUse();
            learner.setModelContext(stream.getHeader());
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
            double mae=0;
            for(int k=0;k<2015;k++){
                if(!isNaN(error_enlace[k])){
                    mae+=error_enlace[k];
                }

            }
            mae=mae/2016;
            System.out.println("La mae del enlace " + w + " es " + mae);

            if(w==0){
                System.out.print(learner.getModel().toString());
            }

        }

        double maeTotal=0;
        int fallos=0;
        for(int k=0;k<2015;k++){
            for(int j=0;j<979;j++){
                if(!isNaN(errores_acumulados[k][j])){
                    maeTotal+=errores_acumulados[k][j];
                } else{
                    fallos++;
                }
            }
        }
        double divisor=2015*979-fallos;
        maeTotal=maeTotal/divisor;
        System.out.println("La mae total es " + maeTotal);
        System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");


        double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);
        System.out.println(time);




    }
}
