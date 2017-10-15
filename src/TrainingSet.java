import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TrainingSet:
 * @author : Sharath
 * 28/09/2017
 */
public class TrainingSet implements Iterable<Map<String,Integer>> {
    int classLabel;
    private List<Map<String,Integer>> data;
    private Set<String> stopWords;


    public TrainingSet(int classLabel,String folderPath, String stopWordsFile) {
        this.classLabel = classLabel;
        if(stopWordsFile !=null)
            stopWords = Util.populateStopWords(stopWordsFile);
        else
            stopWords = new HashSet<>();
        populateDataset(folderPath);
    }

    public TrainingSet(int classLabel,String folderPath, Set<String> stopWords) {
        this.classLabel = classLabel;
        this.stopWords = stopWords;
        populateDataset(folderPath);
    }

    public void populateDataset(String folderPath) {
        if (folderPath != null ) {
            File directory = new File(folderPath);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                data = new ArrayList<>();
                if (files != null) {
                    for (File document : files) {
                        data.add(parseDocument(document));
                    }
                }
            } else {
                System.out.println("Not a folder");
            }

        }
    }

    public HashMap<String,Integer> parseDocument(File doc) {
        HashMap<String,Integer> dataMap = new HashMap<>();
        //Adding a null value for X0, so that we can estimate W0 using X0 = 1
        dataMap.put(null,1);
        BufferedReader br;
        String line;
        try {
            br = new BufferedReader(new FileReader(doc));
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z]"," ");
                String[] words = line.split(" ");
                for (String word : words) {
                    if (!stopWords.contains(word)) {
                        dataMap.put(word, dataMap.getOrDefault(word,0)+ 1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Warning : failed to read from file :" + doc.getAbsolutePath());
        }
        return dataMap;
    }

    @Override
    public Iterator<Map<String, Integer>> iterator() {
        return data.iterator();
    }

    public int getClassLabel() {
        return classLabel;
    }

    public int getSize(){
        return  data.size();
    }
}
