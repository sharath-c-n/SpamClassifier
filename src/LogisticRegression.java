import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * LogisticRegression:
 * @author : Sharath
 * 28/09/2017
 */
public class LogisticRegression {
    private Map<String, Double> weights;
    private double learningRate;
    private double lambda;
    private double initValue;

    public LogisticRegression(double learningRate, double lambda, double initValue) {
        this.initValue = initValue;
        this.learningRate = learningRate;
        this.lambda = lambda;
        weights = new HashMap<>();
    }

    LogisticRegression() {
        this(0.001,100,1);
    }

    public LogisticRegression(double learningRate, double lambda) {
        this(learningRate,lambda,1);
    }


    /**
     * Calculates the sigmoid value of the given training document
     *
     * @param trainingData : list of words and their count in the documents
     * @return : sigmoid value of summation of features and their weights.
     */
    public double sigmoid(Map<String, Integer> trainingData) {
        //sum = w0
        double sum = weights.get(null);
        //sum = w0 + summation(xi*wi)
        for (String feature : trainingData.keySet()) {
            Double wi = weights.get(feature); //gets the weight wi
            int xi = trainingData.get(feature); //gets the count of word xi
            if (wi != null) {
                sum += xi * wi;
            }
        }
        //return sigmoid(x) = 1/1+e^(-x)
        return 1 / (1 + Math.exp(-sum));
    }


    /**
     * Does a gradient ascent to find optimal parameter weights
     * @param hamTrainingSet  : ham data set
     * @param spamTrainingSet : spam data set
     * @param iterations      : number od iterations
     */
    public void train(TrainingSet hamTrainingSet, TrainingSet spamTrainingSet, int iterations) {
        weights = createParameterVector(hamTrainingSet, spamTrainingSet, initValue);
        HashMap<String,Double> deltaWi = new HashMap<>();
        while (iterations > 0) {
            //Error vector, i.e calculate (Td - Od) vector for all training data
            calculateDelta(deltaWi,hamTrainingSet);
            calculateDelta(deltaWi,spamTrainingSet);

            //update each weights/parameter with corrected value
            for (String word : weights.keySet()) {
                double wi = weights.get(word);
                //Wi = Wi+ learningRate * summation (Td - Od)*Xi - learningRate * lambda * Wi
                wi = wi + learningRate * (deltaWi.get(word) - lambda * wi);
                weights.put(word, wi);
            }
            iterations--;
        }
    }

    /**
     * Calculates the delta vector for the given data set
     * @param deltaWi : the destination for the weights
     * @param trainingSet :
     */
    private void calculateDelta(HashMap<String, Double> deltaWi, TrainingSet trainingSet) {
        double error;
        for (Map<String, Integer> document : trainingSet) {
            //required output - produced output, i.e (Td - Od)
            error = (trainingSet.getClassLabel() - sigmoid(document));
            for (String word : document.keySet()) {
                // Summation ( Xi * (Td - Od) )
                double delta = (document.get(word) * error) + deltaWi.getOrDefault(word,0D);
                deltaWi.put(word, delta);
            }
        }
    }

    //Creates the initial weight vector
    private Map<String, Double> createParameterVector(TrainingSet hamTrainingSet, TrainingSet spamTrainingSet, double initVal) {
        HashMap<String, Double> parameters = new HashMap<>();
        initializeWeights(parameters, hamTrainingSet, initVal);
        initializeWeights(parameters, spamTrainingSet, initVal);
        return parameters;
    }

    //initializes weights to desired value
    private void initializeWeights(HashMap<String, Double> vector, TrainingSet trainingSet, double initVal) {
        vector.put(null, initVal);
        for (Map<String, Integer> dataRow : trainingSet) {
            for (String word : dataRow.keySet()) {
                vector.put(word, initVal);
            }
        }
    }

    public int predict(TrainingSet testSet) {
        int hit = 0;
        for (Map<String, Integer> dataRow : testSet) {
            double output = sigmoid(dataRow);
            if ((output > 0.5 && testSet.classLabel == 1) ||
                    (output < 0.5 && testSet.classLabel == 0)) {
                hit++;
            }
        }
        return hit;
    }

    public double getAccuracy(TrainingSet hamSet, TrainingSet spamSet) {
        return ((double) predict(hamSet) + predict(spamSet)) /
                (hamSet.getSize() + spamSet.getSize()) * 100;
    }

    public static void main(String[] args) {
        if(args.length < 5)
        {
            System.out.println("To run the program use Java LogisticRegression" +
                    " <spam folder path> <ham folder path> <stopWordsFile path> " +
                    "<spam test folder path> <Ham test folder path>");
        }
        Set<String> stopWords = Util.populateStopWords(args[2]);
        TrainingSet spam = new TrainingSet(0, args[0],stopWords);
        TrainingSet ham = new TrainingSet(1, args[1],stopWords);
        LogisticRegression lr = new LogisticRegression(0.001,100,1);
        lr.train(ham, spam, 1000);
        TrainingSet testSpam = new TrainingSet(0, args[3],stopWords);
        TrainingSet testHam = new TrainingSet(1, args[4],stopWords);
        System.out.println("Accuracy with stop words:" + lr.getAccuracy(testHam, testSpam));
       // System.out.println("Accuracy of ham:" + (double) lr.predict(testHam) / testHam.getSize() * 100);
        //System.out.println("Accuracy of spam:" + (double) lr.predict(testSpam) / testSpam.getSize() * 100);

        //Without stop words
        TrainingSet spam2 = new TrainingSet(0, args[0],new HashSet<>());
        TrainingSet ham2 = new TrainingSet(1, args[1],new HashSet<>());
        LogisticRegression lr2 = new LogisticRegression(0.001,100,1);
        lr2.train(ham2, spam2, 1000);
        System.out.println("Accuracy without stop words :" + lr2.getAccuracy(testHam, testSpam));
    }
}
