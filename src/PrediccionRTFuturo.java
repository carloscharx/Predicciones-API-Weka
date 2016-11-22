import java.io.*;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.core.EuclideanDistance;
import weka.core.ManhattanDistance;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomTree;
import weka.core.Instances;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.eval.MAEModule;
//import weka.classifiers.timeseries.core.TSLagMaker; esto parece que ya no existe, existe esto:
import weka.core.MinkowskiDistance;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.supervised.attribute.TSLagMaker;

/**
 * Example of using the time series forecasting API. To compile and
 * run the CLASSPATH will need to contain:
 * <p>
 * weka.jar (from your weka distribution)
 * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
 * jcommon-1.0.14.jar (from the time series package lib directory)
 * jfreechart-1.0.13.jar (from the time series package lib directory)
 */
public class PrediccionRTFuturo {

    public static void main(String[] args) {
        try {
            int links_num = 979; // Número de enlaces final obtenido
            int instantesfuturo = 8;
            double[][] errores_acumulados = new double[instantesfuturo][links_num];
            PrintWriter writer1 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 1 + ".txt");
            PrintWriter writer2 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 2 + ".txt");
            PrintWriter writer3 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 3 + ".txt");
            PrintWriter writer4 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 4 + ".txt");
            PrintWriter writer5 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 5 + ".txt");
            PrintWriter writer6 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 6 + ".txt");
            PrintWriter writer7 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 7 + ".txt");
            PrintWriter writer8 = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTfuturo" + 8 + ".txt");
            for (int j = 0; j < links_num; j++) {
                // rutas de los datos
                String pathToData = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWeka/link" + j + ".arff";

                // se cargan los datos de entrenamiento y test
                Instances data_set = new Instances(new BufferedReader(new FileReader(pathToData)));
                Instances training_set = new Instances(data_set, 0, 1728);


                // Se crea un nuevo predictor
                WekaForecaster forecaster = new WekaForecaster();

                // Se elije que queremos predecir, en este caso el Link j-ésimo
                forecaster.setFieldsToForecast("Link" + j);


                forecaster.setBaseForecaster(new REPTree());


                forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
                forecaster.getTSLagMaker().setMinLag(1);
                forecaster.getTSLagMaker().setMaxLag(12);

                // Contruye el modelo
                forecaster.buildForecaster(training_set, System.out);

                double[][] error_enlace = new double[instantesfuturo][288 - instantesfuturo + 1];
                for (int k = 0; k < 288 - instantesfuturo + 1; k++) {
                    // prime the forecaster with enough recent historical data
                    // to cover up to the maximum lag. In our case, we could just supply
                    // the 12 most recent historical instances, as this covers our maximum
                    // lag period
                    forecaster.primeForecaster(new Instances(data_set, 1716 + k, 12));
                    // Predice 8 unidades desde el fin del conjunto de datos con los que ha alimentado al predictor
                    List<List<NumericPrediction>> forecast = forecaster.forecast(8, System.out);

                    // Aquí hay que saturar los valores, descomentar cuando se use e intentar reducir codigo

                        /*for (List<NumericPrediction> predAtStep : forecast) {
                            for (NumericPrediction predForTarget : predAtStep) {
                                if (predForTarget.predicted() > 1.0) {
                                    predAtStep.set(predAtStep.indexOf(predForTarget), new NumericPrediction(predForTarget.actual(), new Double(1)));
                                } else if (predForTarget.predicted() < 0.0) {
                                    predAtStep.set(predAtStep.indexOf(predForTarget), new NumericPrediction(predForTarget.actual(), new Double(0)));
                                }
                            }
                            forecast.set(forecast.indexOf(predAtStep), predAtStep);
                        }*/

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

                    for (int w = 0; w < instantesfuturo; w++) {
                        MAEModule calculoMae = new MAEModule();
                        calculoMae.setTargetFields(fields);
                        List<NumericPrediction> predsAtStep = forecast.get(w);
                        calculoMae.evaluateForInstance(predsAtStep, data_set.get(1728 + k + w));
                        error = calculoMae.calculateMeasure();
                        error_enlace[w][k] = error[0];
                    }


                }

                for(int w=0;w<instantesfuturo;w++){
                    double mae = 0;
                    for (int k = 0; k < 288-instantesfuturo+1; k++) {
                        mae += error_enlace[w][k];
                    }
                    mae = mae / (288-instantesfuturo+1);
                    //System.out.println("La MAE es " + mae);
                    errores_acumulados[w][j] = mae;
                }




                /*double sum = 0.0;
                for (int k = 0; k <= j; k++) {
                    sum += errores_acumulados[instantesfuturo - 1][k];
                }
                sum = sum / (j + 1);
                System.out.println(sum);*/

                System.out.println("Fin de la iteración número " + (j + 1));
                writer1.println(errores_acumulados[0][j]);
                writer2.println(errores_acumulados[1][j]);
                writer3.println(errores_acumulados[2][j]);
                writer4.println(errores_acumulados[3][j]);
                writer5.println(errores_acumulados[4][j]);
                writer6.println(errores_acumulados[5][j]);
                writer7.println(errores_acumulados[6][j]);
                writer8.println(errores_acumulados[7][j]);
            }
            writer1.close();
            writer2.close();
            writer3.close();
            writer4.close();
            writer5.close();
            writer6.close();
            writer7.close();
            writer8.close();



        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}