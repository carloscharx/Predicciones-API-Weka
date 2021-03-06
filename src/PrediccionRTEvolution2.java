import java.io.*;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.functions.SMOreg;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.M5P;
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
public class PrediccionRTEvolution2 {

    public static void main(String[] args) {
        try {
            int links_num = 979; // Número de enlaces final obtenido
            int test_size = 288;
            double[][] errores_acumulados = new double[test_size][links_num];

            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRTEvolution2" + ".txt");
            for (int j = 0; j < links_num; j++) {
                // rutas de los datos
                String pathToData = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWeka/link" + j + ".arff";

                // se cargan los datos de entrenamiento y test
                Instances data_set = new Instances(new BufferedReader(new FileReader(pathToData)));
                Instances training_set = new Instances(data_set, 0, 288);


                // Se crea un nuevo predictor
                WekaForecaster forecaster = new WekaForecaster();

                // Se elije que queremos predecir, en este caso el Link j-ésimo
                forecaster.setFieldsToForecast("Link" + j);


                //String[] opciones={"-R"};
                REPTree predictor = new REPTree();
                //predictor.setOptions(opciones);
                forecaster.setBaseForecaster(predictor);


                forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
                forecaster.getTSLagMaker().setMinLag(1);
                forecaster.getTSLagMaker().setMaxLag(12);

                // Contruye el modelo
                forecaster.buildForecaster(training_set, System.out);

                double[] error_enlace = new double[288];
                for (int k = 0; k < 288; k++) {
                    // prime the forecaster with enough recent historical data
                    // to cover up to the maximum lag. In our case, we could just supply
                    // the 12 most recent historical instances, as this covers our maximum
                    // lag period
                    forecaster.primeForecaster(new Instances(data_set, 276+ k, 12));
                    // Predice 1 unidad desde el fin del conjunto de datos con los que ha alimentado al predictor
                    List<List<NumericPrediction>> forecast = forecaster.forecast(1, System.out);

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
                    MAEModule calculoMae = new MAEModule();
                    calculoMae.setTargetFields(fields);
                    List<NumericPrediction> predsAtStep = forecast.get(0);
                    calculoMae.evaluateForInstance(predsAtStep, data_set.get(1728 + k));
                    error = calculoMae.calculateMeasure();
                    error_enlace[k] = error[0];
                    errores_acumulados[k][j]=error_enlace[k];
                    writer.println(errores_acumulados[k][j]);

                }
            }
            writer.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}