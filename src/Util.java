import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Util:
 * @author : Sharath
 * 28/09/2017
 */
public class Util {

    public static Set<String> populateStopWords(String file) {
        if(file == null)
            return  new HashSet<>();
        Set<String> stopWords = new HashSet<>();
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                stopWords.add(line);
            }
        } catch (IOException e) {
            System.out.println("Warning : StopWords.txt not found!!");
        }
        return stopWords;
    }

}
