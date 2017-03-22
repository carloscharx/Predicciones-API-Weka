import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.yahoo.labs.samoa.instances.Instance;
import moa.classifiers.functions.Perceptron;
import moa.classifiers.functions.SGD;

import moa.classifiers.meta.RandomRules;
import moa.classifiers.rules.AMRulesRegressor;
import moa.classifiers.rules.AMRulesRegressorOld;
import moa.classifiers.rules.functions.FadingTargetMean;
import moa.classifiers.rules.functions.TargetMean;
import moa.classifiers.rules.meta.RandomAMRules;
import moa.classifiers.trees.ORTO;
import moa.core.TimingUtils;
import moa.streams.ArffFileStream;

import java.io.PrintWriter;

import static java.lang.Double.isNaN;

/**
 * Created by carloscharx on 20/03/2017.
 */
public class MOALag {


    public static void main(String[] args) {
/*
        ////////////////ORTO///////////////////
        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;
            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAORTO" + ".txt");
            writer.println("Resultados globales MOA ORTO");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("ORTO");

                for (int w = 0; w < links_num; w++) {


                    ORTO learner = new ORTO();
                    learner.maxTreesOption = new IntOption("numeroarboles", 'i', "numeroarboles", 1);

                    learner.maxOptionLevelOption = new IntOption("maximonivel", 'j', "maximonivel", 10);

                    learner.optionDecayFactorOption = new FloatOption("decay", 'k', "decay", 0.9);

                    learner.optionFadingFactorOption = new FloatOption("fading", 'b', "fading", 0.9995);

                    learner.gracePeriodOption = new IntOption("grace", 'g', "grace", 20000);

                    learner.splitConfidenceOption = new FloatOption("conf", 'c', "conf", 10e-7);

                    learner.tieThresholdOption = new FloatOption("tie", 't', "tie", 0.05);

                    learner.PageHinckleyThresholdOption = new IntOption("Hinckley", 'h', "Hinckley", 50);


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        ///////////////////////SGD/////////////////////////////////
        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;
            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOASGD" + ".txt");
            writer.println("Resultados globales MOA SGD");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("SGD");

                for (int w = 0; w < links_num; w++) {


                    SGD learner = new SGD();


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
*/

        //////////RandomRules///////////////

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOARandomRules" + ".txt");
            writer.println("Resultados globales MOA Random Rules");
            for (int h = 2; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("RandomRules");

                for (int w = 0; w < links_num; w++) {

                    RandomRules learner = new RandomRules();


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

/*
        //AMRulesRegressor
        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAAMRulesRegressor" + ".txt");
            writer.println("Resultados globales MOA AMRulesRegressor");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("AMRulesRegressor");

                for (int w = 0; w < links_num; w++) {



                    AMRulesRegressor learner = new AMRulesRegressor();


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }



        //AMRulesRegressorOLD

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAAMRROld" + ".txt");
            writer.println("Resultados globales MOA AM Rules Regressor OLD");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("AM Rules Regressor OLD");

                for (int w = 0; w < links_num; w++) {



                    AMRulesRegressorOld learner = new AMRulesRegressorOld();


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //FadingTargetMean

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAFadingTarget" + ".txt");
            writer.println("Resultados globales MOA FadingTarget");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("FadingTarget");

                for (int w = 0; w < links_num; w++) {


                    FadingTargetMean learner = new FadingTargetMean();



                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }



        // Fading Target Mejorado///////////

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAFadingTargetMejorado" + ".txt");
            writer.println("Resultados globales MOA Fading Targe tMejorado");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("Fading Target Mejorado");

                for (int w = 0; w < links_num; w++) {



                    FadingTargetMean learner = new FadingTargetMean();
                    learner.fadingFactorOption= new FloatOption("fading",'i',"fading",0.6);

                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //////Perceptron/////////

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOAPerceptron" + ".txt");
            writer.println("Resultados globales MOA Perceptron");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("RandomPerceptron");

                for (int w = 0; w < links_num; w++) {



                    Perceptron learner = new Perceptron();



                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        /////TargetMean/////

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOATargetMean" + ".txt");
            writer.println("Resultados globales MOA TargetMean");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";

                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("TargetMean");

                for (int w = 0; w < links_num; w++) {



                    TargetMean learner = new TargetMean();



                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        ////// Random AM Rules /////

        try {
            int iteraciones = 12;
            int links_num = 979;
            int instances_total = 2016;
            int instances_num = 288;

            String ruta = "";
            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosLAGMOARandomAMRules" + ".txt");
            writer.println("Resultados globales MOA RandomAMRules");
            for (int h = 1; h <= iteraciones; h++) {

                writer.println("Lag"+h);
                ruta = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosMOALag"+h+"/link";


                double[][] errores_acumulados = new double[instances_total][links_num];

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                writer.println("RandomAMRules");

                for (int w = 0; w < links_num; w++) {



                    RandomAMRules learner = new RandomAMRules();


                    ArffFileStream stream = new ArffFileStream(ruta + w + ".arff", -1);
                    stream.prepareForUse();
                    learner.prepareForUse();

                    double[] error_enlace = new double[instances_total];
                    int i = 0;
                    Instance trainInst = stream.nextInstance().getData();
                    while (stream.hasMoreInstances()) {
                        Instance testInst = trainInst;
                        learner.trainOnInstance(trainInst);
                        trainInst = stream.nextInstance().getData();
                        if (i >= instances_total - instances_num && i <= instances_total) {
                            double prediccion = learner.getPredictionForInstance(testInst).getVote(0, 0);
                            if (prediccion > 1)
                                prediccion = 1;
                            else if (prediccion < 0)
                                prediccion = 0;
                            double error = Math.abs(prediccion - trainInst.value(0));

                            error_enlace[i] = error;
                            errores_acumulados[i][w] = error;
                        }


                        i++;
                    }
                    double mae = 0;
                    for (int k = 0; k < instances_total; k++) {
                        if (!isNaN(error_enlace[k])) {
                            mae += error_enlace[k];
                        }

                    }
                    mae = mae / instances_num;
                    System.out.println("La mae del enlace " + w + " es " + mae);

                }

                double maeTotal = 0;
                int fallos = 0;
                for (int k = 0; k < instances_total; k++) {
                    for (int j = 0; j < links_num; j++) {
                        if (!isNaN(errores_acumulados[k][j])) {
                            maeTotal += errores_acumulados[k][j];
                        } else {
                            fallos++;
                        }
                    }
                }
                double divisor = instances_num * links_num - fallos;
                maeTotal = maeTotal / divisor;
                System.out.println("La mae total es " + maeTotal);
                writer.print(maeTotal);
                writer.print(" ");
                System.out.println("Ha habido un total de " + fallos + " fallos en el predictor");

//                    double[] evolucion_error = new double[instances_total];
//                    for (int w = 0; w < instances_total; w++) {
//                        evolucion_error[w] = 0;
//                        for (int k = 0; k < links_num; k++) {
//                            if (!isNaN(errores_acumulados[w][k])) {
//                                evolucion_error[w] += errores_acumulados[w][k];
//                            }
//
//                        }
//                        evolucion_error[w] = evolucion_error[w] / links_num;
//                        System.out.println("Evolución instante " + w + " :" + evolucion_error[w]);
//                        writer.println(evolucion_error[w]);
//                    }
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread() - evaluateStartTime);
                System.out.println(time);
                writer.println(time);


            }
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

*/
    }


}



