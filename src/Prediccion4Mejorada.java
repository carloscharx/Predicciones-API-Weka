import moa.core.TimingUtils;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.eval.MAEModule;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by carloscharx on 01/02/2017.
 */

/**
 * Example of using the time series forecasting API. To compile and
 * run the CLASSPATH will need to contain:
 *
 * weka.jar (from your weka distribution)
 * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
 * jcommon-1.0.14.jar (from the time series package lib directory)
 * jfreechart-1.0.13.jar (from the time series package lib directory)
 */
public class Prediccion4Mejorada {

    public static void main(String[] args) {

        try {
            //int links_num = 979; // Número de enlaces final obtenido
            //int links_num = 610; // Número de enlaces final obtenido
            int links_num = 325; // Número de enlaces final obtenido

            // Los algortimos que vamos a usar:
            String[] algoritmos= {"RT", "SMOreg", "IBk", "GaussianProcesses"};
            int algorithms_num = 4;
            int training =288;
            int test = 1728;
            double[][] errores_acumulados = new double[algorithms_num][links_num];
            PrintWriter tiempos = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultados1diaSiTimestampconPotenciasTiemposMAE" +".txt");
            for(int i = 0;i<algorithms_num;i++) {
                PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultados1diaSiTimestampconPotencias" + algoritmos[i] + ".txt");

                boolean preciseCPUTiming = TimingUtils.enablePreciseTiming();
                long evaluateStartTime = TimingUtils.getNanoCPUTimeOfCurrentThread();

                double sum = 0.0;


                for (int j = 0; j < links_num; j++) {
                    // rutas de los datos
                    String pathToData = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWeka1hora/link" + j + ".arff";

                    // se cargan los datos de entrenamiento y test
                    Instances data_set = new Instances(new BufferedReader(new FileReader(pathToData)));
                    Instances training_set = new Instances(data_set,0,training);


                    // Se crea un nuevo predictor
                    WekaForecaster forecaster = new WekaForecaster();

                    // Se elije que queremos predecir, en este caso el Link j-ésimo
                    forecaster.setFieldsToForecast("Link" + j);

                    // Elegimos el algoritmo para cada una de las iteraciones
                    switch (i){
                        case 0:
                        {   //String[] opciones={"-R"};
                            REPTree predictor = new REPTree();
                            //predictor.setOptions(opciones);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                        case 1:
                        {   //String[] options ={"-N","1"};
                            SMOreg predictor = new SMOreg();
                            //predictor.setOptions(options);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                        case 2:
                        {
                            //LinearNNSearch algoritmobusqueda=new LinearNNSearch();
                            //algoritmobusqueda.setDistanceFunction(new ManhattanDistance());
                            //String[] options ={"-X"};
                            int k=10;
                            IBk predictor = new IBk(k);
                            //predictor.setOptions(options);
                            //predictor.setNearestNeighbourSearchAlgorithm(algoritmobusqueda);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                        case 3:
                        {
                            //String[] options ={"-N","1"};
                            GaussianProcesses predictor = new GaussianProcesses();
                            //predictor.setOptions(options);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                    }



                    forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
                    forecaster.getTSLagMaker().setMinLag(1);
                    forecaster.getTSLagMaker().setMaxLag(12);
                    //forecaster.getTSLagMaker().setAdjustForTrends(false); //no timestamp
                    forecaster.getTSLagMaker().setIncludePowersOfTime(true);
                    forecaster.getTSLagMaker().setIncludeTimeLagProducts(true);

                    // Contruye el modelo
                    forecaster.buildForecaster(training_set, System.out);
                    //System.out.println(forecaster.toString());


                    double[] error_enlace = new double[test];
                    for(int k=0;k<test;k++){
                        // prime the forecaster with enough recent historical data
                        // to cover up to the maximum lag. In our case, we could just supply
                        // the 12 most recent historical instances, as this covers our maximum
                        // lag period
                        forecaster.primeForecaster(new Instances(data_set,training-12+k,12));
                        // Predice 1 unidad desde el fin del conjunto de datos con los que ha alimentado al predictor
                        List<List<NumericPrediction>> forecast = forecaster.forecast(1, System.out);

                        // Aquí hay que saturar los valores, descomentar cuando se use e intentar reducir codigo

                        for (List<NumericPrediction> predAtStep : forecast) {
                            for (NumericPrediction predForTarget : predAtStep) {
                                if (predForTarget.predicted() > 1.0) {
                                    predAtStep.set(predAtStep.indexOf(predForTarget), new NumericPrediction(predForTarget.actual(), new Double(1)));
                                } else if (predForTarget.predicted() < 0.0) {
                                    predAtStep.set(predAtStep.indexOf(predForTarget), new NumericPrediction(predForTarget.actual(), new Double(0)));
                                }
                            }
                            forecast.set(forecast.indexOf(predAtStep), predAtStep);
                        }

                        // Ahora se imprimen, descomentar si fuese necesario

                        /*for (int u = 0; u < 10; u++) {
                            List<NumericPrediction> predsAtStep = forecast.get(u);
                                for (int w = 0; w < 1; w++) {
                            NumericPrediction predForTarget = predsAtStep.get(w);
                            System.out.print("" + predForTarget.predicted() + " ");
                            }
                        System.out.println();
                        }
*/
                        double[] error;

                        List<String> fields = new ArrayList();
                        fields.add("Link" + j);
                        MAEModule calculoMae = new MAEModule();
                        calculoMae.setTargetFields(fields);
                        List<NumericPrediction> predsAtStep = forecast.get(0);
                        calculoMae.evaluateForInstance(predsAtStep, data_set.get(training+k));
                        error = calculoMae.calculateMeasure();
                        error_enlace[k]=error[0];

                    }



                    double mae=0;
                    for(int k=0;k<test;k++){
                        mae+=error_enlace[k];
                    }
                    mae=mae/test;
                    //System.out.println("La MAE es " + mae);
                    errores_acumulados[i][j]=mae;

                    sum = 0.0;
                    for (int k = 0; k <= j; k++) {
                        sum += errores_acumulados[i][k];
                    }
                    sum = sum / (j + 1);
                    //System.out.println(sum);
                    //System.out.println("Fin de la iteración número " + (j + 1));
                    writer.println(errores_acumulados[i][j]);
                }
                System.out.println(sum);
                double time = TimingUtils.nanoTimeToSeconds(TimingUtils.getNanoCPUTimeOfCurrentThread()- evaluateStartTime);
                System.out.println(time);
                tiempos.print(time);
                tiempos.print(" ");
                tiempos.println(sum);
                writer.close();
            }
            tiempos.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}