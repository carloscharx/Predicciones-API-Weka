import java.io.*;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.eval.MAEModule;
//import weka.classifiers.timeseries.core.TSLagMaker; esto parece que ya no existe, existe esto:
import weka.filters.supervised.attribute.TSLagMaker;

/**
 * Example of using the time series forecasting API. To compile and
 * run the CLASSPATH will need to contain:
 *
 * weka.jar (from your weka distribution)
 * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
 * jcommon-1.0.14.jar (from the time series package lib directory)
 * jfreechart-1.0.13.jar (from the time series package lib directory)
 */
public class Prediccion4Algoritmos {

    public static void main(String[] args) {
        try {
            int links_num = 979; // Número de enlaces final obtenido
            // Los algortimos que vamos a usar:
            String[] algoritmos= {"REPTRee", "SMOreg", "IBk", "GaussianProcesses"};
            int algorithms_num = 4;
            double[][] errores_acumulados = new double[algorithms_num][links_num];
            for(int i = 0;i<algorithms_num;i++) {
                PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosBis" + algoritmos[i] + ".txt");
                for (int j = 0; j < links_num; j++) {
                    // rutas de los datos de entrenamiento y test
                    String pathToDataTraining = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWekaSeparados/link" + j + "training.arff";
                    String pathToDataTest = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWekaSeparados/link" + j + "test.arff";

                    // se cargan los datos de entrenamiento y test
                    Instances training_set = new Instances(new BufferedReader(new FileReader(pathToDataTraining)));
                    Instances test_set = new Instances(new BufferedReader(new FileReader(pathToDataTest)));


                    // Se crea un nuevo predictor
                    WekaForecaster forecaster = new WekaForecaster();

                    // Se elije que queremos predecir, en este caso el Link j-ésimo
                    forecaster.setFieldsToForecast("Link" + j);

                    // Elegimos el algoritmo para cada una de las iteraciones
                    switch (i){
                        case 0:
                        {
                            forecaster.setBaseForecaster(new REPTree());
                            break;
                        }
                        case 1:
                        {   String[] options ={"-N","1"};
                            SMOreg predictor = new SMOreg();
                            predictor.setOptions(options);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                        case 2:
                        {
                            forecaster.setBaseForecaster(new IBk());
                            break;
                        }
                        case 3:
                        {
                            String[] options ={"-N","1"};
                            GaussianProcesses predictor = new GaussianProcesses();
                            predictor.setOptions(options);
                            forecaster.setBaseForecaster(predictor);
                            break;
                        }
                    }



                    forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
                    forecaster.getTSLagMaker().setMinLag(1);
                    forecaster.getTSLagMaker().setMaxLag(12);

                    // Contruye el modelo
                    forecaster.buildForecaster(training_set, System.out);

                    // prime the forecaster with enough recent historical data
                    // to cover up to the maximum lag. In our case, we could just supply
                    // the 12 most recent historical instances, as this covers our maximum
                    // lag period

                    forecaster.primeForecaster(training_set);

                    // Predice 288 unidades desde el fin del conjunto de entrenamiento
                    List<List<NumericPrediction>> forecast = forecaster.forecast(288, System.out);


                    // Aquí hay que saturar los valores,

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

                /*for (int i = 0; i < 288; i++) {
                    List<NumericPrediction> predsAtStep = forecast.get(i);
                    for (int j = 0; j < 1; j++) {
                        NumericPrediction predForTarget = predsAtStep.get(j);
                        System.out.print("" + predForTarget.predicted() + " ");
                    }
                    System.out.println();
                }*/
                    double[] error;

                    List<String> fields = new ArrayList();
                    fields.add("Link" + j);
                    MAEModule calculoMae = new MAEModule();
                    calculoMae.setTargetFields(fields);

                    for (int k = 0; k < 288; k++) {
                        List<NumericPrediction> predsAtStep = forecast.get(k);
                        calculoMae.evaluateForInstance(predsAtStep, test_set.get(k));
                    }


                    error = calculoMae.calculateMeasure();
                    System.out.println();
                    System.out.println(error[0]); // Cuidado, si imprimes el vector de double no se imprime bien
                    errores_acumulados[i][j] = error[0];
                    double sum = 0.0;
                    for (int k = 0; k <= j; k++) {
                        sum += errores_acumulados[i][k];
                    }
                    sum = sum / (j + 1);
                    System.out.println(sum);
                    System.out.println("Fin de la iteración número " + (j + 1));
                    writer.println(errores_acumulados[i][j]);
                }
                writer.close();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}