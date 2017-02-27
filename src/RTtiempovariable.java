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

import static java.lang.Double.isNaN;

/**
 * Created by carloscharx on 02/02/2017.
 */
public class RTtiempovariable {

     /* Example of using the time series forecasting API. To compile and
     * run the CLASSPATH will need to contain:
     *
     * weka.jar (from your weka distribution)
     * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
     * jcommon-1.0.14.jar (from the time series package lib directory)
     * jfreechart-1.0.13.jar (from the time series package lib directory)
     */


    public static void main(String[] args) {
        try {
            int links_num = 979; // Número de enlaces final obtenido
            int instances_total = 2016;
            int instances_num = 2004; // 1 día de test
            double[][] errores_acumulados = new double[instances_num][links_num];

            PrintWriter writer = new PrintWriter("C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/resultadosRT1horatrain" + ".txt");
            for (int j = 0; j < links_num; j++) {
                // rutas de los datos
                String pathToData = "C:/Users/carloscharx/Documentos/Teleco/4º Teleco/Prácticas y TFG/datos-Funkfeuer-CONFINE/datosWeka/link" + j + ".arff";

                // se cargan los datos de entrenamiento y test
                Instances data_set = new Instances(new BufferedReader(new FileReader(pathToData)));
                Instances training_set = new Instances(data_set, 0, instances_total-instances_num);


                // Se crea un nuevo predictor
                WekaForecaster forecaster = new WekaForecaster();

                // Se elije que queremos predecir, en este caso el Link j-ésimo
                forecaster.setFieldsToForecast("Link" + j);


                REPTree predictor = new REPTree();
                forecaster.setBaseForecaster(predictor);


                forecaster.getTSLagMaker().setTimeStampField("timestamp"); // date time stamp
                forecaster.getTSLagMaker().setMinLag(1);
                forecaster.getTSLagMaker().setMaxLag(12);
                forecaster.getTSLagMaker().setIncludePowersOfTime(false);
                forecaster.getTSLagMaker().setIncludeTimeLagProducts(false);


                // Contruye el modelo
                forecaster.buildForecaster(training_set, System.out);

                double[] error_enlace = new double[instances_num];
                for (int k = 0; k < instances_num; k++) {
                    // prime the forecaster with enough recent historical data
                    // to cover up to the maximum lag. In our case, we could just supply
                    // the 12 most recent historical instances, as this covers our maximum
                    // lag period
                    forecaster.primeForecaster(new Instances(data_set, (instances_total-instances_num-12) + k, 12));
                    // Predice 1 unidad desde el fin del conjunto de datos con los que ha alimentado al predictor
                    List<List<NumericPrediction>> forecast = forecaster.forecast(1, System.out);



                    double[] error;

                    List<String> fields = new ArrayList();
                    fields.add("Link" + j);
                    MAEModule calculoMae = new MAEModule();
                    calculoMae.setTargetFields(fields);
                    List<NumericPrediction> predsAtStep = forecast.get(0);
                    calculoMae.evaluateForInstance(predsAtStep, data_set.get((instances_total-instances_num) + k));
                    error = calculoMae.calculateMeasure();
                    error_enlace[k] = error[0];
                    errores_acumulados[k][j]=error[0];

                }



            }
            double[] evolucion_error = new double[instances_num];
            for (int w = 0; w < instances_num; w++) {
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

