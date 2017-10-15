import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * NaiveBayes:
 * @author : Sharath
 * 26/09/2017
 */
public class NaiveBayes {
    private HashMap<String, Double> spamCP;
    private HashMap<String, Double> hamCP;
    private DataSet dataSet;

    NaiveBayes(String spamFolder, String hamFolder, String stopWordFile) {
        dataSet = new DataSet(stopWordFile);
        dataSet.populateDataSet(spamFolder, hamFolder);
    }


    public void trainMultiNominalNB() {
        spamCP = new HashMap<>();
        hamCP = new HashMap<>();
        //For each word in the vocabulary find conditional probability
        for (String word : dataSet.getVocabulary()) {
            spamCP.put(word, getConditionalProb(word, true));
            hamCP.put(word, getConditionalProb(word, false));
        }
    }

    /**
     * Calculates the conditional probability of a word in the given class
     * @param word   :
     * @param isSpam : is the class spam or ham
     * @return : P(word| Y= SPAM) or P( word | Y = HAM )
     */
    private double getConditionalProb(String word, boolean isSpam) {
        long wordFreq, totalWordCount;
        if (isSpam) {
            wordFreq = dataSet.spamTable.getOrDefault(word, 0L) + 1;
            totalWordCount = dataSet.getSpamTotalWordCount();
        } else {
            wordFreq = dataSet.hamTable.getOrDefault(word, 0L) + 1;
            totalWordCount = dataSet.getHamTotalWordCount();
        }
        long denominator = dataSet.getVocabulary().size() + totalWordCount;
        return Math.log((double) wordFreq / denominator);
    }


    private double getProbability(Map<String, Long> wordMap, boolean isSpam) {
        HashMap<String, Double> probabilityClass;
        double probability;
        if (isSpam) {
            probabilityClass = spamCP;
            probability = dataSet.getSpamPriorProb();
        } else {
            probabilityClass = hamCP;
            probability = dataSet.getHamPriorProb();
        }
        for (String word : wordMap.keySet()) {
            if (probabilityClass.containsKey(word))
                //Sum of all the log likelihood probabilities
                //multiplied by the number of times the word occurs in the document
                probability += probabilityClass.get(word) * wordMap.get(word);
        }
        return probability;
    }

    //Returns the class label based on which class has highest probability
    private int applyMultinomialNB(File file) {
        Map<String, Long> wordMap = new HashMap<>();
        dataSet.parseDocument(file, wordMap);
        return getProbability(wordMap, true) > getProbability(wordMap, false) ? 1 : 0;
    }

    //Given spam and ham folder finds the accuracy of the NB
    public float findAccuracy(String spamFolder, String hamFolder) {
        File spamDirectory = new File(spamFolder);
        File hamDirectory = new File(hamFolder);
        File[] spamFiles = spamDirectory.listFiles();
        File[] hamFiles = hamDirectory.listFiles();
        if(spamFiles==null || hamFiles == null){
            System.out.println("Invalid input folders please check the path again");
            return 0;
        }
        long total = spamFiles.length + hamFiles.length;
        long hit = 0;
        for (File f : spamFiles) {
            if (applyMultinomialNB(f) == 1)
                hit++;
        }
        for (File f : hamFiles) {
            if (applyMultinomialNB(f) == 0)
                hit++;
        }
        return (float) hit / total * 100;
    }
    public static void main(String[] args) {
        if(args.length < 5)
        {
            System.out.println("To run the program use Java NaiveBayes <spam folder path> <ham folder path> " +
                    "<stopWordsFile path> <spam test folder path> <Ham test folder path>");
        }
        NaiveBayes naiveBayes = new NaiveBayes(args[0], args[1], args[2]);
        naiveBayes.trainMultiNominalNB();
        System.out.println("Accuracy with stop words :" + naiveBayes.findAccuracy(args[3], args[4]));

        NaiveBayes naiveBayes2 = new NaiveBayes(args[0], args[1], null);
        naiveBayes2.trainMultiNominalNB();
        System.out.println("Accuracy without stop words :" + naiveBayes2.findAccuracy(args[3], args[4]));
    }

}
