import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DataSet: Holds the input data from two folders
 * @author : Sharath
 * 26/09/2017
 */
public class DataSet {
    public Map<String, Long> spamTable;
    public Map<String, Long> hamTable;
    private double spamPriorProb;
    private double hamPriorProb;
    private Set<String> stopWords;
    private Set<String> vocabulary;
    private long spamTotalWordCount;
    private long hamTotalWordCount;

    public DataSet(String stopWordFile) {
        vocabulary = new HashSet<>();
        spamTotalWordCount = 0;
        hamTotalWordCount = 0;
        if (stopWordFile != null)
            stopWords =  Util.populateStopWords(stopWordFile);
        else
            stopWords = new HashSet<>();
    }

    public void populateDataSet(String spamFolder, String hamFolder) {
        if (spamFolder != null && hamFolder != null) {
            spamTable = new HashMap<>();
            hamTable = new HashMap<>();
            File spamDirectory = new File(spamFolder);
            File hamDirectory = new File(hamFolder);
            if (spamDirectory.isDirectory() && hamDirectory.isDirectory()) {
                File[] spamFiles = spamDirectory.listFiles();
                File[] hamFiles = hamDirectory.listFiles();

                int spamCount = spamFiles == null ? 0 : spamFiles.length;
                int hamCount = hamFiles == null ? 0 : hamFiles.length;

                spamPriorProb = Math.log((double) spamCount / (spamCount + hamCount));
                hamPriorProb = Math.log((double) hamCount / (spamCount + hamCount));
                spamTotalWordCount = parseDirectory(spamFiles, spamTable);
                hamTotalWordCount = parseDirectory(hamFiles, hamTable);
            } else {
                System.out.println("Not a folder");
            }

        }
    }

    public long parseDirectory(File[] files, Map<String, Long> dataMap) {
        long wordCount = 0;
        if (files != null) {
            for (File document : files) {
                wordCount += parseDocument(document, dataMap);
            }
        }
        return wordCount;
    }

    public long parseDocument(File doc, Map<String, Long> dataMap) {
        BufferedReader br;
        String line;
        long wordCount = 0;
        try {
            br = new BufferedReader(new FileReader(doc));
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z]"," ");
                String[] words = line.split(" ");
                for (String word : words) {
                    if (!isStopWord(word)) {
                        dataMap.put(word, dataMap.getOrDefault(word,0L)+1);
                        wordCount++;
                        vocabulary.add(word);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning : failed to read from file :" + doc.getAbsolutePath());
        }
        return wordCount;
    }

    private boolean isStopWord(String word) {
        return word != null && stopWords.contains(word);
    }

    public Set<String> getSpamTerms() {
        return spamTable.keySet();
    }

    double getSpamPriorProb() {
        return spamPriorProb;
    }

    double getHamPriorProb() {
        return hamPriorProb;
    }

    long getSpamTotalWordCount() {
        return spamTotalWordCount;
    }

    long getHamTotalWordCount() {
        return hamTotalWordCount;
    }

    Set<String> getVocabulary() {
        return vocabulary;
    }
}
